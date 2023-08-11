package click.porito.commons.auth2authserver.domains.resource_owner.entity;

import click.porito.commons.auth2authserver.domains.resource_owner.entity.static_entity.Role;
import click.porito.commons.auth2authserver.domains.resource_owner.entity.credential.CredentialEntity;
import click.porito.commons.auth2authserver.util.GenderConverter;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;

import java.time.Duration;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

@Entity @Table(name = "resource_owner")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ResourceOwnerEntity {

    @Id
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "uuid2")
    @Column(length = 50, nullable = false, updatable = false)
    private String id;

    @Column(name = "name", nullable = false, length = 255)
    private String name;

    @Convert(converter = GenderConverter.class)
    @Column(name = "gender", nullable = false)
    private Gender gender;

    @Column(name = "email", unique = true, nullable = false, length = 255)
    private String email;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    @Column(name = "expires_at")
    private Instant expiresAt;

    @Column(name = "is_locked")
    private boolean locked;

    @Column(name = "is_disabled")
    private boolean disabled;

    @OneToMany(mappedBy = "resourceOwnerEntity", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<CredentialEntity> credentialEntities = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinTable(name = "resource_owner_role",
            joinColumns = @JoinColumn(name = "resource_owner_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new HashSet<>();


    public ResourceOwnerEntity(String name, Gender gender, String email, Set<CredentialEntity> credentialEntities, Set<Role> roles) {
        this.name = name;
        this.gender = gender;
        this.email = email;
        this.credentialEntities = credentialEntities;
        this.roles = roles;
        this.createdAt = Instant.now();
        this.locked = false;
        this.disabled = false;
    }

    public ResourceOwnerEntity(String name, Gender gender, String email, Duration expiresAfter, Set<CredentialEntity> credentialEntities, Set<Role> roles) {
        this.name = name;
        this.gender = gender;
        this.email = email;
        this.createdAt = Instant.now();
        this.expiresAt = createdAt.plus(expiresAfter);
        this.locked = false;
        this.disabled = false;
        this.credentialEntities = credentialEntities;
        this.roles = roles;
    }

    public enum Gender {
        MAN('M'),WOMEN('W');

        private final char idChar;

        Gender(char idChar) {
            this.idChar = idChar;
        }

        public char getIdChar() {
            return idChar;
        }
    }
}
