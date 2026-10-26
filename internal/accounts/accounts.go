// Package accounts implements user registration. It is a port of
// RepositoryUserService of the original application, with social
// sign-in dropped.
package accounts

import (
	"context"
	"errors"
	"strings"
	"unicode/utf8"

	"golang.org/x/crypto/bcrypt"

	"github.com/JaniL/unirater/internal/store"
)

// BcryptCost matches the BCryptPasswordEncoder(10) of the Java app.
const BcryptCost = 10

// ErrDuplicateEmail is returned when the email is already registered.
var ErrDuplicateEmail = store.ErrDuplicateEmail

// Service registers user accounts.
type Service struct {
	store *store.Store
}

// NewService wires the service to a store.
func NewService(st *store.Store) *Service {
	return &Service{store: st}
}

// RegistrationForm is the /user/register form payload.
type RegistrationForm struct {
	Email                string
	FirstName            string
	LastName             string
	Password             string
	PasswordVerification string
}

// ValidationError carries field errors keyed by form field name.
type ValidationError map[string]string

func (e ValidationError) Error() string {
	return "accounts: validation failed"
}

// Validate applies the same rules as the annotated Java RegistrationForm:
// email must be a non-empty address of at most 100 characters, names must
// be non-empty and at most 100 characters, and the password must be given
// and match its verification. It returns a ValidationError listing every
// broken rule, or nil.
func (f *RegistrationForm) Validate() error {
	errs := ValidationError{}

	if f.Email == "" {
		errs["email"] = "Email on pakollinen."
	} else if utf8.RuneCountInString(f.Email) > 100 {
		errs["email"] = "Email on liian pitkä."
	} else if !isEmail(strings.TrimSpace(f.Email)) {
		errs["email"] = "Email ei ole kelvollinen."
	}

	if strings.TrimSpace(f.FirstName) == "" {
		errs["firstName"] = "Etunimi on pakollinen."
	} else if utf8.RuneCountInString(f.FirstName) > 100 {
		errs["firstName"] = "Etunimi on liian pitkä."
	}

	if strings.TrimSpace(f.LastName) == "" {
		errs["lastName"] = "Sukunimi on pakollinen."
	} else if utf8.RuneCountInString(f.LastName) > 100 {
		errs["lastName"] = "Sukunimi on liian pitkä."
	}

	if f.Password == "" {
		errs["password"] = "Salasana on pakollinen."
	} else if f.Password != f.PasswordVerification {
		errs["password"] = "Salasanat eivät täsmää."
	}

	if len(errs) == 0 {
		return nil
	}
	return errs
}

// Register validates the form, hashes the password with bcrypt and
// persists a new user account. It mirrors UserService#registerNewUserAccount.
func (s *Service) Register(ctx context.Context, form RegistrationForm) (*store.User, error) {
	if err := form.Validate(); err != nil {
		return nil, err
	}

	hash, err := bcrypt.GenerateFromPassword([]byte(form.Password), BcryptCost)
	if err != nil {
		return nil, err
	}

	user, err := s.store.CreateUser(ctx, store.User{
		Email:     strings.TrimSpace(form.Email),
		FirstName: strings.TrimSpace(form.FirstName),
		LastName:  strings.TrimSpace(form.LastName),
		Password:  string(hash),
		Role:      "ROLE_USER",
	})
	if err != nil {
		return nil, err
	}
	return user, nil
}

// Authenticate verifies an email/password pair, returning nil user
// (without error) on bad credentials.
func (s *Service) Authenticate(ctx context.Context, email, password string) (*store.User, error) {
	user, err := s.store.UserByEmail(ctx, email)
	if errors.Is(err, store.ErrNotFound) {
		return nil, nil
	}
	if err != nil {
		return nil, err
	}

	if bcrypt.CompareHashAndPassword([]byte(user.Password), []byte(password)) != nil {
		return nil, nil
	}
	return user, nil
}

// isEmail is a deliberately permissive RFC-ish check, matching the spirit
// of the Hibernate @Email validator: one @ separating two non-empty
// parts, the domain containing at least one dot with no spaces.
func isEmail(value string) bool {
	at := strings.Index(value, "@")
	if at <= 0 || at == len(value)-1 {
		return false
	}
	local, domain := value[:at], value[at+1:]
	if strings.ContainsAny(value, " \t\r\n") {
		return false
	}
	dot := strings.Index(domain, ".")
	return dot > 0 && dot < len(domain)-1 && local != ""
}
