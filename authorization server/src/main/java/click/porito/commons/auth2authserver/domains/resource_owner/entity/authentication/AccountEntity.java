package click.porito.commons.auth2authserver.domains.resource_owner.entity.authentication;

import click.porito.commons.auth2authserver.domains.resource_owner.entity.ResourceOwnerEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

/**
 * 계정 엔티티입니다. 계정의 존재를 대변합니다.
 */
@Entity @Table(name = "account")
@Getter
@Setter
@EqualsAndHashCode(of = "email")
@NoArgsConstructor
public class AccountEntity {

    @Id
    @OneToOne
    @JoinColumn(name = "resource_owner_id", nullable = false)
    private ResourceOwnerEntity resourceOwner;

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
