package click.porito.commons.auth2authserver.domains.resource_owner.entity.authentication;

import click.porito.commons.auth2authserver.domains.resource_owner.entity.ResourceOwnerEntity;
import click.porito.commons.auth2authserver.domains.resource_owner.entity.static_entity.RoleEntity;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static test_util.TestEntityFactory.*;

@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@DataJpaTest
class AccountEntityTest {

    @Autowired
    EntityManager em;

    @Nested
    @DisplayName("식별 관계 id 테스트")
    class PrimaryKeyJoinColumnTest {

        @Test
        @DisplayName("one to one 관계로 id 값 공유하기")
        void saveTest(){
            // given
            RoleEntity role = getRoleEntity();
            em.persist(role);
            ResourceOwnerEntity resourceOwner = getResourceOwnerEntity(role);
            em.persist(resourceOwner);
            AccountEntity accountEntity = getAccountEntity(resourceOwner);

            // when
            em.persist(accountEntity);
            em.flush();
            // then
            em.find(AccountEntity.class, resourceOwner.getId());

        }
    }

}