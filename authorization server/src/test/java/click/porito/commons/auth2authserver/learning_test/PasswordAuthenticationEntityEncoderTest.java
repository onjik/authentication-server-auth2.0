package click.porito.commons.auth2authserver.learning_test;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

public class PasswordAuthenticationEntityEncoderTest {

    @Test
    void passwordEncoderResultTest() {
        String password = "a".repeat(30);
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encoded = passwordEncoder.encode(password);
        System.out.println(encoded);
        System.out.println(encoded.length());
    }
}
