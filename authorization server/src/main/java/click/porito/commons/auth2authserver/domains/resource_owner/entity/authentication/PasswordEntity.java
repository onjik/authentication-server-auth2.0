package click.porito.commons.auth2authserver.domains.resource_owner.entity.authentication;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Entity @Table(name = "password")
@DiscriminatorValue("password")
@PrimaryKeyJoinColumn(name = "authentication_id")
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class PasswordEntity extends AuthenticationEntity {

    @Column(name = "password_value", nullable = false)
    private String value;

    @CreationTimestamp
    @Column(name = "issued_at", updatable = false)
    private Instant issuedAt;

    @Column(name = "expires_at")
    private Instant expiresAt;

    public static PasswordEntityBuilder builder(AccountEntity accountEntity, String value) {
        return new PasswordEntityBuilder()
                .accountEntity(accountEntity)
                .value(value);
    }

    @Builder
    public PasswordEntity(AccountEntity accountEntity, String value, Instant expiresAt) {
        super(accountEntity);
        this.value = value;
        this.expiresAt = expiresAt;
    }
}
