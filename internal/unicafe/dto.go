// Package unicafe integrates with the UniCafe restaurants API
// (https://unicafe.fi/wp-json/swiss/v1/restaurants/?lang=fi) and ports the
// menu ingestion of the original LounastyokaluService.
package unicafe

import (
	"encoding/json"
	"fmt"
	"strconv"
)

// FeedItem is one entry of the restaurants feed.
type FeedItem struct {
	ID       int64               `json:"id"`
	Title    string              `json:"title"`
	Slug     string              `json:"slug"`
	MenuData *RestaurantResponse `json:"menuData"`
}

// RestaurantResponse is the menu data embedded in each feed item.
type RestaurantResponse struct {
	Status   *string `json:"status"`
	ID       *int64  `json:"id"`
	Name     *string `json:"name"`
	Areacode *int    `json:"areacode"`
	Data     []Menu  `json:"data"`
	Menus    []Menu  `json:"menus"`
}

// MenuList returns the menus carried by the response, preferring the
// "data" field, mirroring RestaurantResponse#getData.
func (r *RestaurantResponse) MenuList() []Menu {
	if r.Data != nil {
		return r.Data
	}
	return r.Menus
}

// Menu is one day's menu of a restaurant.
type Menu struct {
	Data    []FoodDetails `json:"data"`
	Date    string        `json:"date"`
	DateEN  string        `json:"date_en"`
	DateSV  string        `json:"date_sv"`
	Message string        `json:"message"`
}

// EffectiveDate returns the parsed date of the menu, preferring the
// Finnish, then English, then Swedish date field, mirroring
// MenuOfTheDay#getDate.
func (m *Menu) EffectiveDate() (date string, ok bool) {
	for _, candidate := range []string{m.Date, m.DateEN, m.DateSV} {
		if candidate != "" {
			return candidate, true
		}
	}
	return "", false
}

// FoodDetails is one dish on a menu.
type FoodDetails struct {
	Ingredients string `json:"ingredients"`
	Name        string `json:"name"`
	NameEN      string `json:"name_en"`
	NameSV      string `json:"name_sv"`
	Nutrition   string `json:"nutrition"`
	Price       *Price `json:"price"`
}

// Price is the price category of a dish.
type Price struct {
	Name  string      `json:"name"`
	Value *PriceValue `json:"value"`
}

// UnmarshalJSON mirrors the Java @JsonSetter("value") of the original
// Price class: the API sometimes serves value as an array (empty, for
// non-meal entries such as "Tiedote") or as a scalar instead of an
// object. Anything that is not an object becomes a nil value.
func (p *Price) UnmarshalJSON(data []byte) error {
	var raw struct {
		Name  string          `json:"name"`
		Value json.RawMessage `json:"value"`
	}
	if err := json.Unmarshal(data, &raw); err != nil {
		return err
	}
	p.Name = raw.Name
	p.Value = nil

	if len(raw.Value) == 0 || string(raw.Value) == "null" {
		return nil
	}

	var value PriceValue
	if err := json.Unmarshal(raw.Value, &value); err != nil {
		return nil // arrays and scalars carry no usable value
	}
	p.Value = &value
	return nil
}

// PriceValue holds the per-group prices. The API serves them either as
// strings or numbers, so flexText accepts both (the Java version read
// them via JsonNode#asText).
type PriceValue struct {
	Contract    flexText `json:"contract"`
	Graduate    flexText `json:"graduate"`
	GraduateHYY flexText `json:"graduate_hyy"`
	Normal      flexText `json:"normal"`
	Student     flexText `json:"student"`
	StudentHYY  flexText `json:"student_hyy"`
}

type flexText string

func (f *flexText) String() string { return string(*f) }

// UnmarshalJSON accepts JSON strings, numbers and nulls.
func (f *flexText) UnmarshalJSON(b []byte) error {
	var v any
	if err := json.Unmarshal(b, &v); err != nil {
		return err
	}
	switch t := v.(type) {
	case nil:
		*f = ""
	case string:
		*f = flexText(t)
	case float64:
		*f = flexText(strconv.FormatFloat(t, 'f', -1, 64))
	default:
		*f = flexText(fmt.Sprint(t))
	}
	return nil
}
