package click.porito.commons.auth2authserver.domains.oauth2_client.entity.static_entity;

import click.porito.commons.auth2authserver.common.util.ConstantEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.util.Assert;

import java.util.Objects;

@ConstantEntity
@Entity @Table(name = "scope")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
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

    /**
     * @param uriEndpoint : uri endpoint (ex: "http://localhost:8080/api/v1")
     * @param name : prefixed scope name (ex: "SCOPE_READ")
     */
    public ScopeEntity(String uriEndpoint, String name) {
        Assert.isTrue(name.startsWith(SCOPE_PREFIX), "Scope name must start with SCOPE_ prefix");
        this.uriEndpoint = uriEndpoint;
        this.name = name.toUpperCase();
    }

    @Override
    public String getAuthority() {
        return this.getName();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ScopeEntity that = (ScopeEntity) o;
        return getName().equals(that.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName());
    }
}
