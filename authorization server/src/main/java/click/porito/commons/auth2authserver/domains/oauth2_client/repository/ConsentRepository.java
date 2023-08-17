package click.porito.commons.auth2authserver.domains.oauth2_client.repository;

import click.porito.commons.auth2authserver.domains.oauth2_client.entity.OAuth2AuthorizationConsentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConsentRepository extends JpaRepository<OAuth2AuthorizationConsentEntity, Long> {
    OAuth2AuthorizationConsentEntity findByClientIdAndResourceOwnerId(String clientId, String resourceOwnerId);

    Long deleteByClientIdAndResourceOwnerId(String clientId, String resourceOwnerId);
}
