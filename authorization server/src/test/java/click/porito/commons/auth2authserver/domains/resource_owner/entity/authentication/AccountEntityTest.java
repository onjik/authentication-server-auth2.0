package click.porito.commons.auth2authserver.domains.resource_owner.entity.authentication;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@DataJpaTest
class AccountEntityTest {

    @Autowired
    EntityManager em;


}