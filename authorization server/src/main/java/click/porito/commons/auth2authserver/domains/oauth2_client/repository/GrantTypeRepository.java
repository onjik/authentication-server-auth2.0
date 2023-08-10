package click.porito.commons.auth2authserver.domains.oauth2_client.repository;

import click.porito.commons.auth2authserver.domains.oauth2_client.entity.static_entity.OAuth2AuthorizationGrantType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.Set;

public interface GrantTypeRepository extends JpaRepository<OAuth2AuthorizationGrantType, Long> {
    Set<OAuth2AuthorizationGrantType> findByNameIn(Collection<String> names);
}
