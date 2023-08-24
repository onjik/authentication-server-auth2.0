package click.porito.commons.auth2authserver.domains.oauth2_client.service;

import click.porito.commons.auth2authserver.domains.oauth2_client.entity.ClientEntity;
import click.porito.commons.auth2authserver.domains.oauth2_client.entity.OAuth2AuthorizationConsentEntity;
import click.porito.commons.auth2authserver.domains.oauth2_client.entity.static_entity.ScopeEntity;
import click.porito.commons.auth2authserver.domains.oauth2_client.repository.ClientRepository;
import click.porito.commons.auth2authserver.domains.oauth2_client.repository.ConsentRepository;
import click.porito.commons.auth2authserver.domains.oauth2_client.repository.ScopeRepository;
import click.porito.commons.auth2authserver.domains.resource_owner.entity.ResourceOwnerEntity;
import click.porito.commons.auth2authserver.domains.resource_owner.entity.static_entity.RoleEntity;
import click.porito.commons.auth2authserver.domains.resource_owner.repository.ResourceOwnerRepository;
import click.porito.commons.auth2authserver.domains.resource_owner.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationConsent;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationConsentService;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class JpaConsentService implements OAuth2AuthorizationConsentService {

    private final static String ROLE_PREFIX = "ROLE_";
    private final static String SCOPE_PREFIX = "SCOPE_";

    private final ConsentRepository consentRepository;
    private final ClientRepository clientRepository;
    private final ResourceOwnerRepository resourceOwnerRepository;
    private final RoleRepository roleRepository;
    private final ScopeRepository scopeRepository;

    @Override
    public void save(OAuth2AuthorizationConsent authorizationConsent) {
        Assert.notNull(authorizationConsent, "authorizationConsent cannot be null");
        //load properties
        String registeredClientId = authorizationConsent.getRegisteredClientId();
        ClientEntity clientEntity = clientRepository.findById(registeredClientId)
                .orElseThrow(() -> new DataRetrievalFailureException("client not found"));
        String principalId = authorizationConsent.getPrincipalName();
        ResourceOwnerEntity resourceOwnerEntity = resourceOwnerRepository.findById(principalId)
                .orElseThrow(() -> new DataRetrievalFailureException("resourceOwner not found"));

        Set<String> scopeNames = authorizationConsent.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .filter(grantedAuthority -> grantedAuthority.startsWith(SCOPE_PREFIX))
                .collect(Collectors.toSet());
        Set<ScopeEntity> scopeEntities = scopeRepository.findByNameIgnoreCaseIn(scopeNames);
        if (scopeEntities.size() != scopeNames.size()) {
            throw new DataRetrievalFailureException("scope not found");
        }

        Set<String> roleNames = authorizationConsent.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .filter(grantedAuthority -> grantedAuthority.startsWith(ROLE_PREFIX))
                .collect(Collectors.toSet());
        Set<RoleEntity> roleEntities = roleRepository.findByNameIgnoreCaseIn(roleNames);
        if (roleEntities.size() != roleNames.size()) {
            throw new DataRetrievalFailureException("role not found");
        }

        //assemble
        OAuth2AuthorizationConsentEntity consentEntity = new OAuth2AuthorizationConsentEntity(clientEntity, resourceOwnerEntity);
        consentEntity.getRoles().addAll(roleEntities);
        consentEntity.getScopes().addAll(scopeEntities);

        //persist
        consentRepository.save(consentEntity);
    }

    @Override
    public void remove(OAuth2AuthorizationConsent authorizationConsent) {
        Assert.notNull(authorizationConsent, "authorizationConsent cannot be null");
        String registeredClientId = authorizationConsent.getRegisteredClientId();
        String principalId = authorizationConsent.getPrincipalName();
        Assert.notNull(registeredClientId, "registeredClientId cannot be null");
        Assert.notNull(principalId, "principalId cannot be null");


        Long deleteCount = consentRepository.deleteByClientIdAndResourceOwnerId(registeredClientId, principalId);
        if (deleteCount != 1) {
            throw new EmptyResultDataAccessException("none exist consent",1);
        }
    }

    @Override
    public OAuth2AuthorizationConsent findById(String registeredClientId, String principalName) {
        Assert.notNull(registeredClientId, "registeredClientId cannot be null");
        Assert.notNull(principalName, "principalName cannot be null");
        OAuth2AuthorizationConsentEntity consentEntity = consentRepository.findByClientIdAndResourceOwnerId(registeredClientId, principalName);
        return Optional.ofNullable(consentEntity).map(OAuth2AuthorizationConsentEntity::toObject).orElse(null);
    }

    /**
     * GrantedAuthority 를 P
     * @param grantedAuthority GrantedAuthority
     * @return GrantType
     * @throws IllegalArgumentException if invalid authority(invalid prefix)
     */
    private GrantType grantTypeClassifier(GrantedAuthority grantedAuthority){
        String authority = grantedAuthority.getAuthority();
        if (authority.startsWith(SCOPE_PREFIX)) {
            return GrantType.SCOPE;
        } else if (authority.startsWith(ROLE_PREFIX)) {
            return GrantType.ROLE;
        } else {
            throw new IllegalArgumentException("invalid authority");
        }
    }

    enum GrantType {
        SCOPE, ROLE;
    }


}