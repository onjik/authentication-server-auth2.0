package click.porito.commons.auth2authserver.domains.oauth2_client.entity.token;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.Instant;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@DataJpaTest
class CreationTimeGenerationTest {

    @PersistenceContext
    private EntityManager entityManager;

    @Test
    void creationTimeGenerationTest() {
        //given
        RefreshToken refreshToken = new RefreshToken(new HashMap<>(), "refresh_token");

        //when
        entityManager.persist(refreshToken);
        entityManager.flush();

        //then
        Instant issuedAt = refreshToken.getIssuedAt();
        assertNotNull(issuedAt);
        assertTrue(issuedAt.isBefore(Instant.now()));

    }

}