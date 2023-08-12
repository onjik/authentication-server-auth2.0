package click.porito.commons.auth2authserver.domains.oauth2_client.repository;

import click.porito.commons.auth2authserver.domains.oauth2_client.entity.token.CommonTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Set;

public interface CommonTokenRepository extends JpaRepository<CommonTokenEntity, Long> {

    Set<CommonTokenEntity> findAllByValue(String value);
}
