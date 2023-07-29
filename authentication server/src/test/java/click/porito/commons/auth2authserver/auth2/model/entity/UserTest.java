package click.porito.commons.auth2authserver.auth2.model.entity;

import click.porito.commons.auth2authserver.auth2.model.constant.Gender;
import click.porito.commons.auth2authserver.auth2.model.entity.Password;
import click.porito.commons.auth2authserver.auth2.model.entity.Role;
import click.porito.commons.auth2authserver.auth2.model.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
class UserTest {

    @Autowired
    private TestEntityManager testEntityManager;

    @Test
    @DisplayName("PrePersist를 통한 registeredDate 값 설정 테스트")
    void prePersistTest() {
        // given
        String email = "test@google.com";
        String name = "test name";
        Gender gender = Gender.M;
        LocalDate birthDate = LocalDate.of(1999, 1, 1);
        Password password = Password.of("test");
        User user = User.builder(email,name,gender,birthDate)
                .setCredential(password)
                .setRole(Role.of(Role.Type.ROLE_USER))
                .build();

        // when
        testEntityManager.persist(user);

        // then
        assertNotNull(user.getRegisteredDate());
    }



}