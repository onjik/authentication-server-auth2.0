package click.porito.commons.auth2authserver.domains.resource_owner.repository;

import click.porito.commons.auth2authserver.domains.resource_owner.entity.ResourceOwnerEntity;
import click.porito.commons.auth2authserver.domains.resource_owner.entity.authentication.AccountEntity;
import click.porito.commons.auth2authserver.domains.resource_owner.entity.authentication.PasswordAuthenticationEntity;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static test_util.TestEntityFactory.*;

@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@DataJpaTest
class PasswordAuthenticationRepositoryTest {

    @Autowired
    private PasswordAuthenticationRepository passwordAuthenticationRepository;

    @Autowired
    private EntityManager em;

    @Test
    void findByAccountEntity_Email() {
        // given
        ResourceOwnerEntity resourceOwner = getResourceOwnerEntity();
        em.persist(resourceOwner);
        AccountEntity account = getAccountEntity(resourceOwner);
        em.persist(account);
        PasswordAuthenticationEntity passwordAuthentication = getPasswordAuthenticationEntity(account);
        account.addAuthentication(passwordAuthentication);
        //cascade persist

        // when
        PasswordAuthenticationEntity result = passwordAuthenticationRepository.findByAccountEntity_Email(account.getEmail())
                .orElseGet(() -> fail());

        // then
        assertEquals(result.getValue(), passwordAuthentication.getValue());
        assertEquals(result.getAccountEntity().getEmail(), account.getEmail());


    }
}