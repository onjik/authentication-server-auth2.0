package click.porito.commons.auth2authserver.domains.oauth2_client.repository;

import click.porito.commons.auth2authserver.domains.oauth2_client.entity.static_entity.AuthenticationMethod;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@DataJpaTest
class AuthenticationMethodRepositoryTest {

    @Autowired
    AuthenticationMethodRepository authenticationMethodRepository;

    @Test
    void findByNameIn() {
        // given
        AuthenticationMethod basic = new AuthenticationMethod("client_secret_basic");
        AuthenticationMethod jwt = new AuthenticationMethod("client_secret_jwt");
        authenticationMethodRepository.save(basic);
        authenticationMethodRepository.save(jwt);

        // when
        Set<AuthenticationMethod> result = authenticationMethodRepository.findByNameIn(List.of(basic.getName(), jwt.getName()));

        // then
        assertEquals(2, result.size());
        assertTrue(result.contains(basic));
        assertTrue(result.contains(jwt));
    }


}