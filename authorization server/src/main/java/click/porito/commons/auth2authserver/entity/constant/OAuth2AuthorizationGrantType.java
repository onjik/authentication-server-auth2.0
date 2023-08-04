package click.porito.commons.auth2authserver.entity.constant;

import click.porito.commons.auth2authserver.ConstantEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.util.Assert;

/**
 * AuthorizationGrantType 정보를 담는 정적인 Entity
 * @see org.springframework.security.oauth2.core.AuthorizationGrantType
 */
@ConstantEntity
@Entity @Table(name = "authorization_grant_type")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(of = "name")
public class OAuth2AuthorizationGrantType {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name", unique = true,
            length = 255, nullable = false)
    private String name;


    //과도한 객체 생성 비용을 방지하기 위해, 내부 필드에 임시 저장
    @Transient
    private AuthorizationGrantType grantType;

    public OAuth2AuthorizationGrantType(@NonNull AuthorizationGrantType grantType) {
        Assert.notNull(grantType, "grantType cannot be null");
        this.grantType = grantType;
        this.name = grantType.getValue();
    }

    public static OAuth2AuthorizationGrantType from(@NonNull AuthorizationGrantType grantType) {
        return new OAuth2AuthorizationGrantType(grantType);
    }

    public AuthorizationGrantType toAuthorizationGrantType() {
        if (grantType == null) {
            grantType = new AuthorizationGrantType(name);
        }
        return grantType;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
