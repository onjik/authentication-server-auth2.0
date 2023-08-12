package click.porito.commons.auth2authserver.domains.oauth2_client.entity.token;

import click.porito.commons.auth2authserver.domains.oauth2_client.entity.OAuth2AuthorizationEntity;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import org.springframework.security.oauth2.core.OAuth2Token;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationCode;

import java.time.Instant;
import java.util.Map;

@DiscriminatorValue("authorization_code")
@Entity @Table(name = "authorization_code")
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class AuthorizationCodeEntity extends CommonTokenEntity {

    @Override
    public Object toObject() {
        return new OAuth2AuthorizationCode(this.getValue(), this.getIssuedAt(), this.getExpiresAt());

    }

    @Override
    public Class<? extends OAuth2Token> obtainObjectType() {
        return null;
    }

    @Builder
    public AuthorizationCodeEntity(Map<String, Object> metadata, String value, Instant issuedAt, Instant expiresAt, OAuth2AuthorizationEntity authorization) {
        super(metadata, value, issuedAt, expiresAt, authorization);
    }

    public static AuthorizationCodeEntity from(OAuth2Authorization.Token<OAuth2AuthorizationCode> tokenHolder, OAuth2AuthorizationEntity authorization) {
        return AuthorizationCodeEntity.builder()
                .metadata(tokenHolder.getMetadata())
                .value(tokenHolder.getToken().getTokenValue())
                .issuedAt(tokenHolder.getToken().getIssuedAt())
                .expiresAt(tokenHolder.getToken().getExpiresAt())
                .authorization(authorization)
                .build();
    }
}
