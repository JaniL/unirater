// Package store implements the persistence layer of UniRater on
// PostgreSQL, replacing the Spring Data JPA repositories of the original
// application. It uses pgx.
package store

import (
	"context"
	_ "embed"
	"errors"
	"fmt"
	"time"

	"github.com/jackc/pgerrcode"
	"github.com/jackc/pgx/v5"
	"github.com/jackc/pgx/v5/pgconn"
	"github.com/jackc/pgx/v5/pgxpool"

	"github.com/JaniL/unirater/internal/slug"
)

//go:embed schema.sql
var schemaSQL string

// Store wraps a pgx connection pool.
type Store struct {
	pool *pgxpool.Pool
}

// ErrNotFound is returned when a single-row lookup has no match.
var ErrNotFound = errors.New("store: not found")

// ErrDuplicateEmail is returned by CreateUser when the email is taken.
var ErrDuplicateEmail = errors.New("store: duplicate email")

// Open creates a connection pool for the given database URL and pings it.
func Open(ctx context.Context, databaseURL string) (*Store, error) {
	cfg, err := pgxpool.ParseConfig(databaseURL)
	if err != nil {
		return nil, fmt.Errorf("store: parsing database URL: %w", err)
	}
	cfg.MaxConns = 4
	cfg.MinConns = 0

	pool, err := pgxpool.NewWithConfig(ctx, cfg)
	if err != nil {
		return nil, fmt.Errorf("store: creating pool: %w", err)
	}
	if err := pool.Ping(ctx); err != nil {
		pool.Close()
		return nil, fmt.Errorf("store: pinging database: %w", err)
	}

	return &Store{pool: pool}, nil
}

// Close releases the pool.
func (s *Store) Close() { s.pool.Close() }

// Migrate applies the embedded schema.
func (s *Store) Migrate(ctx context.Context) error {
	if _, err := s.pool.Exec(ctx, schemaSQL); err != nil {
		return fmt.Errorf("store: applying schema: %w", err)
	}
	return nil
}

// Restaurants ----------------------------------------------------------------

// ListRestaurants returns every restaurant, ordered by name.
func (s *Store) ListRestaurants(ctx context.Context) ([]Restaurant, error) {
	rows, err := s.pool.Query(ctx,
		`SELECT id, name, uri, areacode FROM restaurant ORDER BY name`)
	if err != nil {
		return nil, err
	}
	defer rows.Close()

	var out []Restaurant
	for rows.Next() {
		var r Restaurant
		if err := rows.Scan(&r.ID, &r.Name, &r.URI, &r.Areacode); err != nil {
			return nil, err
		}
		out = append(out, r)
	}
	return out, rows.Err()
}

// RestaurantByName finds a restaurant by exact name.
func (s *Store) RestaurantByName(ctx context.Context, name string) (*Restaurant, error) {
	return s.restaurantWhere(ctx, `SELECT id, name, uri, areacode FROM restaurant WHERE name = $1`, name)
}

// RestaurantByURI finds a restaurant by its URI slug.
func (s *Store) RestaurantByURI(ctx context.Context, uri string) (*Restaurant, error) {
	return s.restaurantWhere(ctx, `SELECT id, name, uri, areacode FROM restaurant WHERE uri = $1`, uri)
}

func (s *Store) restaurantWhere(ctx context.Context, query string, arg any) (*Restaurant, error) {
	row := s.pool.QueryRow(ctx, query, arg)

	var r Restaurant
	err := row.Scan(&r.ID, &r.Name, &r.URI, &r.Areacode)
	if errors.Is(err, pgx.ErrNoRows) {
		return nil, ErrNotFound
	}
	if err != nil {
		return nil, err
	}
	return &r, nil
}

// CreateRestaurant inserts a new restaurant and derives its URI slug,
// exactly like Restaurant#setName did in the Java application.
// It returns the existing restaurant when the name is already present.
func (s *Store) CreateRestaurant(ctx context.Context, name string, areacode int) (*Restaurant, error) {
	uri := slug.URI(name)

	var r Restaurant
	err := s.pool.QueryRow(ctx, `
		INSERT INTO restaurant (name, uri, areacode)
		VALUES ($1, $2, $3)
		ON CONFLICT (name) DO UPDATE SET areacode = EXCLUDED.areacode
		RETURNING id, name, uri, areacode`,
		name, uri, areacode,
	).Scan(&r.ID, &r.Name, &r.URI, &r.Areacode)
	if err != nil {
		return nil, fmt.Errorf("store: creating restaurant %q: %w", name, err)
	}
	return &r, nil
}

// Food -------------------------------------------------------------------------

// FoodByNameAndRestaurant finds a food by name within a restaurant.
func (s *Store) FoodByNameAndRestaurant(ctx context.Context, name string, restaurantID int64) (*Food, error) {
	row := s.pool.QueryRow(ctx, `
		SELECT id, name, price_id, total, last_seen_on_menu, restaurant_id
		FROM food
		WHERE name = $1 AND restaurant_id = $2`,
		name, restaurantID)

	var f Food
	err := row.Scan(&f.ID, &f.Name, &f.PriceID, &f.Total, &f.LastSeenOnMenu, &f.RestaurantID)
	if errors.Is(err, pgx.ErrNoRows) {
		return nil, ErrNotFound
	}
	if err != nil {
		return nil, err
	}
	return &f, nil
}

// CreateFood inserts a new food.
func (s *Store) CreateFood(ctx context.Context, f Food) (*Food, error) {
	row := s.pool.QueryRow(ctx, `
		INSERT INTO food (name, price_id, total, last_seen_on_menu, restaurant_id)
		VALUES ($1, $2, $3, $4, $5)
		RETURNING id, name, price_id, total, last_seen_on_menu, restaurant_id`,
		f.Name, f.PriceID, f.Total, f.LastSeenOnMenu, f.RestaurantID)

	var out Food
	err := row.Scan(&out.ID, &out.Name, &out.PriceID, &out.Total, &out.LastSeenOnMenu, &out.RestaurantID)
	if err != nil {
		return nil, fmt.Errorf("store: creating food %q: %w", f.Name, err)
	}
	return &out, nil
}

// Price ------------------------------------------------------------------------

// PriceByName finds a price category by name.
func (s *Store) PriceByName(ctx context.Context, name string) (*Price, error) {
	row := s.pool.QueryRow(ctx, `
		SELECT p.id, p.name, v.id,
		       COALESCE(v.contract, ''), COALESCE(v.graduate, ''), COALESCE(v.graduate_hyy, ''),
		       COALESCE(v.normal, ''), COALESCE(v.student, ''), COALESCE(v.student_hyy, '')
		FROM price p
		LEFT JOIN price_value v ON v.id = p.value_id
		WHERE p.name = $1`, name)

	var p Price
	var valueID *int64
	var value PriceValue
	err := row.Scan(&p.ID, &p.Name, &valueID,
		&value.Contract, &value.Graduate, &value.GraduateHYY,
		&value.Normal, &value.Student, &value.StudentHYY)
	if errors.Is(err, pgx.ErrNoRows) {
		return nil, ErrNotFound
	}
	if err != nil {
		return nil, err
	}
	if valueID != nil {
		value.ID = *valueID
		p.Value = &value
	}
	return &p, nil
}

// CreatePrice persists a new price category together with its value.
// If a price with the same name already exists it is returned unchanged,
// mirroring LounastyokaluService#resolvePrice.
func (s *Store) CreatePrice(ctx context.Context, p Price) (*Price, error) {
	tx, err := s.pool.Begin(ctx)
	if err != nil {
		return nil, err
	}
	defer tx.Rollback(ctx)

	var valueID *int64
	if p.Value != nil {
		err = tx.QueryRow(ctx, `
			INSERT INTO price_value (contract, graduate, graduate_hyy, normal, student, student_hyy)
			VALUES ($1, $2, $3, $4, $5, $6)
			RETURNING id`,
			p.Value.Contract, p.Value.Graduate, p.Value.GraduateHYY,
			p.Value.Normal, p.Value.Student, p.Value.StudentHYY,
		).Scan(&valueID)
		if err != nil {
			return nil, err
		}
	}

	var id int64
	err = tx.QueryRow(ctx, `
		INSERT INTO price (name, value_id) VALUES ($1, $2)
		ON CONFLICT (name) DO NOTHING
		RETURNING id`,
		p.Name, valueID,
	).Scan(&id)
	if errors.Is(err, pgx.ErrNoRows) {
		// Conflict path: the price existed already. Read it back inside
		// the transaction for consistency.
		var existing Price
		var value PriceValue
		if err := tx.QueryRow(ctx, `
			SELECT p.id, p.name, v.id,
			       COALESCE(v.contract, ''), COALESCE(v.graduate, ''), COALESCE(v.graduate_hyy, ''),
			       COALESCE(v.normal, ''), COALESCE(v.student, ''), COALESCE(v.student_hyy, '')
			FROM price p
			LEFT JOIN price_value v ON v.id = p.value_id
			WHERE p.name = $1`, p.Name,
		).Scan(&existing.ID, &existing.Name, &valueID,
			&value.Contract, &value.Graduate, &value.GraduateHYY,
			&value.Normal, &value.Student, &value.StudentHYY); err != nil {
			return nil, err
		}
		if valueID != nil {
			value.ID = *valueID
			existing.Value = &value
		}
		if err := tx.Commit(ctx); err != nil {
			return nil, err
		}
		return &existing, nil
	}
	if err != nil {
		return nil, err
	}

	out := &Price{ID: id, Name: p.Name}
	if valueID != nil {
		out.Value = &PriceValue{
			ID: *valueID, Contract: p.Value.Contract, Graduate: p.Value.Graduate,
			GraduateHYY: p.Value.GraduateHYY, Normal: p.Value.Normal,
			Student: p.Value.Student, StudentHYY: p.Value.StudentHYY,
		}
	}
	if err := tx.Commit(ctx); err != nil {
		return nil, err
	}
	return out, nil
}

// Menus ------------------------------------------------------------------------

// MenuExistsForDate reports whether a restaurant already has a menu for
// the given date.
func (s *Store) MenuExistsForDate(ctx context.Context, restaurantID int64, date time.Time) (bool, error) {
	var one int
	err := s.pool.QueryRow(ctx,
		`SELECT 1 FROM menu_of_the_day WHERE restaurant_id = $1 AND menu_date = $2`,
		restaurantID, date,
	).Scan(&one)
	if errors.Is(err, pgx.ErrNoRows) {
		return false, nil
	}
	if err != nil {
		return false, err
	}
	return true, nil
}

// CreateMenu inserts a menu with its foods.
func (s *Store) CreateMenu(ctx context.Context, restaurantID int64, date time.Time, foodIDs []int64) (int64, error) {
	tx, err := s.pool.Begin(ctx)
	if err != nil {
		return 0, err
	}
	defer tx.Rollback(ctx)

	var id int64
	err = tx.QueryRow(ctx, `
		INSERT INTO menu_of_the_day (restaurant_id, menu_date)
		VALUES ($1, $2)
		ON CONFLICT DO NOTHING
		RETURNING id`,
		restaurantID, date,
	).Scan(&id)
	if errors.Is(err, pgx.ErrNoRows) {
		// Menu already existed (dedupe per restaurant+date); nothing to do.
		if err := tx.Commit(ctx); err != nil {
			return 0, err
		}
		return 0, ErrNotFound
	}
	if err != nil {
		return 0, err
	}

	for _, foodID := range foodIDs {
		if _, err := tx.Exec(ctx,
			`INSERT INTO food_menu (menu_of_the_day_id, food_id) VALUES ($1, $2)
			 ON CONFLICT DO NOTHING`,
			id, foodID); err != nil {
			return 0, err
		}
	}

	if err := tx.Commit(ctx); err != nil {
		return 0, err
	}
	return id, nil
}

const menuQuery = `
	SELECT m.id, m.restaurant_id, m.menu_date,
	       f.id, f.name, f.total,
	       COALESCE(p.name, '')
	FROM menu_of_the_day m
	JOIN food_menu fm ON fm.menu_of_the_day_id = m.id
	JOIN food f ON f.id = fm.food_id
	LEFT JOIN price p ON p.id = f.price_id`

func (s *Store) queryMenus(ctx context.Context, where string, args ...any) ([]Menu, error) {
	query := menuQuery
	if where != "" {
		query += " WHERE " + where
	}
	query += " ORDER BY m.menu_date, f.id"

	rows, err := s.pool.Query(ctx, query, args...)
	if err != nil {
		return nil, err
	}
	defer rows.Close()

	byID := map[int64]*Menu{}
	var order []int64
	for rows.Next() {
		var (
			menuID, foodID, total int64
			restaurantID          int64
			menuDate              time.Time
			foodName, priceName   string
		)
		if err := rows.Scan(&menuID, &restaurantID, &menuDate, &foodID, &foodName, &total, &priceName); err != nil {
			return nil, err
		}

		menu, ok := byID[menuID]
		if !ok {
			menu = &Menu{ID: menuID, RestaurantID: restaurantID, Date: menuDate}
			byID[menuID] = menu
			order = append(order, menuID)
		}
		menu.Foods = append(menu.Foods, MenuFood{
			Food:      Food{ID: foodID, Name: foodName, Total: int(total), RestaurantID: restaurantID},
			PriceName: priceName,
		})
	}
	if err := rows.Err(); err != nil {
		return nil, err
	}

	out := make([]Menu, 0, len(order))
	for _, id := range order {
		out = append(out, *byID[id])
	}
	return out, nil
}

// MenusByRestaurant returns all menus of a restaurant, newest first.
func (s *Store) MenusByRestaurant(ctx context.Context, restaurantID int64) ([]Menu, error) {
	return s.queryMenus(ctx, "m.restaurant_id = $1", restaurantID)
}

// AllMenus returns every menu in the database.
func (s *Store) AllMenus(ctx context.Context) ([]Menu, error) {
	return s.queryMenus(ctx, "")
}

// CountMenus returns the number of stored menus.
func (s *Store) CountMenus(ctx context.Context) (int64, error) {
	var n int64
	err := s.pool.QueryRow(ctx, `SELECT count(*) FROM menu_of_the_day`).Scan(&n)
	return n, err
}

// Ratings -----------------------------------------------------------------------

// SetRating replaces the user's rating of a food with the given value
// (+1/-1) and recomputes the food's denormalized total, mirroring
// FoodController#postRating.
func (s *Store) SetRating(ctx context.Context, userID, foodID int64, value int) error {
	tx, err := s.pool.Begin(ctx)
	if err != nil {
		return err
	}
	defer tx.Rollback(ctx)

	if _, err := tx.Exec(ctx,
		`DELETE FROM rating WHERE user_id = $1 AND food_id = $2`,
		userID, foodID); err != nil {
		return err
	}

	var rating Rating
	if err := tx.QueryRow(ctx, `
		INSERT INTO rating (rating, rated_at, user_id, food_id)
		VALUES ($1, now(), $2, $3)
		RETURNING id, rating, rated_at, user_id, food_id`,
		value, userID, foodID,
	).Scan(&rating.ID, &rating.Value, &rating.Date, &rating.UserID, &rating.FoodID); err != nil {
		return err
	}

	if _, err := tx.Exec(ctx, `
		UPDATE food SET total = (
			SELECT COALESCE(SUM(rating), 0) FROM rating WHERE food_id = $1
		) WHERE id = $1`,
		foodID); err != nil {
		return err
	}

	return tx.Commit(ctx)
}

// Users -------------------------------------------------------------------------

// UserByEmail finds a user account by email.
func (s *Store) UserByEmail(ctx context.Context, email string) (*User, error) {
	return s.userWhere(ctx, `WHERE email = $1`, email)
}

// UserByID finds a user account by id.
func (s *Store) UserByID(ctx context.Context, id int64) (*User, error) {
	return s.userWhere(ctx, `WHERE id = $1`, id)
}

func (s *Store) userWhere(ctx context.Context, where string, arg any) (*User, error) {
	row := s.pool.QueryRow(ctx, `
		SELECT id, email, first_name, last_name, password, role, created_at, updated_at
		FROM user_accounts `+where, arg)

	var u User
	err := row.Scan(&u.ID, &u.Email, &u.FirstName, &u.LastName, &u.Password, &u.Role, &u.CreatedAt, &u.UpdatedAt)
	if errors.Is(err, pgx.ErrNoRows) {
		return nil, ErrNotFound
	}
	if err != nil {
		return nil, err
	}
	return &u, nil
}

// CreateUser inserts a new user account. Returns ErrDuplicateEmail when the
// email is taken.
func (s *Store) CreateUser(ctx context.Context, u User) (*User, error) {
	var out User
	err := s.pool.QueryRow(ctx, `
		INSERT INTO user_accounts (email, first_name, last_name, password, role)
		VALUES ($1, $2, $3, $4, $5)
		RETURNING id, email, first_name, last_name, password, role, created_at, updated_at`,
		u.Email, u.FirstName, u.LastName, u.Password, u.Role,
	).Scan(&out.ID, &out.Email, &out.FirstName, &out.LastName, &out.Password, &out.Role, &out.CreatedAt, &out.UpdatedAt)
	if err != nil {
		var pgErr *pgconn.PgError
		if errors.As(err, &pgErr) && pgErr.Code == pgerrcode.UniqueViolation {
			return nil, ErrDuplicateEmail
		}
		return nil, fmt.Errorf("store: creating user: %w", err)
	}
	return &out, nil
}
