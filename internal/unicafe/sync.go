package unicafe

import (
	"context"
	"errors"
	"log"
	"strings"
	"time"

	"github.com/JaniL/unirater/internal/store"
)

// Repo is the persistence surface the sync needs. *store.Store
// implements it.
type Repo interface {
	CreateRestaurant(ctx context.Context, name string, areacode int) (*store.Restaurant, error)
	FoodByNameAndRestaurant(ctx context.Context, name string, restaurantID int64) (*store.Food, error)
	CreateFood(ctx context.Context, f store.Food) (*store.Food, error)
	MenuExistsForDate(ctx context.Context, restaurantID int64, date time.Time) (bool, error)
	CreateMenu(ctx context.Context, restaurantID int64, date time.Time, foodIDs []int64) (int64, error)
	PriceByName(ctx context.Context, name string) (*store.Price, error)
	CreatePrice(ctx context.Context, p store.Price) (*store.Price, error)
}

// SyncAll saves every menu of the feed into the repository. It is a port
// of LounastyokaluService#saveAllToRepo: restaurants are find-or-created
// by name, menus are deduplicated per restaurant and date, foods are
// find-or-created per name and restaurant, and prices are deduplicated
// by name.
func SyncAll(ctx context.Context, items []FeedItem, repo Repo) error {
	for _, item := range items {
		if err := ctx.Err(); err != nil {
			return err
		}

		resp := item.MenuData
		if resp == nil {
			continue
		}

		name := restaurantName(item)
		if name == "" {
			continue
		}

		areacode := 0
		if resp.Areacode != nil {
			areacode = *resp.Areacode
		}

		restaurant, err := repo.CreateRestaurant(ctx, name, areacode)
		if err != nil {
			return err
		}

		if err := syncMenus(ctx, resp.MenuList(), restaurant.ID, repo); err != nil {
			return err
		}
	}
	return nil
}

func syncMenus(ctx context.Context, menus []Menu, restaurantID int64, repo Repo) error {
	for _, menu := range menus {
		if err := ctx.Err(); err != nil {
			return err
		}

		dateString, ok := menu.EffectiveDate()
		if !ok {
			continue
		}
		date, ok := ParseDate(dateString)
		if !ok {
			log.Printf("unicafe: skipping menu with unparsable date %q", dateString)
			continue
		}

		exists, err := repo.MenuExistsForDate(ctx, restaurantID, date)
		if err != nil {
			return err
		}
		if exists {
			continue
		}

		foodIDs, err := syncFoods(ctx, menu.Data, restaurantID, date, repo)
		if err != nil {
			return err
		}
		if len(foodIDs) == 0 {
			continue
		}

		if _, err := repo.CreateMenu(ctx, restaurantID, date, foodIDs); err != nil {
			if errors.Is(err, store.ErrNotFound) {
				continue // menu appeared meanwhile; dedupe like the Java version
			}
			return err
		}
	}
	return nil
}

func syncFoods(ctx context.Context, foods []FoodDetails, restaurantID int64, date time.Time, repo Repo) ([]int64, error) {
	var ids []int64
	seen := map[int64]bool{}

	for _, details := range foods {
		if err := ctx.Err(); err != nil {
			return nil, err
		}

		name := strings.TrimSpace(details.Name)
		if name == "" {
			continue
		}

		food, err := repo.FoodByNameAndRestaurant(ctx, name, restaurantID)
		if errors.Is(err, store.ErrNotFound) {
			price, err := resolvePrice(ctx, details.Price, repo)
			if err != nil {
				return nil, err
			}

			var priceID *int64
			if price != nil {
				id := price.ID
				priceID = &id
			}

			food, err = repo.CreateFood(ctx, store.Food{
				Name:           name,
				PriceID:        priceID,
				LastSeenOnMenu: &date,
				RestaurantID:   restaurantID,
			})
			if err != nil {
				return nil, err
			}
		} else if err != nil {
			return nil, err
		}

		if !seen[food.ID] {
			seen[food.ID] = true
			ids = append(ids, food.ID)
		}
	}
	return ids, nil
}

// resolvePrice ports LounastyokaluService#resolvePrice: existing price
// categories are reused, new ones are persisted with their value.
func resolvePrice(ctx context.Context, price *Price, repo Repo) (*store.Price, error) {
	if price == nil || price.Name == "" {
		return nil, nil
	}

	existing, err := repo.PriceByName(ctx, price.Name)
	if err == nil {
		return existing, nil
	}
	if !errors.Is(err, store.ErrNotFound) {
		return nil, err
	}

	var value *store.PriceValue
	if price.Value != nil {
		value = &store.PriceValue{
			Contract:    price.Value.Contract.String(),
			Graduate:    price.Value.Graduate.String(),
			GraduateHYY: price.Value.GraduateHYY.String(),
			Normal:      price.Value.Normal.String(),
			Student:     price.Value.Student.String(),
			StudentHYY:  price.Value.StudentHYY.String(),
		}
	}

	return repo.CreatePrice(ctx, store.Price{Name: price.Name, Value: value})
}

// restaurantName resolves the restaurant name of a feed item, preferring
// the menu data name over the item title, and dropping empty values.
func restaurantName(item FeedItem) string {
	name := ""
	if item.MenuData != nil && item.MenuData.Name != nil {
		name = *item.MenuData.Name
	}
	if strings.TrimSpace(name) == "" {
		name = item.Title
	}
	if strings.TrimSpace(name) == "" {
		return ""
	}
	return name
}
