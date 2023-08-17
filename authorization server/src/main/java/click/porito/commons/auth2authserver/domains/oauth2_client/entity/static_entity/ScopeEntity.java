package click.porito.commons.auth2authserver.domains.oauth2_client.entity.static_entity;

import click.porito.commons.auth2authserver.global.util.ConstantEntity;
import jakarta.persistence.*;
import lombok.*;

@ConstantEntity
@Entity @Table(name = "scope")
@Getter
@Setter
@EqualsAndHashCode(of = "id")
@NoArgsConstructor
public class ScopeEntity {

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
        this.name = this.name.toUpperCase();
    }

    public String getScopePrefixedName() {
        return SCOPE_PREFIX + name;
    }

    public ScopeEntity(String uriEndpoint, String name) {
        this.uriEndpoint = uriEndpoint;
        this.name = name;
    }
}
