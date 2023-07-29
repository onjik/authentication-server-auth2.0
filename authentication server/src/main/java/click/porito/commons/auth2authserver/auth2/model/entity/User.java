package click.porito.commons.auth2authserver.auth2.model.entity;

import click.porito.commons.auth2authserver.auth2.model.constant.Gender;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public final class User {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(name = "email", unique = true, nullable = false)
    @Email(message = "invalid email format") @Length(max = 254, message = "email must be less than 254 characters")
    private String email;

    @Column(name = "name", nullable = false)
    @NotBlank(message = "name must not be empty") @Length(max = 100, message = "name must be less than 100 characters")
    private String name;

    @Enumerated(EnumType.STRING)
    @NotNull
    @Column(name = "gender", nullable = false)
    private Gender gender;

    @Column(name = "birth_date", nullable = false)
    @Past(message = "birth date must be past")
    private LocalDate birthDate;

    @Column(name = "registered_date", nullable = false)
    private LocalDateTime registeredDate;


    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private Set<Credential> credentials = new HashSet<>(4);

    @OneToMany(cascade = CascadeType.REMOVE,
            orphanRemoval = true, fetch = FetchType.LAZY, mappedBy = "owner"
    )
    private List<RememberMeToken> rememberMeTokens = new ArrayList<>(4);

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_role",
            joinColumns = @JoinColumn(name = "user_id",nullable = false),
            inverseJoinColumns = @JoinColumn(name = "role_id",nullable = false)
    )
    private Set<Role> roles = new HashSet<>();

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true, mappedBy = "owner")
    private List<Service> services= new ArrayList<>(4);

    @PrePersist
    public void createdAt(){
        this.registeredDate = LocalDateTime.now();
    }


    public User(String email, String name, Gender gender, LocalDate birthDate, Set<Credential> credentials, List<RememberMeToken> rememberMeTokens, Set<Role> roles, List<Service> services) {
        this.email = email;
        this.name = name;
        this.gender = gender;
        this.birthDate = birthDate;
        this.credentials = credentials;
        this.rememberMeTokens = rememberMeTokens;
        this.roles = roles;
        this.services = services;
    }

    public static CredentialAsker builder(String email, String name, Gender gender, LocalDate birthDate){
        UserBuilder userBuilder = new UserBuilder(email, name, gender, birthDate);
        return new CredentialAsker(userBuilder);
    }

    public static class CredentialAsker {
        private final UserBuilder userBuilder;

        public CredentialAsker(UserBuilder userBuilder) {
            this.userBuilder = userBuilder;
        }

        public RoleAsker setCredential(Set<Credential> credentials){
            this.userBuilder.credentials = credentials;
            return new RoleAsker(this.userBuilder);
        }

        public RoleAsker setCredential(Credential credential){
            HashSet<Credential> credentials = new HashSet<>();
            credentials.add(credential);
            return new RoleAsker(this.userBuilder);
        }
    }

    public static class RoleAsker{
        private final UserBuilder userBuilder;

        public RoleAsker(UserBuilder userBuilder) {
            this.userBuilder = userBuilder;
        }

        public UserBuilder setRoles(Set<Role> roles){
            this.userBuilder.roles = roles;
            return this.userBuilder;
        }

        public UserBuilder setRole(Role role){
            HashSet<Role> roles = new HashSet<>();
            roles.add(role);
            this.setRoles(roles);
            return this.userBuilder;
        }
    }

    public static class UserBuilder{
        // essential
        private String email;
        private String name;
        private Gender gender;
        private LocalDate birthDate;
        private Set<Credential> credentials;

        // optional
        private List<RememberMeToken> rememberMeTokens;
        private Set<Role> roles;
        private List<Service> services;

        private UserBuilder(String email, String name, Gender gender, LocalDate birthDate) {
            this.email = email;
            this.name = name;
            this.gender = gender;
            this.birthDate = birthDate;
        }


        public UserBuilder setRememberMeTokens(List<RememberMeToken> rememberMeTokens){
            this.rememberMeTokens = rememberMeTokens;
            return this;
        }

        public UserBuilder setRememberMeToken(RememberMeToken rememberMeToken){
            List<RememberMeToken> list = new ArrayList<>();
            list.add(rememberMeToken);
            this.rememberMeTokens = list;
            return this;
        }

        public UserBuilder setRoles(Set<Role> roles){
            this.roles = roles;
            return this;
        }

        public UserBuilder setRole(Role role){
            HashSet<Role> set = new HashSet<>();
            set.add(role);
            this.roles = set;
            return this;
        }

        public UserBuilder setServices(List<Service> services){
            this.services = services;
            return this;
        }

        public UserBuilder setService(Service service){
            List<Service> list = new ArrayList<>();
            list.add(service);
            this.services = list;
            return this;
        }

        public User build(){
            return new User(email,name,gender,birthDate,credentials,rememberMeTokens,roles,services);
        }


    }
}
