package click.porito.commons.auth2authserver.auth2.repository;

import click.porito.commons.auth2authserver.auth2.model.entity.RememberMeToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TokenRepository extends JpaRepository<RememberMeToken,Long> {
}
