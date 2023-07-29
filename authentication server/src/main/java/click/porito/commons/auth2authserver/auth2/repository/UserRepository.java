package click.porito.commons.auth2authserver.auth2.repository;

import click.porito.commons.auth2authserver.auth2.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {
}
