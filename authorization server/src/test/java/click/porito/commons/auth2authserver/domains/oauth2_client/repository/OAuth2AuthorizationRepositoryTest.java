package click.porito.commons.auth2authserver.domains.oauth2_client.repository;

import click.porito.commons.auth2authserver.domains.oauth2_client.entity.ClientEntity;
import click.porito.commons.auth2authserver.domains.oauth2_client.entity.OAuth2AuthorizationEntity;
import click.porito.commons.auth2authserver.domains.oauth2_client.entity.static_entity.AuthorizationGrantTypeEntity;
import click.porito.commons.auth2authserver.domains.oauth2_client.entity.static_entity.ClientAuthenticationMethodEntity;
import click.porito.commons.auth2authserver.domains.oauth2_client.entity.static_entity.ScopeEntity;
import click.porito.commons.auth2authserver.domains.oauth2_client.entity.token.AccessTokenEntity;
import click.porito.commons.auth2authserver.domains.oauth2_client.entity.token.CommonTokenEntity;
import click.porito.commons.auth2authserver.domains.resource_owner.entity.ResourceOwnerEntity;
import click.porito.commons.auth2authserver.domains.resource_owner.entity.static_entity.RoleEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static test_util.TestEntityFactory.*;

@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@DataJpaTest
class OAuth2AuthorizationRepositoryTest {

    @Autowired
    OAuth2AuthorizationRepository oAuth2AuthorizationRepository;


    @PersistenceContext
    EntityManager em;

    ScopeEntity scope;
    RoleEntity roleEntity;
    ClientAuthenticationMethodEntity method;
    AuthorizationGrantTypeEntity grantType;
    ClientEntity client;
    ResourceOwnerEntity resourceOwner;
    AccessTokenEntity accessTokenEntity;
    OAuth2AuthorizationEntity authorization;

    @BeforeEach
    void staticDataSetUp(){
        //static data
        scope = getScopeEntity();
        roleEntity = getRoleEntity();
        method = getClientAuthenticationMethodEntity();
        grantType = getAuthorizationGrantTypeEntity();
        em.persist(scope);
        em.persist(roleEntity);
        em.persist(method);
        em.persist(grantType);

        client = getClientEntity(scope, method, grantType);
        em.persist(client);
        resourceOwner = getResourceOwnerEntity(roleEntity);
        em.persist(resourceOwner);
        authorization = getOAuth2AuthorizationEntity(client, resourceOwner, grantType);
        em.persist(authorization);
    }

    @Test
    @DisplayName("findByTokensValue 테스트")
    void findByTokensValue() {
        //given
        accessTokenEntity = getAccessTokenEntity(authorization);
        authorization.addToken(accessTokenEntity);
        //when
        List<OAuth2AuthorizationEntity> result = oAuth2AuthorizationRepository.findByTokensValue(accessTokenEntity.getValue());
        //then
        assertEquals(result.size(),1);
        String tokenValue = result.get(0)
                .getTokens()
                .stream()
                .map(CommonTokenEntity::getValue)
                .findFirst()
                .get();
        assertEquals(tokenValue,accessTokenEntity.getValue());
    }

    @Test
    @DisplayName("findByTokensValueAndTokensInstance 테스트")
    void findByTokensValueAndTokensInstance() {
        //given
        accessTokenEntity = getAccessTokenEntity(authorization);
        authorization.addToken(accessTokenEntity);
        //when
        OAuth2AuthorizationEntity result = oAuth2AuthorizationRepository.findByTokensValueAndTokensInstance(accessTokenEntity.getValue(), AccessTokenEntity.class)
                .orElseGet(() -> fail());
        //then
        assertNotNull(result);
        assertEquals(result.getTokens().size(),1);
        CommonTokenEntity commonTokenEntity = result.getTokens().get(0);
        assertInstanceOf(AccessTokenEntity.class,commonTokenEntity);
        assertEquals(commonTokenEntity.getValue(),accessTokenEntity.getValue());

    }




}