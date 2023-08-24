package click.porito.commons.auth2authserver.domains.oauth2_client.entity.static_entity;

import click.porito.commons.auth2authserver.global.util.ConstantEntity;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.util.Assert;

@ConstantEntity
@Entity @Table(name = "scope")
@Getter
@Setter
@EqualsAndHashCode(of = {"id","name"})
@NoArgsConstructor
public class ScopeEntity implements GrantedAuthority {

    public static final String SCOPE_PREFIX = "SCOPE_";

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "uri_endpoint", updatable = true)
    private String uriEndpoint;

    @Column(name = "name", unique = true,
            length = 255, nullable = false)
    private String name;

    @PrePersist
    public void prePersist() {
        Assert.notNull(this.name, "Scope name must not be null");
        this.name = this.name.toUpperCase();
        if (!this.name.startsWith(SCOPE_PREFIX)) {
            this.name = SCOPE_PREFIX + this.name;
        }
    }

    public ScopeEntity(String uriEndpoint, String name) {
        this.uriEndpoint = uriEndpoint;
        this.name = name;
    }

    @Override
    public String getAuthority() {
        return this.getName();
    }
}
