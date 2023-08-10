package click.porito.commons.auth2authserver.domains.resource_owner.entity.static_entity;

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
@Entity @Table(name = "role")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Role {
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

    public String getRolePrefixedName() {
        return ROLE_PREFIX + name.toUpperCase();
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }

    public void setPriority(short priority) {
        this.priority = priority;
    }

    /**
     * recommend to use static factory method
     */
    public Role(@NonNull String name, boolean isAdmin, short priority) {
        //validate name
        Assert.hasText(name, "name cannot be empty");
        name = name.toUpperCase();
        //validate priority
        if (priority < LOWEST_ADMIN_PRIORITY && isAdmin){
            throw new IllegalArgumentException("super user priority cannot be less than " + LOWEST_ADMIN_PRIORITY);
        }
        if (priority >= LOWEST_ADMIN_PRIORITY && !isAdmin){
            throw new IllegalArgumentException("user priority cannot be greater than or equal to " + LOWEST_ADMIN_PRIORITY);
        }

        //set
        this.name = name;
        this.isAdmin = isAdmin;
        this.priority = priority;
    }

    public static Role ofDefaultPriorityUser(@NonNull String name) {
        return new Role(name, false, DEFAULT_USER_PRIORITY);
    }

    public static Role ofUser(@NonNull String name, short priority) {
        if (priority >= LOWEST_ADMIN_PRIORITY) {
            priority = LOWEST_ADMIN_PRIORITY -1;
        }
        return new Role(name, false, priority);
    }

    public static Role ofAdmin(@NonNull String name) {
        return new Role(name, true, LOWEST_ADMIN_PRIORITY);
    }

    public static Role ofAdmin(@NonNull String name, short priority) {
        if (priority < LOWEST_ADMIN_PRIORITY) {
            priority = LOWEST_ADMIN_PRIORITY;
        }
        return new Role(name, true, LOWEST_ADMIN_PRIORITY);
    }

}
