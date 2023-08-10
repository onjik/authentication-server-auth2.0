package click.porito.commons.auth2authserver.domains.oauth2_client.entity.static_entity;

import click.porito.commons.auth2authserver.global.ConstantEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.util.Assert;

/**
 * ClientAuthenticationMethod 정보를 담는 정적인 Entity
 * @see ClientAuthenticationMethod
 */
@ConstantEntity
@Entity @Table(name = "authentication_method")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AuthenticationMethod {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name", unique = true,
            length = 255, nullable = false)
    private String name;

    public AuthenticationMethod(String name) {
        Assert.hasText(name, "name must not be empty");
        this.name = name;
    }

    public static AuthenticationMethod of(ClientAuthenticationMethod authenticationMethod) {
        return new AuthenticationMethod(authenticationMethod.getValue());
    }

    public ClientAuthenticationMethod toClientAuthenticationMethod() {
        return new ClientAuthenticationMethod(name);

    }


}
