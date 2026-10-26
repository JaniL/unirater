package store

import "time"

// Restaurant mirrors the restaurant table.
type Restaurant struct {
	ID       int64
	Name     string
	URI      string
	Areacode int
}

// Food mirrors the food table.
type Food struct {
	ID             int64
	Name           string
	PriceID        *int64
	Total          int
	LastSeenOnMenu *time.Time
	RestaurantID   int64
}

// PriceName is resolved during menu queries; kept separate from Food to
// avoid a circular dependency on the price table scan order.
type MenuFood struct {
	Food
	PriceName string
}

// Menu mirrors the menu_of_the_day table together with its foods.
type Menu struct {
	ID           int64
	RestaurantID int64
	Date         time.Time
	Foods        []MenuFood
}

// DateString renders the menu date in the dd.mm.yyyy form used by the UI.
func (m *Menu) DateString() string {
	return m.Date.Format("02.01.2006")
}

// Rating mirrors the rating table.
type Rating struct {
	ID     int64
	Value  int
	Date   time.Time
	UserID int64
	FoodID int64
}

// Price mirrors the price table.
type Price struct {
	ID    int64
	Name  string
	Value *PriceValue
}

// PriceValue mirrors the price_value table.
type PriceValue struct {
	ID          int64
	Contract    string
	Graduate    string
	GraduateHYY string
	Normal      string
	Student     string
	StudentHYY  string
}

// User mirrors the user_accounts table.
type User struct {
	ID        int64
	Email     string
	FirstName string
	LastName  string
	Password  string
	Role      string
	CreatedAt time.Time
	UpdatedAt time.Time
}
