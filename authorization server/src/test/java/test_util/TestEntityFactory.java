package test_util;

import click.porito.commons.auth2authserver.domains.oauth2_client.entity.ClientEntity;
import click.porito.commons.auth2authserver.domains.oauth2_client.entity.OAuth2AuthorizationConsentEntity;
import click.porito.commons.auth2authserver.domains.oauth2_client.entity.OAuth2AuthorizationEntity;
import click.porito.commons.auth2authserver.domains.oauth2_client.entity.RedirectUriEntity;
import click.porito.commons.auth2authserver.domains.oauth2_client.entity.static_entity.AuthorizationGrantTypeEntity;
import click.porito.commons.auth2authserver.domains.oauth2_client.entity.static_entity.ClientAuthenticationMethodEntity;
import click.porito.commons.auth2authserver.domains.oauth2_client.entity.static_entity.ScopeEntity;
import click.porito.commons.auth2authserver.domains.oauth2_client.entity.token.AccessTokenEntity;
import click.porito.commons.auth2authserver.domains.oauth2_client.entity.token.RefreshTokenEntity;
import click.porito.commons.auth2authserver.domains.resource_owner.entity.ResourceOwnerEntity;
import click.porito.commons.auth2authserver.domains.resource_owner.entity.credential.AccountEntity;
import click.porito.commons.auth2authserver.domains.resource_owner.entity.static_entity.RoleEntity;
import jakarta.persistence.EntityManager;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class TestEntityFactory {

    public static ClientEntity getClientEntity(ScopeEntity scope, ClientAuthenticationMethodEntity method, AuthorizationGrantTypeEntity grantType) {
        ClientEntity testClient = ClientEntity.builder()
                .id(UUID.randomUUID().toString())
                .clientId(UUID.randomUUID().toString())
                .clientIdIssuedAt(Instant.now())
                .clientName("test client")
                .clientSecret(UUID.randomUUID().toString())
                .clientSecretExpiresAt(Instant.now().plus(Duration.ofDays(180)))
                .clientSettings(ClientSettings.builder().requireAuthorizationConsent(true).build().getSettings())
                .tokenSettings(TokenSettings.builder().accessTokenTimeToLive(Duration.ofDays(180)).build().getSettings())
                .build();
        RedirectUriEntity redirectUriEntity = new RedirectUriEntity("http://localhost:8080/oauth2/redirect", testClient);

        testClient.addRedirectUri(redirectUriEntity);
        testClient.addScope(scope);
        testClient.addClientAuthenticationMethod(method);
        testClient.addAuthorizationGrantType(grantType);
        return testClient;
    }

    public static ClientEntity getSavedClientEntity(EntityManager em){
        ScopeEntity scope = getScopeEntity();
        ClientAuthenticationMethodEntity method = getClientAuthenticationMethodEntity();
        AuthorizationGrantTypeEntity grantType = getAuthorizationGrantTypeEntity();
        RoleEntity role = getRoleEntity();

        em.persist(scope);
        em.persist(method);
        em.persist(grantType);
        em.persist(role);

        ClientEntity clientEntity = getClientEntity(scope, method, grantType);
        em.persist(clientEntity);
        return clientEntity;
    }

    public static AccountEntity getAccountEntity(ResourceOwnerEntity resourceOwnerEntity) {
        return AccountEntity.builder("test@gmail.com", resourceOwnerEntity).build();
    }

    public static ResourceOwnerEntity getResourceOwnerEntity(RoleEntity roleEntity) {
        //resource owner
        ResourceOwnerEntity resourceOwner = new ResourceOwnerEntity("name", ResourceOwnerEntity.Gender.MAN);
        resourceOwner.addRole(roleEntity);
        return resourceOwner;
    }

    public static ResourceOwnerEntity getSavedResourceOwnerEntity(EntityManager em) {
        RoleEntity role = getRoleEntity();
        em.persist(role);

        ResourceOwnerEntity resourceOwner = getResourceOwnerEntity(role);
        em.persist(resourceOwner);
        return resourceOwner;
    }

    public static OAuth2AuthorizationEntity getOAuth2AuthorizationEntity(ClientEntity testClient, ResourceOwnerEntity resourceOwner, AuthorizationGrantTypeEntity grantType) {
        OAuth2AuthorizationEntity authorization = OAuth2AuthorizationEntity.builder()
                .id(UUID.randomUUID().toString())
                .client(testClient)
                .resourceOwner(resourceOwner)
                .authorizationGrantType(grantType)
                .attribute(new HashMap<>())
                .state(UUID.randomUUID().toString())
                .build();
        return authorization;
    }

    public static ClientAuthenticationMethodEntity getClientAuthenticationMethodEntity() {
        return ClientAuthenticationMethodEntity.of(ClientAuthenticationMethod.CLIENT_SECRET_JWT);
    }

    public static RoleEntity getRoleEntity() {
        return new RoleEntity("ROLE_USER");
    }

    public static OAuth2AuthorizationEntity getSavedOAuth2AuthorizationEntity(EntityManager em) {
        ScopeEntity scope = getScopeEntity();
        ClientAuthenticationMethodEntity method = getClientAuthenticationMethodEntity();
        AuthorizationGrantTypeEntity grantType = getAuthorizationGrantTypeEntity();
        RoleEntity role = getRoleEntity();

        em.persist(scope);
        em.persist(method);
        em.persist(grantType);
        em.persist(role);

        ClientEntity clientEntity = getClientEntity(scope, method, grantType);
        em.persist(clientEntity);

        ResourceOwnerEntity resourceOwner = getResourceOwnerEntity(role);
        em.persist(resourceOwner);

        OAuth2AuthorizationEntity authorization = getOAuth2AuthorizationEntity(clientEntity, resourceOwner, grantType);
        AccessTokenEntity accessTokenEntity = getAccessTokenEntity(authorization);
        authorization.addToken(accessTokenEntity);
        em.persist(authorization);
        return authorization;
    }


    public static AccessTokenEntity getAccessTokenEntity(OAuth2AuthorizationEntity authorization) {
        AccessTokenEntity accessToken = AccessTokenEntity.builder()
                .tokenType(OAuth2AccessToken.TokenType.BEARER)
                .value(UUID.randomUUID().toString())
                .authorization(authorization)
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plus(Duration.ofDays(180)))
                .metadata(Map.of(OAuth2Authorization.Token.INVALIDATED_METADATA_NAME, false))
                .build();
        return accessToken;
    }

    public static RefreshTokenEntity getRefreshTokenEntity(OAuth2AuthorizationEntity authorization) {
        RefreshTokenEntity refreshToken = RefreshTokenEntity.builder()
                .metadata(RefreshTokenEntity.DEFAULT_METADATA)
                .value("value")
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plus(Duration.ofDays(180)))
                .authorization(authorization)
                .build();
        return refreshToken;
    }



        public static ScopeEntity getScopeEntity() {
        return new ScopeEntity("http://localhost:8080/api/v1/users/name", "SCOPE_USER_NAME");
    }


    public static AuthorizationGrantTypeEntity getAuthorizationGrantTypeEntity() {
        AuthorizationGrantTypeEntity grantType = AuthorizationGrantTypeEntity.from(AuthorizationGrantType.JWT_BEARER);
        return grantType;
    }

    public static OAuth2AuthorizationConsentEntity getOAuth2AuthorizationConsentEntity(ResourceOwnerEntity resourceOwner, ClientEntity client, Set<RoleEntity> roleEntities, Set<ScopeEntity> scopeEntities) {
        OAuth2AuthorizationConsentEntity entity = OAuth2AuthorizationConsentEntity.builder()
                .resourceOwner(resourceOwner)
                .client(client)
                .build();
        entity.getRoles().addAll(roleEntities);
        entity.getScopes().addAll(scopeEntities);
        return entity;
    }



}
