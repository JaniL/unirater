package unicafe

import (
	"context"
	"errors"
	"fmt"
	"testing"
	"time"

	"github.com/JaniL/unirater/internal/store"
)

// fakeRepo records everything SyncAll persists.
type fakeRepo struct {
	restaurants []*store.Restaurant
	foods       map[string]*store.Food // key: restaurantID/name
	menus       map[string]bool        // key: restaurantID/date
	prices      map[string]*store.Price
	nextID      int64
}

func newFakeRepo() *fakeRepo {
	return &fakeRepo{
		foods:  map[string]*store.Food{},
		menus:  map[string]bool{},
		prices: map[string]*store.Price{},
	}
}

func (f *fakeRepo) CreateRestaurant(_ context.Context, name string, areacode int) (*store.Restaurant, error) {
	for _, r := range f.restaurants {
		if r.Name == name {
			r.Areacode = areacode
			return r, nil
		}
	}
	f.nextID++
	f.restaurants = append(f.restaurants, &store.Restaurant{ID: f.nextID, Name: name, URI: name, Areacode: areacode})
	return f.restaurants[len(f.restaurants)-1], nil
}

func (f *fakeRepo) FoodByNameAndRestaurant(_ context.Context, name string, restaurantID int64) (*store.Food, error) {
	if food, ok := f.foods[key(name, restaurantID)]; ok {
		return food, nil
	}
	return nil, store.ErrNotFound
}

func (f *fakeRepo) CreateFood(_ context.Context, food store.Food) (*store.Food, error) {
	f.nextID++
	created := food
	created.ID = f.nextID
	f.foods[key(food.Name, food.RestaurantID)] = &created
	return &created, nil
}

func (f *fakeRepo) MenuExistsForDate(_ context.Context, restaurantID int64, date time.Time) (bool, error) {
	return f.menus[key(restaurantID, date.Format("2006-01-02"))], nil
}

func (f *fakeRepo) CreateMenu(_ context.Context, restaurantID int64, date time.Time, foodIDs []int64) (int64, error) {
	menuKey := key(restaurantID, date.Format("2006-01-02"))
	if f.menus[menuKey] {
		return 0, store.ErrNotFound
	}
	f.menus[menuKey] = true
	f.nextID++
	return f.nextID, nil
}

func (f *fakeRepo) PriceByName(_ context.Context, name string) (*store.Price, error) {
	if p, ok := f.prices[name]; ok {
		return p, nil
	}
	return nil, store.ErrNotFound
}

func (f *fakeRepo) CreatePrice(_ context.Context, p store.Price) (*store.Price, error) {
	f.nextID++
	created := p
	created.ID = f.nextID
	f.prices[p.Name] = &created
	return &created, nil
}

func key(a any, b any) string {
	return fmt.Sprintf("%v/%v", a, b)
}

func strptr(s string) *string { return &s }

func intptr(i int) *int { return &i }

func TestSyncAllCreatesRestaurantsAndMenus(t *testing.T) {
	repo := newFakeRepo()
	feed := []FeedItem{{
		ID:    1,
		Title: "Exactum",
		MenuData: &RestaurantResponse{
			Name:     strptr("Exactum"),
			Areacode: intptr(10),
			Data: []Menu{{
				Date: "24.12.",
				Data: []FoodDetails{
					{Name: "Joulupuu", Price: &Price{Name: "Kevyesti", Value: &PriceValue{Student: "2.95"}}},
					{Name: "Kyrsiä"},
				},
			}},
		},
	}}

	if err := SyncAll(context.Background(), feed, repo); err != nil {
		t.Fatalf("SyncAll: %v", err)
	}

	if len(repo.restaurants) != 1 {
		t.Fatalf("restaurants = %d, want 1", len(repo.restaurants))
	}
	if repo.restaurants[0].Areacode != 10 {
		t.Errorf("areacode = %d, want 10", repo.restaurants[0].Areacode)
	}
	if len(repo.menus) != 1 {
		t.Fatalf("menus = %d, want 1", len(repo.menus))
	}
	if _, ok := repo.prices["Kevyesti"]; !ok {
		t.Error("price Kevyesti missing")
	}
	if len(repo.foods) != 2 {
		t.Errorf("foods = %d, want 2", len(repo.foods))
	}
}

func TestSyncAllDedupesMenusPerRestaurantAndDate(t *testing.T) {
	repo := newFakeRepo()
	item := FeedItem{
		ID:    1,
		Title: "Exactum",
		MenuData: &RestaurantResponse{
			Name: strptr("Exactum"),
			Data: []Menu{{Date: "24.12.", Data: []FoodDetails{{Name: "Kalkkunaa"}}}},
		},
	}

	for i := 0; i < 3; i++ {
		if err := SyncAll(context.Background(), []FeedItem{item}, repo); err != nil {
			t.Fatalf("SyncAll #%d: %v", i, err)
		}
	}
	if len(repo.menus) != 1 {
		t.Errorf("menus after 3 syncs = %d, want 1", len(repo.menus))
	}
}

func TestSyncAllSkipsInvalidAndEmpty(t *testing.T) {
	repo := newFakeRepo()
	feed := []FeedItem{
		{}, // no menu data
		{Title: "Empty", MenuData: &RestaurantResponse{Name: strptr("Empty")}},
		{Title: "Skipped", MenuData: &RestaurantResponse{Name: strptr("Skipped"), Data: []Menu{{Date: "keät"}}}},
	}
	if err := SyncAll(context.Background(), feed, repo); err != nil {
		t.Fatalf("SyncAll: %v", err)
	}
	if len(repo.restaurants) != 2 {
		t.Errorf("restaurants = %d, want 2", len(repo.restaurants))
	}
	if len(repo.menus) != 0 {
		t.Errorf("menus = %d, want 0", len(repo.menus))
	}
}

func TestSyncAllMenuNameFallbackToTitle(t *testing.T) {
	repo := newFakeRepo()
	feed := []FeedItem{{Title: "Terkkari", MenuData: &RestaurantResponse{Data: []Menu{{Date: "1.1.", Data: []FoodDetails{{Name: "Kaalilaatikko"}}}}}}}
	if err := SyncAll(context.Background(), feed, repo); err != nil {
		t.Fatalf("SyncAll: %v", err)
	}
	if repo.restaurants[0].Name != "Terkkari" {
		t.Errorf("restaurant name = %q, want Terkkari", repo.restaurants[0].Name)
	}
}

func TestRestaurantName(t *testing.T) {
	cases := []struct {
		item FeedItem
		want string
	}{
		{FeedItem{Title: "T", MenuData: &RestaurantResponse{Name: strptr("N")}}, "N"},
		{FeedItem{Title: "T", MenuData: &RestaurantResponse{Name: strptr("  ")}}, "T"},
		{FeedItem{Title: "T"}, "T"},
		{FeedItem{}, ""},
	}
	for i, c := range cases {
		if got := restaurantName(c.item); got != c.want {
			t.Errorf("case %d: restaurantName = %q, want %q", i, got, c.want)
		}
	}
}

func TestSyncAllHonorsContextCancel(t *testing.T) {
	repo := newFakeRepo()
	ctx, cancel := context.WithCancel(context.Background())
	cancel()
	feed := []FeedItem{{Title: "A", MenuData: &RestaurantResponse{Name: strptr("A")}}}
	if err := SyncAll(ctx, feed, repo); !errors.Is(err, context.Canceled) {
		t.Errorf("SyncAll with cancelled ctx = %v, want context.Canceled", err)
	}
}
