package slug

import "testing"

func TestURI(t *testing.T) {
	cases := []struct{ in, want string }{
		{"Exactum", "exactum"},
		{"Chemicum", "chemicum"},
		// Java: NFD -> strip diacritics -> non-alnum runs -> single dash.
		{"Café Portaali", "cafe-portaali"},
		{"Soc&Kom", "soc-kom"},
		{"Porthania (opettajien ravintola)", "porthania-opettajien-ravintola-"},
		{"  Biokeskus 3 ", "-biokeskus-3-"},
		{"Metsätalo", "metsatalo"},
	}
	for _, c := range cases {
		if got := URI(c.in); got != c.want {
			t.Errorf("URI(%q) = %q, want %q", c.in, got, c.want)
		}
	}
}

func TestKey(t *testing.T) {
	cases := []struct{ in, want string }{
		{"Exactum", "exactum"},
		{"  Biokeskus 3 ", "biokeskus-3"},
		{"---", ""},
		{"", ""},
		{"Ylioppilasaukio", "ylioppilasaukio"},
	}
	for _, c := range cases {
		if got := Key(c.in); got != c.want {
			t.Errorf("Key(%q) = %q, want %q", c.in, got, c.want)
		}
	}
}

func TestKeyMatchesJavaSetURI(t *testing.T) {
	// Restaurant#setUri in the Java app: NFD, strip diacritics,
	// non-alnum -> dash, no trimming.
	if got := URI("Metsätalo"); got != "metsatalo" {
		t.Errorf("URI(Metsätalo) = %q, want metsatalo", got)
	}
}
