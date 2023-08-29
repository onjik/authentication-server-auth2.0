package click.porito.commons.auth2authserver.domains.oauth2_client.service;

import click.porito.commons.auth2authserver.common.util.RepositoryHolder;
import click.porito.commons.auth2authserver.domains.oauth2_client.entity.ClientEntity;
import click.porito.commons.auth2authserver.domains.oauth2_client.entity.RedirectUriEntity;
import click.porito.commons.auth2authserver.domains.oauth2_client.entity.static_entity.AuthorizationGrantTypeEntity;
import click.porito.commons.auth2authserver.domains.oauth2_client.entity.static_entity.ClientAuthenticationMethodEntity;
import click.porito.commons.auth2authserver.domains.oauth2_client.entity.static_entity.ScopeEntity;
import click.porito.commons.auth2authserver.domains.oauth2_client.repository.AuthenticationMethodRepository;
import click.porito.commons.auth2authserver.domains.oauth2_client.repository.AuthorizationGrantTypeRepository;
import click.porito.commons.auth2authserver.domains.oauth2_client.repository.ClientRepository;
import click.porito.commons.auth2authserver.domains.oauth2_client.repository.ScopeRepository;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.lang.Nullable;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Set;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.stream.Collectors;

public class JpaRegisteredClientService implements RegisteredClientRepository {

    private final ClientRepository clientRepository;
    private final ScopeRepository scopeRepository;
    private final AuthenticationMethodRepository authenticationMethodRepository;
    private final AuthorizationGrantTypeRepository authorizationGrantTypeRepository;

    public JpaRegisteredClientService(RepositoryHolder repositoryHolder) {
        this.clientRepository = repositoryHolder.getRepository(ClientRepository.class);
        this.scopeRepository = repositoryHolder.getRepository(ScopeRepository.class);
        this.authenticationMethodRepository = repositoryHolder.getRepository(AuthenticationMethodRepository.class);
        this.authorizationGrantTypeRepository = repositoryHolder.getRepository(AuthorizationGrantTypeRepository.class);
    }

    @Override
    public void save(RegisteredClient registeredClient) {
        Assert.notNull(registeredClient, "registeredClient cannot be null");
        ClientEntity clientEntity = toEntity(registeredClient); //read only
        clientRepository.save(clientEntity); // write
    }

    @Nullable
    @Override
    public RegisteredClient findById(String id) {
        ClientEntity clientEntity = clientRepository
                .findById(id).orElse(null);
        if (clientEntity == null) return null;
        return clientEntity.toObject();
    }

    @Nullable
    @Override
    public RegisteredClient findByClientId(String clientId) {
        ClientEntity clientEntity = clientRepository
                .findByClientId(clientId).orElse(null);
        if (clientEntity == null) return null;
        return clientEntity.toObject();
    }

    private ClientEntity toEntity(RegisteredClient registeredClient){
        // column mapping
        ClientEntity clientEntity = ClientEntity.builder()
                .id(registeredClient.getId())
                .clientId(registeredClient.getClientId())
                .clientName(registeredClient.getClientName())
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

            clientEntity.getRedirectUris().add(new RedirectUriEntity(redirectUri, clientEntity));
        });

        // scopeEntities
        Set<ScopeEntity> scopeEntities = loadEntity(
                registeredClient.getScopes(),
                scopeRepository::findByNameIgnoreCaseIn,
                (scope, scopeEntity) -> scope.equals(scopeEntity.getName())
        );
        clientEntity.getScopes().addAll(scopeEntities);

        // authentication methods
        Set<String> methodValues = registeredClient.getClientAuthenticationMethods().stream()
                .map(ClientAuthenticationMethod::getValue)
                .collect(Collectors.toSet());
        Set<ClientAuthenticationMethodEntity> methodEntities = loadEntity(
                methodValues,
                authenticationMethodRepository::findByNameIn,
                (method, methodEntity) -> method.equals(methodEntity.getName())
        );
        clientEntity.getClientAuthenticationMethods().addAll(methodEntities);

        // grant types
        Set<String> grantTypeValues = registeredClient.getAuthorizationGrantTypes().stream()
                .map(AuthorizationGrantType::getValue)
                .collect(Collectors.toSet());
        Set<AuthorizationGrantTypeEntity> grantTypeEntities = loadEntity(
                grantTypeValues,
                authorizationGrantTypeRepository::findByNameIn,
                (grantType, grantTypeEntity) -> grantType.equals(grantTypeEntity.getName())
        );
        clientEntity.getAuthorizationGrantTypes().addAll(grantTypeEntities);

        return clientEntity;
    }

    private <BY,E> Set<E> loadEntity(Set<BY> loadBy,
                                     Function<Set<BY>, Set<E>> loadFunction,
                                     BiPredicate<BY,E> isMatch) {
        Set<E> entitySet = loadFunction.apply(loadBy);
        if (entitySet.size() != loadBy.size()) {
            //find which is not found
            List<BY> notFound = loadBy.stream()
                    .filter(by -> entitySet.stream().noneMatch(entity -> isMatch.test(by, entity)))
                    .toList();
            throw new DataRetrievalFailureException("not found entity (entities) -> " + notFound);
        }
        return entitySet;
    }


}
