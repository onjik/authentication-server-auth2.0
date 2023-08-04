package click.porito.commons.auth2authserver.entity.constant;

import click.porito.commons.auth2authserver.ConstantEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.util.Assert;

@ConstantEntity
@Entity @Table(name = "scope")
@Getter
@EqualsAndHashCode(of = "name")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Scope {

    public static final String SCOPE_PREFIX = "SCOPE_";

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "uri_endpoint")
    private String uriEndpoint;

    @Column(name = "name", unique = true,
            length = 255, nullable = false)
    private String name;



    public Scope(@NonNull String uriEndpoint, @NonNull String name) {
        Assert.hasText(uriEndpoint, "uriEndpoint cannot be null");
        Assert.hasText(name, "name cannot be null");
        this.uriEndpoint = uriEndpoint;
        this.name = name;
    }

    public String getScopePrefixedName() {
        return SCOPE_PREFIX + name;
    }


}
