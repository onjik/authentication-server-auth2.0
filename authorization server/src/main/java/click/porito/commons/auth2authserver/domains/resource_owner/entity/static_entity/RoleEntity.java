package click.porito.commons.auth2authserver.domains.resource_owner.entity.static_entity;

import click.porito.commons.auth2authserver.global.util.ConstantEntity;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.lang.NonNull;

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
@EqualsAndHashCode(of = "id")
@NoArgsConstructor
public class RoleEntity {
    public static final short MAX_PRIORITY = Short.MAX_VALUE -1;
    public static final short MIN_PRIORITY = Short.MIN_VALUE +1;
    public static final short LOWEST_ADMIN_PRIORITY = 20_000;
    public static final short DEFAULT_USER_PRIORITY = 0;
    public static final String ROLE_PREFIX = "ROLE_";

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name", unique = true,
            length = 255, nullable = true)
    private String name;

    @Column(name = "is_super_user") //default false (on db)
    private boolean isAdmin;

    @Column(name = "priority") //default 0 (on db)
    private short priority;



    public static RoleEntity ofDefaultPriorityUser(@NonNull String name) {
        RoleEntity roleEntity = new RoleEntity();
        roleEntity.setName(name);
        roleEntity.setAdmin(false);
        roleEntity.setPriority(DEFAULT_USER_PRIORITY);
        return roleEntity;
    }

    public static RoleEntity ofUser(@NonNull String name, short priority) {
        if (priority >= LOWEST_ADMIN_PRIORITY) {
            priority = LOWEST_ADMIN_PRIORITY -1;
        }
        RoleEntity roleEntity = new RoleEntity();
        roleEntity.setName(name);
        roleEntity.setAdmin(false);
        roleEntity.setPriority(priority);
        return roleEntity;
    }

    public static RoleEntity ofAdmin(@NonNull String name) {
        RoleEntity roleEntity = new RoleEntity();
        roleEntity.setName(name);
        roleEntity.setAdmin(true);
        roleEntity.setPriority(LOWEST_ADMIN_PRIORITY);
        return roleEntity;
    }

    public static RoleEntity ofAdmin(@NonNull String name, short priority) {
        if (priority < LOWEST_ADMIN_PRIORITY) {
            priority = LOWEST_ADMIN_PRIORITY;
        }
        RoleEntity roleEntity = new RoleEntity();
        roleEntity.setName(name);
        roleEntity.setAdmin(true);
        roleEntity.setPriority(priority);
        return roleEntity;
    }

}
