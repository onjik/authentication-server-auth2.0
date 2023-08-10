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
import lombok.RequiredArgsConstructor;
import org.springframework.lang.Nullable;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class RegisteredClientRepositoryService implements RegisteredClientRepository {

    private final ClientRepository clientRepository;
    private final ScopeRepository scopeRepository;
    private final AuthenticationMethodRepository authenticationMethodRepository;
    private final GrantTypeRepository grantTypeRepository;

    @Override
    public void save(RegisteredClient registeredClient) {
        Client client = toEntity(registeredClient); //read only
        clientRepository.save(client); // write
    }

    @Nullable
    @Override
    public RegisteredClient findById(String id) {
        Client client = clientRepository.findById(id).orElse(null);
        if (client == null) return null;
        return toDto(client);
    }

    @Nullable
    @Override
    public RegisteredClient findByClientId(String clientId) {
        Client client = clientRepository.findByClientId(clientId).orElse(null);
        if (client == null) return null;
        return toDto(client);
    }

    private Client toEntity(RegisteredClient registeredClient){
        // column mapping
        Client client = Client.builder()
                .id(registeredClient.getId())
                .clientName(registeredClient.getClientName())
                .clientId(registeredClient.getClientId())
                .clientIdIssuedAt(registeredClient.getClientIdIssuedAt())
                .clientSecret(registeredClient.getClientSecret())
                .clientSecretExpiresAt(registeredClient.getClientSecretExpiresAt())
                .clientSettings(registeredClient.getClientSettings().getSettings())
                .tokenSettings(registeredClient.getTokenSettings().getSettings())
                .build();

        /*
        TODO: 여러번 조회가 동기적으로 일어나는 문제가 있음 다음과 같은 해결책을 고민중
        1. JPA 2차 캐시
        2. 비동기 처리(병렬 조회)
        3. Spring 에서 제공하는 캐싱 -> 서비스 부분이나, 정적 엔티티들을 미리 캐싱해두는 서비스를 이용
         */
        // relationship mapping
        //redirect uri
        registeredClient.getRedirectUris().forEach(redirectUri -> {
            client.getRedirectUris().add(new RedirectUri(redirectUri,client));
        });

        // scopes
        Set<Scope> scopes = scopeRepository.findByNameIn(registeredClient.getScopes());
        client.getScopes().addAll(scopes);

        // authentication methods
        Set<String> methodValues = registeredClient.getClientAuthenticationMethods().stream()
                .map(ClientAuthenticationMethod::getValue)
                .collect(Collectors.toSet());
        Set<AuthenticationMethod> methods = authenticationMethodRepository.findByNameIn(methodValues);
        client.getAuthenticationMethods().addAll(methods);

        // grant types
        Set<String> grantTypeValues = registeredClient.getAuthorizationGrantTypes().stream()
                .map(AuthorizationGrantType::getValue)
                .collect(Collectors.toSet());
        Set<OAuth2AuthorizationGrantType> grantTypes = grantTypeRepository.findByNameIn(grantTypeValues);
        client.getAuthorizationGrantTypes().addAll(grantTypes);

        return client;
    }

    private RegisteredClient toDto(Client client){
        Set<AuthorizationGrantType> grantTypes = client.getAuthorizationGrantTypes().stream()
                .map(OAuth2AuthorizationGrantType::getName)
                .map(AuthorizationGrantType::new)
                .collect(Collectors.toSet());
        // column mapping
        return RegisteredClient.withId(client.getId().toString())
                .clientId(client.getClientId())
                .clientIdIssuedAt(client.getClientIdIssuedAt())
                .clientSecret(client.getClientSecret())
                .clientSecretExpiresAt(client.getClientSecretExpiresAt())
                .clientName(client.getClientName())
                .clientAuthenticationMethods(methodSet -> {
                    client.getAuthenticationMethods().stream()
                            .map(AuthenticationMethod::getName)
                            .map(ClientAuthenticationMethod::new)
                            .forEach(methodSet::add);
                })
                .authorizationGrantTypes(grantSet -> {
                    client.getAuthorizationGrantTypes().stream()
                            .map(OAuth2AuthorizationGrantType::getName)
                            .map(AuthorizationGrantType::new)
                            .forEach(grantSet::add);
                })
                .redirectUris(redirectUriSet -> {
                    client.getRedirectUris().stream()
                            .map(RedirectUri::getUri)
                            .forEach(redirectUriSet::add);
                })
                .scopes(scopeSet -> {
                    client.getScopes().stream()
                            .map(Scope::getName)
                            .forEach(scopeSet::add);
                })
                .clientSettings(ClientSettings.withSettings(client.getClientSettings()).build())
                .tokenSettings(TokenSettings.withSettings(client.getTokenSettings()).build())
                .build();
    }

}
