package click.porito.commons.auth2authserver.domains.resource_owner.entity.authentication;

import click.porito.commons.auth2authserver.domains.resource_owner.entity.ResourceOwnerEntity;
import click.porito.commons.auth2authserver.domains.resource_owner.entity.static_entity.RoleEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 계정 엔티티입니다. 계정의 존재를 대변합니다.
 */
@Entity @Table(name = "account")
@Getter
@Setter
@EqualsAndHashCode(of = "email")
@NoArgsConstructor
public class AccountEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "email", nullable = false, length = 255, unique = true)
    private String email;

    @Column(name = "is_email_verified", nullable = false)
    private boolean isEmailVerified;

    @Column(name = "expires_at", nullable = true)
    private Instant expiresAt;

    @Column(name = "is_locked")
    private boolean isLocked;

    @Column(name = "is_disabled")
    private boolean isDisabled;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "resource_owner_id", nullable = false)
    private ResourceOwnerEntity resourceOwner;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "account_role",
            joinColumns = @JoinColumn(name = "account_id",nullable = false),
            inverseJoinColumns = @JoinColumn(name = "role_id", nullable = false))
    private Set<RoleEntity> roleEntities = new HashSet<>();

    @OneToMany(mappedBy = "accountEntity", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AuthenticationEntity> authentications = new ArrayList<>();

    public void addAuthentication(AuthenticationEntity authenticationEntity) {
        authenticationEntity.setAccountEntity(this);
        getAuthentications().add(authenticationEntity);
    }


    public void addRole(RoleEntity roleEntity) {
        getRoleEntities().add(roleEntity);
    }

    public static AccountEntityBuilder builder(String email, ResourceOwnerEntity resourceOwner) {
        return new AccountEntityBuilder()
                .resourceOwner(resourceOwner)
                .email(email)
                .isDisabled(false)
                .isEmailVerified(false)
                .isLocked(false);
    }

    @Builder
    public AccountEntity(ResourceOwnerEntity resourceOwner, String email, boolean isEmailVerified, Instant expiresAt, boolean isLocked, boolean isDisabled) {
        this.resourceOwner = resourceOwner;
        this.email = email;
        this.isEmailVerified = isEmailVerified;
        this.expiresAt = expiresAt;
        this.isLocked = isLocked;
        this.isDisabled = isDisabled;
    }
}
