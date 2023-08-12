package click.porito.commons.auth2authserver.domains.oauth2_client.entity.token;

import click.porito.commons.auth2authserver.domains.oauth2_client.entity.OAuth2AuthorizationEntity;
import click.porito.commons.auth2authserver.domains.oauth2_client.entity.static_entity.ScopeEntity;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2Token;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
import org.springframework.util.Assert;

import java.time.Instant;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@DiscriminatorValue("access_token")
@Entity @Table(name = "access_token")
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class AccessTokenEntity extends CommonTokenEntity {

    @Column(name = "token_type", nullable = false)
    private String tokenType;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "access_token_scope",
            joinColumns = @JoinColumn(name = "token_id"),
            inverseJoinColumns = @JoinColumn(name = "scope_id"))
    private Set<ScopeEntity> scopes = new HashSet<>();


    @Override
    public Object toObject(){
        return new OAuth2AccessToken(OAuth2AccessToken.TokenType.BEARER, getValue(), getIssuedAt(), getExpiresAt(), getScopes().stream().map(ScopeEntity::getName).collect(Collectors.toSet()));
    }

    @Override
    public Class<? extends OAuth2Token> obtainObjectType() {
        return OAuth2AccessToken.class;
    }

    public void addScope(ScopeEntity scopeEntity){
        this.scopes.add(scopeEntity);
    }

    @Builder
    public AccessTokenEntity(Map<String, Object> metadata, String value, Instant issuedAt, Instant expiresAt, OAuth2AuthorizationEntity authorization, OAuth2AccessToken.TokenType tokenType) {
        super(metadata, value, issuedAt, expiresAt, authorization);
        this.tokenType = tokenType.getValue();
    }

    public static AccessTokenEntity from(OAuth2Authorization.Token<OAuth2AccessToken> accessTokenHolder,OAuth2AuthorizationEntity authorization, Set<ScopeEntity> scopeEntities){
        Map<String, Object> metadata = accessTokenHolder.getMetadata();
        OAuth2AccessToken token = accessTokenHolder.getToken();

        Assert.isTrue(token.getTokenType().equals(OAuth2AccessToken.TokenType.BEARER), "Access Token Type must be Bearer");

        AccessTokenEntity accessTokenEntity = AccessTokenEntity.builder()
                .metadata(metadata)
                .tokenType(token.getTokenType())
                .value(token.getTokenValue())
                .issuedAt(token.getIssuedAt())
                .expiresAt(token.getExpiresAt())
                .authorization(authorization)
                .build();
        scopeEntities.forEach(accessTokenEntity::addScope);

        return accessTokenEntity;
    }

}
