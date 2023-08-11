package click.porito.commons.auth2authserver.domains.oauth2_client.repository;

import click.porito.commons.auth2authserver.domains.oauth2_client.entity.static_entity.AuthorizationGrantTypeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.Set;

public interface GrantTypeRepository extends JpaRepository<AuthorizationGrantTypeEntity, Long> {
    Set<AuthorizationGrantTypeEntity> findByNameIn(Collection<String> names);
}
