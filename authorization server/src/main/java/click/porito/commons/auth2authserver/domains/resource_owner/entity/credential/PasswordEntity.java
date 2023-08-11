package click.porito.commons.auth2authserver.domains.resource_owner.entity.credential;

import click.porito.commons.auth2authserver.domains.resource_owner.entity.ResourceOwnerEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Duration;
import java.time.Instant;

@Entity @Table(name = "password")
@DiscriminatorValue("password")
@PrimaryKeyJoinColumn(name = "credential_id")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PasswordEntity extends CredentialEntity {

    @Column(name = "password_value", nullable = false)
    private String value;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Instant issuedAt;

    @Column(name = "expires_at")
    private Instant expiresAt;

    public PasswordEntity(ResourceOwnerEntity resourceOwnerEntity, String value, Duration expiresAfter) {
        super(resourceOwnerEntity);
        this.value = value;
        this.issuedAt = Instant.now();
        this.expiresAt = issuedAt.plus(expiresAfter);
    }

    public void setExpiresAt(Instant expiresAt) {
        this.expiresAt = expiresAt;
    }
}
