package click.porito.commons.auth2authserver.domains.oauth2_client.repository;

import click.porito.commons.auth2authserver.domains.oauth2_client.entity.static_entity.AuthenticationMethod;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.Set;

public interface AuthenticationMethodRepository extends JpaRepository<AuthenticationMethod,Long> {
    Set<AuthenticationMethod> findByNameIn(Collection<String> values);
}
