package click.porito.commons.auth2authserver.domains.resource_owner.repository;

import click.porito.commons.auth2authserver.domains.resource_owner.entity.static_entity.RoleEntity;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import test_util.TestEntityFactory;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@DataJpaTest
class RoleRepositoryTest {

    @Autowired
    RoleRepository roleRepository;

    @Test
    void findFirstByName(){
        //given
        RoleEntity originalEntity = TestEntityFactory.getRoleEntity();
        roleRepository.save(originalEntity);
        //when
        RoleEntity resultEntity = roleRepository.findFirstByNameIgnoreCase(originalEntity.getName())
                .orElseGet(Assertions::fail);

        //then
        assertEquals(originalEntity.getId(),resultEntity.getId());
        assertEquals(originalEntity.getName(), resultEntity.getName());
        assertEquals(originalEntity.getPriority(), resultEntity.getPriority());
    }

}