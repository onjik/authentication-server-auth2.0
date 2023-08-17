package click.porito.commons.auth2authserver.domains.oauth2_client.repository;

import click.porito.commons.auth2authserver.domains.oauth2_client.entity.ClientEntity;
import click.porito.commons.auth2authserver.domains.oauth2_client.entity.OAuth2AuthorizationConsentEntity;
import click.porito.commons.auth2authserver.domains.oauth2_client.entity.static_entity.AuthorizationGrantTypeEntity;
import click.porito.commons.auth2authserver.domains.oauth2_client.entity.static_entity.ClientAuthenticationMethodEntity;
import click.porito.commons.auth2authserver.domains.oauth2_client.entity.static_entity.ScopeEntity;
import click.porito.commons.auth2authserver.domains.resource_owner.entity.ResourceOwnerEntity;
import click.porito.commons.auth2authserver.domains.resource_owner.entity.static_entity.RoleEntity;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static test_util.TestEntityFactory.*;

@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@DataJpaTest
class ConsentRepositoryTest {

    @Autowired
    ConsentRepository consentRepository;

    @Autowired
    EntityManager em;

    OAuth2AuthorizationConsentEntity consentEntity;
    ClientEntity clientEntity;
    ResourceOwnerEntity resourceOwner;
    @BeforeEach
    void setUp(){
        //static properties
        ScopeEntity scope = getScopeEntity();
        em.persist(scope);
        ClientAuthenticationMethodEntity method = getClientAuthenticationMethodEntity();
        em.persist(method);
        AuthorizationGrantTypeEntity grantType = getAuthorizationGrantTypeEntity();
        em.persist(grantType);
        RoleEntity role = getRoleEntity();
        em.persist(role);

        //client
        this.clientEntity = getClientEntity(scope, method, grantType);
        em.persist(clientEntity);

        //resourceOwner
        this.resourceOwner = getResourceOwnerEntity(role);
        em.persist(resourceOwner);

        //consent
        this.consentEntity = new OAuth2AuthorizationConsentEntity(clientEntity, resourceOwner);
        consentRepository.save(consentEntity);
    }

    @Test
    void findByClientIdAndResourceOwnerId() {
        //given
        String clientId = clientEntity.getId();
        String resourceOwnerId = resourceOwner.getId();

        //when
        OAuth2AuthorizationConsentEntity resultEntity = Optional.ofNullable(consentRepository.findByClientIdAndResourceOwnerId(clientId, resourceOwnerId))
                .orElseGet(Assertions::fail);

        //then
        assertEquals(consentEntity.getClient().getId(), resultEntity.getClient().getId());
        assertEquals(consentEntity.getResourceOwner().getId(), resultEntity.getResourceOwner().getId());

    }

    @Test
    void deleteByClientIdAndResourceOwnerId() {
        //given
        String clientId = clientEntity.getId();
        String resourceOwnerId = resourceOwner.getId();

        //when
        consentRepository.deleteByClientIdAndResourceOwnerId(clientId, resourceOwnerId);

        //then
        assertNull(consentRepository.findByClientIdAndResourceOwnerId(clientId, resourceOwnerId));
    }


}