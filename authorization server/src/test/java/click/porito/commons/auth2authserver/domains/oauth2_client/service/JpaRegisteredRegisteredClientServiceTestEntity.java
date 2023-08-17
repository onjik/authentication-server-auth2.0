package click.porito.commons.auth2authserver.domains.oauth2_client.service;

import click.porito.commons.auth2authserver.domains.oauth2_client.entity.ClientEntity;
import click.porito.commons.auth2authserver.domains.oauth2_client.entity.RedirectUriEntity;
import click.porito.commons.auth2authserver.domains.oauth2_client.entity.static_entity.AuthorizationGrantTypeEntity;
import click.porito.commons.auth2authserver.domains.oauth2_client.entity.static_entity.ClientAuthenticationMethodEntity;
import click.porito.commons.auth2authserver.domains.oauth2_client.entity.static_entity.ScopeEntity;
import click.porito.commons.auth2authserver.domains.oauth2_client.repository.AuthenticationMethodRepository;
import click.porito.commons.auth2authserver.domains.oauth2_client.repository.AuthorizationGrantTypeRepository;
import click.porito.commons.auth2authserver.domains.oauth2_client.repository.ClientRepository;
import click.porito.commons.auth2authserver.domains.oauth2_client.repository.ScopeRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static test_util.TestEntityFactory.*;

@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@DataJpaTest
class JpaRegisteredRegisteredClientServiceTestEntity {

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private ScopeRepository scopeRepository;

    @Autowired
    private AuthenticationMethodRepository authenticationMethodRepository;

    @Autowired
    private AuthorizationGrantTypeRepository authorizationGrantTypeRepository;
    @Autowired
    private EntityManager em;


    private RegisteredClientRepository jpaRegisteredClientService;
    private ClientEntity clientEntity;

    @BeforeEach
    void setUp() {
        jpaRegisteredClientService = new JpaRegisteredClientService(clientRepository, scopeRepository, authenticationMethodRepository, authorizationGrantTypeRepository);

        //entity 초기화
        ScopeEntity scope = getScopeEntity();
        ClientAuthenticationMethodEntity method = getClientAuthenticationMethodEntity();
        AuthorizationGrantTypeEntity grantType = getAuthorizationGrantTypeEntity();
        em.persist(scope);
        em.persist(method);
        em.persist(grantType);

        clientEntity = getClientEntity(scope, method, grantType);


    }

    @Test
    @DisplayName("RegisteredClient 저장 테스트")
    void saveTest() {
        //given
        RegisteredClient registeredClient = clientEntity.toObject();

        //when
        jpaRegisteredClientService.save(registeredClient);

        //then
        Optional<ClientEntity> savedClient = clientRepository.findById(registeredClient.getId());
        ClientEntity clientEntity = savedClient.orElseGet(Assertions::fail);
        assertTrue(clientEntity.getRedirectUris().stream()
                .map(RedirectUriEntity::getUri)
                .collect(Collectors.toSet())
                .containsAll(registeredClient.getRedirectUris()));
        assertTrue(clientEntity.getScopes().stream()
                .map(ScopeEntity::getName)
                .collect(Collectors.toSet())
                .containsAll(registeredClient.getScopes()));
        assertTrue(clientEntity.getClientAuthenticationMethods().stream()
                .map(ClientAuthenticationMethodEntity::getName)
                .collect(Collectors.toSet())
                .contains(registeredClient.getClientAuthenticationMethods().iterator().next().getValue()));

        assertTrue(clientEntity.getAuthorizationGrantTypes().stream()
                .map(AuthorizationGrantTypeEntity::getName)
                .collect(Collectors.toSet())
                .contains(registeredClient.getAuthorizationGrantTypes().iterator().next().getValue()));
        assertEquals(ClientSettings.withSettings(clientEntity.getClientSettings()).build(), registeredClient.getClientSettings());
        assertEquals(TokenSettings.withSettings(clientEntity.getTokenSettings()).build(), registeredClient.getTokenSettings());

    }

    @Test
    @DisplayName("RegisteredClient 저장시 null 인자로 예외 발생")
    void saveWithNullArgument() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> jpaRegisteredClientService.save(null));
    }

    @Test
    @DisplayName("findById 로 저장된 RegisteredClient 조회 테스트")
    void findById() {
        //given
        RegisteredClient registeredClient = clientEntity.toObject();
        jpaRegisteredClientService.save(registeredClient);

        //when
        RegisteredClient client = jpaRegisteredClientService.findById(registeredClient.getId());

        //then
        assertNotNull(client);
        assertEquals(registeredClient.getId(), client.getId());
    }


    @Test
    void findByClientId() {
        //given
        RegisteredClient registeredClient = clientEntity.toObject();
        jpaRegisteredClientService.save(registeredClient);

        //when
        RegisteredClient client = jpaRegisteredClientService.findByClientId(registeredClient.getClientId());

        //then
        assertNotNull(client);
        assertEquals(registeredClient.getId(), client.getId());

    }


}