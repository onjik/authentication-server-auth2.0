package click.porito.commons.auth2authserver.domains.oauth2_client.service;

import click.porito.commons.auth2authserver.domains.oauth2_client.entity.ClientEntity;
import click.porito.commons.auth2authserver.domains.oauth2_client.entity.OAuth2AuthorizationEntity;
import click.porito.commons.auth2authserver.domains.oauth2_client.entity.static_entity.AuthorizationGrantTypeEntity;
import click.porito.commons.auth2authserver.domains.oauth2_client.entity.static_entity.ScopeEntity;
import click.porito.commons.auth2authserver.domains.oauth2_client.entity.token.AccessTokenEntity;
import click.porito.commons.auth2authserver.domains.oauth2_client.entity.token.AuthorizationCodeEntity;
import click.porito.commons.auth2authserver.domains.oauth2_client.entity.token.OidcIdTokenEntity;
import click.porito.commons.auth2authserver.domains.oauth2_client.entity.token.RefreshTokenEntity;
import click.porito.commons.auth2authserver.domains.oauth2_client.repository.AuthorizationGrantTypeRepository;
import click.porito.commons.auth2authserver.domains.oauth2_client.repository.OAuth2AuthorizationRepository;
import click.porito.commons.auth2authserver.domains.oauth2_client.repository.ScopeRepository;
import click.porito.commons.auth2authserver.domains.resource_owner.entity.ResourceOwnerEntity;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationCode;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
public class JpaAuthorizationService implements OAuth2AuthorizationService {

    private final EntityManager em;

    private final ScopeRepository scopeRepository;
    private final OAuth2AuthorizationRepository authorizationRepository;
    private final AuthorizationGrantTypeRepository authorizationGrantTypeRepository;


    @Override
    public void save(OAuth2Authorization authorization) {
        Assert.notNull(authorization,"authorization must not be null");
        OAuth2AuthorizationEntity oAuth2AuthorizationEntity = toEntity(authorization);
        authorizationRepository.save(oAuth2AuthorizationEntity);
    }

    @Override
    public void remove(OAuth2Authorization authorization) {
        Assert.notNull(authorization,"authorization must not be null");
        authorizationRepository.deleteById(authorization.getId());
    }

    @Override
    public OAuth2Authorization findById(String id) {
        Assert.hasText(id,"id must not be empty");
        return authorizationRepository.findById(id)
                .map(OAuth2AuthorizationEntity::toObject)
                .orElse(null);
    }

    @Override
    public OAuth2Authorization findByToken(String token, OAuth2TokenType tokenType) {
        Assert.hasText(token,"token must not be empty");
        Optional<OAuth2AuthorizationEntity> result;
        if (tokenType == null) {
            result = authorizationRepository.findByTokensValue(token).stream()
                    .findFirst();
        } else if (OAuth2ParameterNames.STATE.equals(tokenType.getValue())) {
            result = this.authorizationRepository.findByState(token);
        } else if (OAuth2ParameterNames.CODE.equals(tokenType.getValue())) {
            result = this.authorizationRepository.findByTokensValueAndTokensInstance(token, AuthorizationCodeEntity.class);
        } else if (OAuth2ParameterNames.ACCESS_TOKEN.equals(tokenType.getValue())) {
            result = this.authorizationRepository.findByTokensValueAndTokensInstance(token, AccessTokenEntity.class);
        } else if (OAuth2ParameterNames.REFRESH_TOKEN.equals(tokenType.getValue())) {
            result = this.authorizationRepository.findByTokensValueAndTokensInstance(token, RefreshTokenEntity.class);
        } else {
            result = Optional.empty();
        }

        return result.map(OAuth2AuthorizationEntity::toObject)
                .orElse(null);
    }

    private OAuth2AuthorizationEntity toEntity(OAuth2Authorization authorization) {

        ClientEntity clientEntity = Optional.ofNullable(em.find(ClientEntity.class,authorization.getRegisteredClientId()))
                .orElseThrow(() -> new DataRetrievalFailureException("client not found"));

        ResourceOwnerEntity resourceOwner = Optional.ofNullable(em.find(ResourceOwnerEntity.class,authorization.getPrincipalName()))
                .orElseThrow(() -> new DataRetrievalFailureException("resource owner not found"));

        AuthorizationGrantTypeEntity grantType = authorizationGrantTypeRepository.findByName(authorization.getAuthorizationGrantType().getValue())
                .orElseThrow(() -> new DataRetrievalFailureException("grant type not found"));

        Set<ScopeEntity> scopes = scopeRepository.findByNameIgnoreCaseIn(authorization.getAuthorizedScopes());
        if (scopes.size() != authorization.getAuthorizedScopes().size()) {
            throw new DataRetrievalFailureException("scope not found");
        }

        OAuth2AuthorizationEntity authorizationEntity = OAuth2AuthorizationEntity.builder()
                .id(authorization.getId())
                .client(clientEntity)
                .resourceOwner(resourceOwner)
                .authorizationGrantType(grantType)
                .attribute(authorization.getAttributes())
                .state(authorization.getAttribute(OAuth2ParameterNames.STATE))
                .build();
        scopes.forEach(authorizationEntity::addScope);


        Stream.of(authorization.getAccessToken())
                .filter(Objects::nonNull)
                .map(token -> AccessTokenEntity.from(token, authorizationEntity, scopes))
                .forEach(authorizationEntity::addToken);

        Stream.of(authorization.getRefreshToken())
                .filter(Objects::nonNull)
                .map(token -> RefreshTokenEntity.from(token, authorizationEntity))
                .forEach(authorizationEntity::addToken);

        Stream.of(authorization.getToken(OidcIdToken.class))
                .filter(Objects::nonNull)
                .map(token -> OidcIdTokenEntity.from(token, authorizationEntity))
                .forEach(authorizationEntity::addToken);

        Stream.of(authorization.getToken(OAuth2AuthorizationCode.class))
                .filter(Objects::nonNull)
                .map(token -> AuthorizationCodeEntity.from(token, authorizationEntity))
                .forEach(authorizationEntity::addToken);

        return authorizationEntity;
    }
}
