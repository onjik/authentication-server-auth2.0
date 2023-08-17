package click.porito.commons.auth2authserver.domains.oauth2_client.entity.static_entity;

import click.porito.commons.auth2authserver.global.util.ConstantEntity;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.lang.NonNull;
import org.springframework.security.oauth2.core.AuthorizationGrantType;

/**
 * AuthorizationGrantType 정보를 담는 정적인 Entity
 * @see org.springframework.security.oauth2.core.AuthorizationGrantType
 */
@ConstantEntity
@Entity @Table(name = "authorization_grant_type")
@EqualsAndHashCode(of = "id")
@Getter @Setter
@NoArgsConstructor
public class AuthorizationGrantTypeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name", unique = true,
            length = 255, nullable = false)
    private String name;

    @PrePersist
    public void prePersist() {
        this.name = this.name.toUpperCase();
    }


    public AuthorizationGrantTypeEntity(String name) {
        this.name = name;
    }

    public static AuthorizationGrantTypeEntity from(@NonNull AuthorizationGrantType grantType) {
        AuthorizationGrantTypeEntity grantTypeEntity = new AuthorizationGrantTypeEntity();
        grantTypeEntity.setName(grantType.getValue());
        return grantTypeEntity;
    }

    public AuthorizationGrantType toAuthorizationGrantType() {
        return new AuthorizationGrantType(this.name);
    }

}
