package click.porito.commons.auth2authserver.domains.resource_owner.repository;

import click.porito.commons.auth2authserver.domains.resource_owner.entity.authentication.PasswordAuthenticationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PasswordAuthenticationRepository extends JpaRepository<PasswordAuthenticationEntity, Long> {

    Optional<PasswordAuthenticationEntity> findByAccountEntity_Email(String email);
}
