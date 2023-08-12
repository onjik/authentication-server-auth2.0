package click.porito.commons.auth2authserver.domains.oauth2_client.entity.token;

import click.porito.commons.auth2authserver.domains.oauth2_client.entity.OAuth2AuthorizationEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import test_util.TestEntityFactory;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static test_util.TestEntityFactory.getSavedOAuth2AuthorizationEntity;
import static test_util.TestEntityFactory.getRefreshTokenEntity;

@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@DataJpaTest
class CreationTimeGenerationTest {

    @PersistenceContext
    private EntityManager entityManager;

    @Test
    void creationTimeGenerationTest() {
        //given
        OAuth2AuthorizationEntity oAuth2Authorization = TestEntityFactory.getSavedOAuth2AuthorizationEntity(entityManager);
        RefreshTokenEntity refreshToken = getRefreshTokenEntity(oAuth2Authorization);
        //when
        entityManager.persist(refreshToken);
        entityManager.flush();

        //then
        Instant issuedAt = refreshToken.getIssuedAt();
        assertNotNull(issuedAt);
        assertTrue(issuedAt.isBefore(Instant.now()));

    }

}