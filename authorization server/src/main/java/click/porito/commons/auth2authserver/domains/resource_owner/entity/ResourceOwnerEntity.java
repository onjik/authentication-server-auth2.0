package click.porito.commons.auth2authserver.domains.resource_owner.entity;

import click.porito.commons.auth2authserver.domains.resource_owner.entity.credential.CredentialEntity;
import click.porito.commons.auth2authserver.domains.resource_owner.entity.static_entity.RoleEntity;
import click.porito.commons.auth2authserver.util.GenderConverter;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

@Entity @Table(name = "resource_owner")
@Getter
@Setter @EqualsAndHashCode(of = "id")
@NoArgsConstructor
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

    @Nullable
    @Column(name = "expires_at", nullable = true)
    private Instant expiresAt;

    @Column(name = "is_locked")
    private boolean locked;

    @Column(name = "is_disabled")
    private boolean disabled;

    @OneToMany(mappedBy = "resourceOwner", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<CredentialEntity> credentials = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "resource_owner_role",
            joinColumns = @JoinColumn(name = "resource_owner_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<RoleEntity> roleEntities = new HashSet<>();

    @Builder
    public ResourceOwnerEntity(String name, Gender gender, String email, Instant createdAt, Instant expiresAt, boolean locked, boolean disabled) {
        this.name = name;
        this.gender = gender;
        this.email = email;
        this.createdAt = createdAt;
        this.expiresAt = expiresAt;
        this.locked = locked;
        this.disabled = disabled;
    }

    public void addCredential(CredentialEntity credential) {
        credentials.add(credential);
        credential.setResourceOwner(this);
    }

    public void addRole(RoleEntity roleEntity) {
        roleEntities.add(roleEntity);
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
