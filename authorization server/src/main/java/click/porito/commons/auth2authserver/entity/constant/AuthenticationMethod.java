package click.porito.commons.auth2authserver.entity.constant;

import click.porito.commons.auth2authserver.ConstantEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
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
@Getter @EqualsAndHashCode(of = "name")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AuthenticationMethod {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name", unique = true,
            length = 255, nullable = false)
    private String name;

    @Transient
    private ClientAuthenticationMethod authenticationMethod;

    public AuthenticationMethod(@NotNull ClientAuthenticationMethod authenticationMethod) {
        Assert.notNull(name, "name cannot be null");
        this.name = authenticationMethod.getValue();
        this.authenticationMethod = authenticationMethod; //for performance
    }

    public static AuthenticationMethod from(@NotNull ClientAuthenticationMethod authenticationMethod) {
        return new AuthenticationMethod(authenticationMethod);
    }

    public ClientAuthenticationMethod toClientAuthenticationMethod() {
        if (authenticationMethod == null) {
            authenticationMethod = new ClientAuthenticationMethod(name);
        }
        return authenticationMethod;
    }

}
