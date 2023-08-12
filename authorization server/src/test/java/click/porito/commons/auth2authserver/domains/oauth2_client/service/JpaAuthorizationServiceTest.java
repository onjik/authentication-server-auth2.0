package click.porito.commons.auth2authserver.domains.oauth2_client.service;

import click.porito.commons.auth2authserver.domains.oauth2_client.entity.ClientEntity;
import click.porito.commons.auth2authserver.domains.oauth2_client.entity.OAuth2AuthorizationEntity;
import click.porito.commons.auth2authserver.domains.oauth2_client.entity.static_entity.AuthorizationGrantTypeEntity;
import click.porito.commons.auth2authserver.domains.oauth2_client.entity.static_entity.ClientAuthenticationMethodEntity;
import click.porito.commons.auth2authserver.domains.oauth2_client.entity.static_entity.ScopeEntity;
import click.porito.commons.auth2authserver.domains.oauth2_client.entity.token.AccessTokenEntity;
import click.porito.commons.auth2authserver.domains.oauth2_client.repository.AuthorizationGrantTypeRepository;
import click.porito.commons.auth2authserver.domains.oauth2_client.repository.CommonTokenRepository;
import click.porito.commons.auth2authserver.domains.oauth2_client.repository.OAuth2AuthorizationRepository;
import click.porito.commons.auth2authserver.domains.oauth2_client.repository.ScopeRepository;
import click.porito.commons.auth2authserver.domains.resource_owner.entity.ResourceOwnerEntity;
import click.porito.commons.auth2authserver.domains.resource_owner.entity.static_entity.RoleEntity;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.*;
import static test_util.TestEntityFactory.*;


@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@DataJpaTest
class JpaAuthorizationServiceTest {

    @Autowired
    private EntityManager em;

    @Autowired
    private ScopeRepository scopeRepository;

    @Autowired
    private CommonTokenRepository commonTokenRepository;

    @Autowired
    private OAuth2AuthorizationRepository oAuth2AuthorizationRepository;

    @Autowired
    private AuthorizationGrantTypeRepository authorizationGrantTypeRepository;


    private JpaAuthorizationService authorizationService;


    ClientEntity client;
    ResourceOwnerEntity resourceOwnerEntity;
    AuthorizationGrantTypeEntity authorizationGrantTypeEntity;

    @BeforeEach
    void setUp() {
        authorizationService = new JpaAuthorizationService(em,scopeRepository,commonTokenRepository,oAuth2AuthorizationRepository,authorizationGrantTypeRepository);
        ScopeEntity scopeEntity = getScopeEntity();
        ClientAuthenticationMethodEntity method = getClientAuthenticationMethodEntity();
        this.authorizationGrantTypeEntity = getAuthorizationGrantTypeEntity();
        RoleEntity role = getRoleEntity();
        em.persist(scopeEntity);
        em.persist(method);
        em.persist(authorizationGrantTypeEntity);
        em.persist(role);

        this.client = getClientEntity(scopeEntity,method,authorizationGrantTypeEntity);
        em.persist(client);
        this.resourceOwnerEntity = getResourceOwnerEntity(role);
        em.persist(resourceOwnerEntity);
    }
    @Test
    void save() {
        //given
        OAuth2AuthorizationEntity authorization = getOAuth2AuthorizationEntity(client, resourceOwnerEntity, authorizationGrantTypeEntity);
        OAuth2Authorization oAuth2Authorization = authorization.toObject();

        //when
        authorizationService.save(oAuth2Authorization);

        //then
        OAuth2AuthorizationEntity foundEntity = em.find(OAuth2AuthorizationEntity.class, authorization.getId());
        assertNotNull(foundEntity);
        assertEquals(foundEntity.getId(), authorization.getId());

    }

    @Test
    void remove() {
        //given
        OAuth2AuthorizationEntity authorization = getOAuth2AuthorizationEntity(client, resourceOwnerEntity, authorizationGrantTypeEntity);
        OAuth2Authorization oAuth2Authorization = authorization.toObject();
        authorizationService.save(oAuth2Authorization);

        //when
        authorizationService.remove(oAuth2Authorization);

        //then
        OAuth2AuthorizationEntity foundEntity = em.find(OAuth2AuthorizationEntity.class, authorization.getId());
        assertNull(foundEntity);
    }

    @Test
    void findById() {
        //given
        OAuth2AuthorizationEntity authorization = getOAuth2AuthorizationEntity(client, resourceOwnerEntity, authorizationGrantTypeEntity);
        OAuth2Authorization oAuth2Authorization = authorization.toObject();
        authorizationService.save(oAuth2Authorization);

        //when
        OAuth2Authorization foundAuthorization = authorizationService.findById(oAuth2Authorization.getId());

        //then
        assertNotNull(foundAuthorization);
        assertEquals(foundAuthorization.getId(), authorization.getId());
    }

    @Test
    void findByToken() {
        //given
        OAuth2AuthorizationEntity authorization = getOAuth2AuthorizationEntity(client, resourceOwnerEntity, authorizationGrantTypeEntity);
        AccessTokenEntity accessTokenEntity = getAccessTokenEntity(authorization);
        authorization.addToken(accessTokenEntity); //persist cascade
        em.persist(authorization);

        OAuth2Authorization oAuth2Authorization = authorization.toObject();
        authorizationService.save(oAuth2Authorization);

        //when
        OAuth2Authorization foundAuthorization = authorizationService.findByToken(accessTokenEntity.getValue(), OAuth2TokenType.ACCESS_TOKEN);

        //then
        assertNotNull(foundAuthorization);
        assertEquals(foundAuthorization.getId(), authorization.getId());
    }
}