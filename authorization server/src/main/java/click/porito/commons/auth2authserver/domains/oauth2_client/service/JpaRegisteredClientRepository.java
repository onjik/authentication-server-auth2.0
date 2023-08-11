package click.porito.commons.auth2authserver.domains.oauth2_client.service;

import click.porito.commons.auth2authserver.domains.oauth2_client.entity.RegisteredClientEntity;
import click.porito.commons.auth2authserver.domains.oauth2_client.entity.RedirectUriEntity;
import click.porito.commons.auth2authserver.domains.oauth2_client.entity.static_entity.ClientAuthenticationMethodEntity;
import click.porito.commons.auth2authserver.domains.oauth2_client.entity.static_entity.AuthorizationGrantTypeEntity;
import click.porito.commons.auth2authserver.domains.oauth2_client.entity.static_entity.ScopeEntity;
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
import org.springframework.util.Assert;

import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class JpaRegisteredClientRepository implements RegisteredClientRepository {

    private final ClientRepository clientRepository;
    private final ScopeRepository scopeRepository;
    private final AuthenticationMethodRepository authenticationMethodRepository;
    private final GrantTypeRepository grantTypeRepository;

    @Override
    public void save(RegisteredClient registeredClient) {
        Assert.notNull(registeredClient, "registeredClient cannot be null");
        RegisteredClientEntity registeredClientEntity = toEntity(registeredClient); //read only
        clientRepository.save(registeredClientEntity); // write
    }

    @Nullable
    @Override
    public RegisteredClient findById(String id) {
        RegisteredClientEntity registeredClientEntity = clientRepository.findById(id).orElse(null);
        if (registeredClientEntity == null) return null;
        return toDto(registeredClientEntity);
    }

    @Nullable
    @Override
    public RegisteredClient findByClientId(String clientId) {
        RegisteredClientEntity registeredClientEntity = clientRepository.findByClientId(clientId).orElse(null);
        if (registeredClientEntity == null) return null;
        return toDto(registeredClientEntity);
    }

    private RegisteredClientEntity toEntity(RegisteredClient registeredClient){
        // column mapping
        RegisteredClientEntity registeredClientEntity = RegisteredClientEntity.builder()
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
            registeredClientEntity.getRedirectUrisEntities().add(new RedirectUriEntity(redirectUri, registeredClientEntity));
        });

        // scopeEntities
        Set<ScopeEntity> scopeEntities = scopeRepository.findByNameIn(registeredClient.getScopes());
        registeredClientEntity.getScopeEntities().addAll(scopeEntities);

        // authentication methods
        Set<String> methodValues = registeredClient.getClientAuthenticationMethods().stream()
                .map(ClientAuthenticationMethod::getValue)
                .collect(Collectors.toSet());
        Set<ClientAuthenticationMethodEntity> methods = authenticationMethodRepository.findByNameIn(methodValues);
        registeredClientEntity.getClientAuthenticationMethodEntities().addAll(methods);

        // grant types
        Set<String> grantTypeValues = registeredClient.getAuthorizationGrantTypes().stream()
                .map(AuthorizationGrantType::getValue)
                .collect(Collectors.toSet());
        Set<AuthorizationGrantTypeEntity> grantTypes = grantTypeRepository.findByNameIn(grantTypeValues);
        registeredClientEntity.getAuthorizationGrantTypes().addAll(grantTypes);

        return registeredClientEntity;
    }

    private RegisteredClient toDto(RegisteredClientEntity registeredClientEntity){
        Set<AuthorizationGrantType> grantTypes = registeredClientEntity.getAuthorizationGrantTypes().stream()
                .map(AuthorizationGrantTypeEntity::getName)
                .map(AuthorizationGrantType::new)
                .collect(Collectors.toSet());
        // column mapping
        return RegisteredClient.withId(registeredClientEntity.getId().toString())
                .clientId(registeredClientEntity.getClientId())
                .clientIdIssuedAt(registeredClientEntity.getClientIdIssuedAt())
                .clientSecret(registeredClientEntity.getClientSecret())
                .clientSecretExpiresAt(registeredClientEntity.getClientSecretExpiresAt())
                .clientName(registeredClientEntity.getClientName())
                .clientAuthenticationMethods(methodSet -> {
                    registeredClientEntity.getClientAuthenticationMethodEntities().stream()
                            .map(ClientAuthenticationMethodEntity::getName)
                            .map(ClientAuthenticationMethod::new)
                            .forEach(methodSet::add);
                })
                .authorizationGrantTypes(grantSet -> {
                    registeredClientEntity.getAuthorizationGrantTypes().stream()
                            .map(AuthorizationGrantTypeEntity::getName)
                            .map(AuthorizationGrantType::new)
                            .forEach(grantSet::add);
                })
                .redirectUris(redirectUriSet -> {
                    registeredClientEntity.getRedirectUrisEntities().stream()
                            .map(RedirectUriEntity::getUri)
                            .forEach(redirectUriSet::add);
                })
                .scopes(scopeSet -> {
                    registeredClientEntity.getScopeEntities().stream()
                            .map(ScopeEntity::getName)
                            .forEach(scopeSet::add);
                })
                .clientSettings(ClientSettings.withSettings(registeredClientEntity.getClientSettings()).build())
                .tokenSettings(TokenSettings.withSettings(registeredClientEntity.getTokenSettings()).build())
                .build();
    }

}
