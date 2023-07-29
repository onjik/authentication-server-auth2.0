package click.porito.commons.auth2authserver.auth2.model.entity;

import click.porito.commons.auth2authserver.auth2.model.constant.Gender;
import org.junit.jupiter.api.BeforeEach;
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
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
class ServiceTest {

    @Autowired
    private TestEntityManager testEntityManager;

    private Service service;
    private User user;

    @BeforeEach
    void setUp() {
        // given
        String email = "test@google.com";
        String name = "test name";
        Gender gender = Gender.M;
        LocalDate birthDate = LocalDate.of(1999, 1, 1);
        Password password = Password.of("test");
        user = User.builder(email,name,gender,birthDate)
                .setCredential(password)
                .setRole(Role.of(Role.Type.ROLE_USER))
                .build();

        RegisteredRedirection sampleUrl = RegisteredRedirection.of("http://localhost:8080/redirection");
        RegisteredRedirection sampleUrl2 = RegisteredRedirection.of("http://localhost:8080/endpoint");

        service = new Service("test", user, "test", Set.of(sampleUrl, sampleUrl2));
    }

    @Test
    @DisplayName("array 타입이 sql으로 잘 변환되는지 테스트")
    void arrayTypeSqlTest(){

        // when
        testEntityManager.persist(user);
        testEntityManager.persist(service);

        // then
        Service findService = testEntityManager.find(Service.class, service.getId());
        assertEquals(service.getAllowedRedirection(), findService.getAllowedRedirection());

    }

    @Test
    @DisplayName("prePersist로 서비스 키가 잘 등록되는지 체크")
    void prePersistTest() {
        testEntityManager.persist(user);
        testEntityManager.persist(service);

        Service findService = testEntityManager.find(Service.class, service.getId());
        assertNotNull(findService.getServiceKey());
    }


}