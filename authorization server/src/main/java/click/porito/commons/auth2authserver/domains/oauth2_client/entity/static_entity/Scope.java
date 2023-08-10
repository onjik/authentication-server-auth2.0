package click.porito.commons.auth2authserver.domains.oauth2_client.entity.static_entity;

import click.porito.commons.auth2authserver.global.util.ConstantEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.lang.NonNull;
import org.springframework.util.Assert;

@ConstantEntity
@Cacheable @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity @Table(name = "scope")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Scope {

    public static final String SCOPE_PREFIX = "SCOPE_";

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "uri_endpoint", updatable = true)
    private String uriEndpoint;

    @Column(name = "name", unique = true,
            length = 255, nullable = false)
    private String name;

    public String getScopePrefixedName() {
        return SCOPE_PREFIX + name;
    }

    public void setUriEndpoint(String uriEndpoint) {
        this.uriEndpoint = uriEndpoint;
    }

    public Scope(@NonNull String uriEndpoint, @NonNull String name) {
        Assert.hasText(uriEndpoint, "uriEndpoint cannot be null");
        Assert.hasText(name, "name cannot be null");
        this.uriEndpoint = uriEndpoint;
        this.name = name;
    }



}
