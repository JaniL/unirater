package unicafe

import (
	"encoding/json"
	"testing"
	"time"
)

func TestParseDatePrimary(t *testing.T) {
	got, ok := ParseDate("4.11.")
	if !ok {
		t.Fatal("ParseDate(\"4.11.\") not ok")
	}
	if got.Day() != 4 || got.Month() != time.November {
		t.Errorf("ParseDate(\"4.11.\") = %v, want Nov 4", got)
	}
	if got.Year() != time.Now().Year() {
		t.Errorf("year = %d, want current year %d", got.Year(), time.Now().Year())
	}
}

func TestParseDatePrefersFirst(t *testing.T) {
	menu := Menu{Date: "1.5.", DateEN: "2.6.", DateSV: "3.7."}
	dateString, ok := menu.EffectiveDate()
	if !ok || dateString != "1.5." {
		t.Errorf("EffectiveDate = %q, want 1.5.", dateString)
	}
}

func TestParseDateFallbacks(t *testing.T) {
	menu := Menu{Date: "", DateEN: "", DateSV: "3.7."}
	if s, ok := menu.EffectiveDate(); !ok || s != "3.7." {
		t.Errorf("EffectiveDate fallback = %q ok=%v, want 3.7.", s, ok)
	}
	menu = Menu{}
	if _, ok := menu.EffectiveDate(); ok {
		t.Error("EffectiveDate on empty menu reported ok")
	}
}

func TestParseDateWithWeekday(t *testing.T) {
	// Formats like "Ma 04.11." seen in feeds.
	got, ok := ParseDate("Ma 04.11.")
	if !ok || got.Day() != 4 || got.Month() != time.November {
		t.Errorf("ParseDate(\"Ma 04.11.\") = %v ok=%v, want Nov 4", got, ok)
	}
}

func TestParseDateInvalid(t *testing.T) {
	if _, ok := ParseDate(""); ok {
		t.Error("empty date parsed")
	}
	if _, ok := ParseDate("   "); ok {
		t.Error("blank date parsed")
	}
	if _, ok := ParseDate("kevät"); ok {
		t.Error("non-date parsed")
	}
}

func TestFlexTextPriceValues(t *testing.T) {
	raw := `{"contract":"8.50","graduate":7.25,"graduate_hyy":null,"normal":10.5,"student":2.95,"student_hyy":null}`
	var v PriceValue
	if err := json.Unmarshal([]byte(raw), &v); err != nil {
		t.Fatal(err)
	}
	if v.Contract.String() != "8.50" {
		t.Errorf("contract = %q, want 8.50", v.Contract)
	}
	if v.Normal.String() != "10.5" {
		t.Errorf("normal = %q, want 10.5 (number accepted)", v.Normal)
	}
	if v.GraduateHYY.String() != "" {
		t.Errorf("graduate_hyy = %q, want empty", v.GraduateHYY)
	}
}

func TestPriceValueSchemaDrift(t *testing.T) {
	cases := []struct {
		name string
		raw  string
		want *PriceValue
	}{
		{"object", `{"name":"Kevyesti","value":{"student":"2.95"}}`, &PriceValue{Student: "2.95"}},
		{"empty array", `{"name":"Tiedote","value":[]}`, nil},
		{"null value", `{"name":"Tiedote","value":null}`, nil},
		{"scalar value", `{"name":"Maksullinen","value":"ei"}`, nil},
	}
	for _, c := range cases {
		var p Price
		if err := json.Unmarshal([]byte(c.raw), &p); err != nil {
			t.Fatalf("%s: %v", c.name, err)
		}
		if (p.Value == nil) != (c.want == nil) {
			t.Errorf("%s: value = %v, want %v", c.name, p.Value, c.want)
		}
		if c.want != nil && p.Value != nil && p.Value.Student.String() != c.want.Student.String() {
			t.Errorf("%s: student = %q, want %q", c.name, p.Value.Student, c.want.Student)
		}
	}
}
