package click.porito.commons.auth2authserver.domains.resource_owner.entity.static_entity;

import click.porito.commons.auth2authserver.global.util.ConstantEntity;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.util.Assert;

/**
 * <p>Usage Example</p>
 * <code>
 *     RoleEntity userRole = RoleEntity.ofUser("USER", RoleEntity.DEFAULT_USER_PRIORITY);
 * </code>
 */
@ConstantEntity
@Entity @Table(name = "role")
@Getter
@Setter
@EqualsAndHashCode(of = {"id", "name"})
@NoArgsConstructor
public class RoleEntity implements GrantedAuthority {
    public static final String ROLE_PREFIX = "ROLE_";

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name", unique = true,
            length = 255, nullable = true)
    private String name;


    @PrePersist
    void prePersist() {
        Assert.notNull(this.name, "Role name must not be null");
        this.name = this.name.toUpperCase();
        if (!this.name.startsWith(ROLE_PREFIX)) {
            this.name = ROLE_PREFIX + this.name;
        }
    }

    public RoleEntity(String name) {
        this.name = name;
    }

    @Override
    public String getAuthority() {
        return this.getName();
    }
}
