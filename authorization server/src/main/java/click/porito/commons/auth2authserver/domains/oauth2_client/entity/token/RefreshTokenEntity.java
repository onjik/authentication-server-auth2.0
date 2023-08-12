package click.porito.commons.auth2authserver.domains.oauth2_client.entity.token;

import click.porito.commons.auth2authserver.domains.oauth2_client.entity.OAuth2AuthorizationEntity;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import org.springframework.security.oauth2.core.OAuth2RefreshToken;
import org.springframework.security.oauth2.core.OAuth2Token;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;

import java.time.Instant;
import java.util.Map;

@DiscriminatorValue("refresh_token")
@Entity @Table(name = "refresh_token")
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class RefreshTokenEntity extends CommonTokenEntity {

    @Override
    public OAuth2RefreshToken toObject() {
        return new OAuth2RefreshToken(this.getValue(), this.getIssuedAt(), this.getExpiresAt());
    }

    @Override
    public Class<? extends OAuth2Token> obtainObjectType() {
        return OAuth2RefreshToken.class;
    }

    @Builder
    public RefreshTokenEntity(Map<String, Object> metadata, String value, Instant issuedAt, Instant expiresAt, OAuth2AuthorizationEntity authorization) {
        super(metadata, value, issuedAt, expiresAt, authorization);
    }

    public static RefreshTokenEntity from(OAuth2Authorization.Token<OAuth2RefreshToken> refreshTokenTokenHolder, OAuth2AuthorizationEntity authorization){
        OAuth2RefreshToken token = refreshTokenTokenHolder.getToken();
        return RefreshTokenEntity.builder()
                .metadata(refreshTokenTokenHolder.getMetadata())
                .value(token.getTokenValue())
                .issuedAt(token.getIssuedAt())
                .expiresAt(token.getExpiresAt())
                .authorization(authorization)
                .build();
    }
}
