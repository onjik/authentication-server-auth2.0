package click.porito.commons.auth2authserver.domains.oauth2_client.entity.token;

import click.porito.commons.auth2authserver.domains.oauth2_client.entity.OAuth2AuthorizationEntity;
import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.Type;
import org.springframework.security.oauth2.core.OAuth2Token;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;

import java.time.Instant;
import java.util.Map;

@DiscriminatorValue("oidc_id_token")
@Entity @Table(name = "oidc_id_token")
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class OidcIdTokenEntity extends CommonTokenEntity {

    @Type(JsonType.class)
    @Column(name = "claims" , nullable = false, columnDefinition = "json")
    private Map<String, Object> claims;

    @Override
    public OidcIdToken toObject() {
        return new OidcIdToken(this.getValue(), this.getIssuedAt(), this.getExpiresAt(), this.getClaims());
    }

    @Override
    public Class<? extends OAuth2Token> obtainObjectType() {
        return OidcIdToken.class;
    }

    @Builder
    public OidcIdTokenEntity(Map<String, Object> metadata, String value, Instant issuedAt, Instant expiresAt, OAuth2AuthorizationEntity authorization, Map<String, Object> claims) {
        super(metadata, value, issuedAt, expiresAt, authorization);
        this.claims = claims;
    }

    public static OidcIdTokenEntity from(OAuth2Authorization.Token<OidcIdToken> oidcIdTokenTokenHolder, OAuth2AuthorizationEntity authorization){
        OidcIdToken token = oidcIdTokenTokenHolder.getToken();
        return OidcIdTokenEntity.builder()
                .metadata(oidcIdTokenTokenHolder.getMetadata())
                .value(token.getTokenValue())
                .issuedAt(token.getIssuedAt())
                .expiresAt(token.getExpiresAt())
                .claims(token.getClaims())
                .authorization(authorization)
                .claims(token.getClaims())
                .build();
    }

}
