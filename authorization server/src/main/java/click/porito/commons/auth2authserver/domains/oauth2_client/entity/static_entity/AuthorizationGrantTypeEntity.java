package click.porito.commons.auth2authserver.domains.oauth2_client.entity.static_entity;

import click.porito.commons.auth2authserver.global.util.ConstantEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.lang.NonNull;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.util.Assert;

/**
 * AuthorizationGrantType 정보를 담는 정적인 Entity
 * @see org.springframework.security.oauth2.core.AuthorizationGrantType
 */
@ConstantEntity
@Cacheable @Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
@Entity @Table(name = "authorization_grant_type")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AuthorizationGrantTypeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name", unique = true,
            length = 255, nullable = false)
    private String name;


    public AuthorizationGrantTypeEntity(String name) {
        Assert.hasText(name, "grantType cannot be null");
        this.name = name;
    }

    public static AuthorizationGrantTypeEntity from(@NonNull AuthorizationGrantType grantType) {
        return new AuthorizationGrantTypeEntity(grantType.getValue());
    }

    public AuthorizationGrantType toAuthorizationGrantType() {
        return new AuthorizationGrantType(this.name);
    }

}
