package click.porito.commons.auth2authserver.auth2.model.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "roles")
@NoArgsConstructor(access = AccessLevel.PROTECTED) @Getter
public final class Role {

    public enum Type {
        ROLE_USER,ROLE_ADMIN
    }

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "role_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "name",nullable = false, unique = true)
    private Type type;


    public Role(Type type) {
        this.type = type;
    }

    public static Role of(Type type){
        return new Role(type);
    }
}
