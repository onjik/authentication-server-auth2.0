package click.porito.commons.auth2authserver.domains.oauth2_client.repository;

import click.porito.commons.auth2authserver.domains.oauth2_client.entity.static_entity.AuthorizationGrantTypeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;

public interface AuthorizationGrantTypeRepository extends JpaRepository<AuthorizationGrantTypeEntity, Long> {
    Set<AuthorizationGrantTypeEntity> findByNameIn(Collection<String> names);

    Optional<AuthorizationGrantTypeEntity> findByName(String name);
}
