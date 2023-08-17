package click.porito.commons.auth2authserver.domains.resource_owner.repository;

import click.porito.commons.auth2authserver.domains.resource_owner.entity.static_entity.RoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;

@Repository
public interface RoleRepository extends JpaRepository<RoleEntity, Long> {
    Optional<RoleEntity> findFirstByNameIgnoreCase(String name);

    Set<RoleEntity> findByNameIgnoreCaseIn(Collection<String> names);
}
