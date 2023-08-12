package click.porito.commons.auth2authserver.domains.oauth2_client.repository;

import click.porito.commons.auth2authserver.domains.oauth2_client.entity.OAuth2AuthorizationEntity;
import click.porito.commons.auth2authserver.domains.oauth2_client.entity.token.AccessTokenEntity;
import click.porito.commons.auth2authserver.domains.oauth2_client.entity.token.CommonTokenEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import test_util.TestEntityFactory;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@DataJpaTest
class CommonTokenRepositoryTest {

    @PersistenceContext
    EntityManager em;

    @Autowired
    CommonTokenRepository commonTokenRepository;

    @Test
    @DisplayName("Inheritance Entity 도 repository 로 조회가 가능한지 테스트")
    void inheritanceEntityRepositoryAvailable(){
        //given
        OAuth2AuthorizationEntity authorization = TestEntityFactory.getSavedOAuth2AuthorizationEntity(em);

        AccessTokenEntity accessTokenEntity = TestEntityFactory.getAccessTokenEntity(authorization);
        em.persist(accessTokenEntity);

        //when
        CommonTokenEntity commonTokenEntity = commonTokenRepository.findById(accessTokenEntity.getId())
                .orElseGet(() -> fail("조회 실패"));

        //then
        assertTrue(commonTokenEntity instanceof AccessTokenEntity accessToken);
        String type = accessTokenEntity.getTokenType();
        assertNotNull(accessTokenEntity.getTokenType());
        assertEquals(accessTokenEntity.getAuthorization(), authorization);

    }

}