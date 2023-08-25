package click.porito.commons.auth2authserver.domains.oauth2_client.entity.static_entity;

import click.porito.commons.auth2authserver.common.util.ConstantEntity;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;

/**
 * ClientAuthenticationMethod 정보를 담는 정적인 Entity
 * @see ClientAuthenticationMethod
 */
@ConstantEntity
@Entity @Table(name = "authentication_method")
@Getter
@Setter
@EqualsAndHashCode(of = {"id", "name"})
@NoArgsConstructor
public class ClientAuthenticationMethodEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name", unique = true,
            length = 255, nullable = false)
    private String name;

    @PrePersist
    public void prePersist() {
        this.name = this.name.toUpperCase();
    }

    public ClientAuthenticationMethodEntity(String name) {
        this.name = name;
    }

    public static ClientAuthenticationMethodEntity of(ClientAuthenticationMethod authenticationMethod) {
        ClientAuthenticationMethodEntity method = new ClientAuthenticationMethodEntity();
        method.setName(authenticationMethod.getValue());
        return method;
    }

    public ClientAuthenticationMethod toClientAuthenticationMethod() {
        return new ClientAuthenticationMethod(name);

    }


}
