package click.porito.commons.auth2authserver.domains.oauth2_client.service;

import click.porito.commons.auth2authserver.domains.oauth2_client.entity.Client;
import click.porito.commons.auth2authserver.domains.oauth2_client.entity.RedirectUri;
import click.porito.commons.auth2authserver.domains.oauth2_client.entity.static_entity.AuthenticationMethod;
import click.porito.commons.auth2authserver.domains.oauth2_client.entity.static_entity.OAuth2AuthorizationGrantType;
import click.porito.commons.auth2authserver.domains.oauth2_client.entity.static_entity.Scope;
import click.porito.commons.auth2authserver.domains.oauth2_client.repository.AuthenticationMethodRepository;
import click.porito.commons.auth2authserver.domains.oauth2_client.repository.ClientRepository;
import click.porito.commons.auth2authserver.domains.oauth2_client.repository.GrantTypeRepository;
import click.porito.commons.auth2authserver.domains.oauth2_client.repository.ScopeRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@DataJpaTest
class RegisteredClientRepositoryServiceTest {

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private ScopeRepository scopeRepository;

    @Autowired
    private AuthenticationMethodRepository authenticationMethodRepository;

    @Autowired
    private GrantTypeRepository grantTypeRepository;


    private RegisteredClientRepositoryService registeredClientRepositoryService;

    @BeforeEach
    void setUp() {
        registeredClientRepositoryService = new RegisteredClientRepositoryService(clientRepository, scopeRepository, authenticationMethodRepository, grantTypeRepository);
        Scope photo = new Scope("localhost:8080/photo", "photo");
        scopeRepository.save(photo);
        AuthenticationMethod authenticationMethod = new AuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_JWT.getValue());
        authenticationMethodRepository.save(authenticationMethod);
        OAuth2AuthorizationGrantType oAuth2AuthorizationGrantType = new OAuth2AuthorizationGrantType(AuthorizationGrantType.JWT_BEARER.getValue());
        grantTypeRepository.save(oAuth2AuthorizationGrantType);

    }

    @Test
    void save() {
        //given
        RegisteredClient registeredClient = given();

        //when
        registeredClientRepositoryService.save(registeredClient);

        //then
        Optional<Client> savedClient = clientRepository.findById(registeredClient.getId());
        Client client = savedClient.orElseGet(Assertions::fail);
        assertTrue(client.getRedirectUris().stream()
                .map(RedirectUri::getUri)
                .collect(Collectors.toSet())
                .containsAll(registeredClient.getRedirectUris()));
        assertTrue(client.getScopes().stream()
                .map(Scope::getName)
                .collect(Collectors.toSet())
                .containsAll(registeredClient.getScopes()));
        assertTrue(client.getAuthenticationMethods().stream()
                .map(AuthenticationMethod::getName)
                .collect(Collectors.toSet())
                .contains(registeredClient.getClientAuthenticationMethods().iterator().next().getValue()));

        assertTrue(client.getAuthorizationGrantTypes().stream()
                .map(OAuth2AuthorizationGrantType::getName)
                .collect(Collectors.toSet())
                .contains(registeredClient.getAuthorizationGrantTypes().iterator().next().getValue()));
        assertEquals(ClientSettings.withSettings(client.getClientSettings()).build(), registeredClient.getClientSettings());
        assertEquals(TokenSettings.withSettings(client.getTokenSettings()).build(), registeredClient.getTokenSettings());

        //TODO : 테스트 케이스 추가

    }


    private RegisteredClient given(){
        ClientSettings clientSettings = ClientSettings.builder()
                .requireAuthorizationConsent(true)
                .build();
        TokenSettings tokenSettings = TokenSettings.builder().accessTokenTimeToLive(Duration.ofDays(7)).build();
        RegisteredClient registeredClient = RegisteredClient.withId(UUID.randomUUID().toString())
                .clientId("client")
                .clientIdIssuedAt(Instant.now())
                .clientSecret("secret")
                .clientSecretExpiresAt(Instant.now())
                .clientSettings(clientSettings)
                .tokenSettings(tokenSettings)
                .redirectUri("http://localhost:8080/authorized")
                .scope("photo")
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_JWT)
                .authorizationGrantType(AuthorizationGrantType.JWT_BEARER)
                .build();
        return registeredClient;
    }

}