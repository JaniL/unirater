package unicaferater.domain;


import javax.persistence.*;
import unicaferater.auth.Role;
import unicaferater.auth.SocialMediaService;
import unicaferater.entity.BaseEntity;
 
@Entity
@Table(name = "user_accounts")
public class User extends BaseEntity<Long> {
 
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
 
    @Column(name = "email", length = 100, nullable = false, unique = true)
    private String email;
 
    @Column(name = "first_name", length = 100,nullable = false)
    private String firstName;
 
    @Column(name = "last_name", length = 100, nullable = false)
    private String lastName;
 
    @Column(name = "password", length = 255)
    private String password;
 
    @Enumerated(EnumType.STRING)
    @Column(name = "role", length = 20, nullable = false)
    private Role role;
 
    @Enumerated(EnumType.STRING)
    @Column(name = "sign_in_provider", length = 20)
    private SocialMediaService signInProvider;
 
    //The constructor and getters are omitted for the sake of clarity
 
    public static Builder getBuilder() {
        return new Builder();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public SocialMediaService getSignInProvider() {
        return signInProvider;
    }

    public void setSignInProvider(SocialMediaService signInProvider) {
        this.signInProvider = signInProvider;
    }
     
    
    public static class Builder {
 
        private User user;
 
        public Builder() {
            user = new User();
            user.role = Role.ROLE_USER;
        }
 
        public Builder email(String email) {
            user.email = email;
            return this;
        }
 
        public Builder firstName(String firstName) {
            user.firstName = firstName;
            return this;
        }
 
        public Builder lastName(String lastName) {
            user.lastName = lastName;
            return this;
        }
 
        public Builder password(String password) {
            user.password = password;
            return this;
        }
 
        public Builder signInProvider(SocialMediaService signInProvider) {
            user.signInProvider = signInProvider;
            return this;
        }
 
        public User build() {
            return user;
        }
    }
}