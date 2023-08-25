package click.porito.commons.auth2authserver.common.util;

import click.porito.commons.auth2authserver.domains.resource_owner.entity.static_entity.RoleEntity;
import click.porito.commons.auth2authserver.domains.resource_owner.repository.RoleRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import test_util.TestEntityFactory;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@DisplayName("RepositoryHolder 테스트")
@Transactional
@SpringBootTest
class RepositoryHolderTest {

    @Autowired
    RepositoryHolder repositoryHolder;

    @PersistenceContext
    EntityManager entityManager;


    @Test
    @DisplayName("RoleRepository 정상적으로 로드 후 메서드 호출")
    void loadRepository() {
        //given
        RoleEntity roleEntity = TestEntityFactory.getRoleEntity();
        entityManager.persist(roleEntity);

        //when
        RoleRepository repository = repositoryHolder.getRepository(RoleRepository.class);

        //then
        assertNotNull(repository);

        //call method
        RoleEntity result = repository.findFirstByNameIgnoreCase(roleEntity.getName())
                .orElseGet(() -> fail());

        //then
        assertNotNull(result);
        assertEquals(roleEntity.getName(), result.getName());


    }



}