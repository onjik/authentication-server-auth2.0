package click.porito.commons.auth2authserver.domains.oauth2_client.repository;

import click.porito.commons.auth2authserver.domains.oauth2_client.entity.ClientEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClientRepository extends JpaRepository<ClientEntity,String> {
    Optional<ClientEntity> findByClientId(String clientId);
}
