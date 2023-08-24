package click.porito.commons.auth2authserver.domains.resource_owner.entity;

import click.porito.commons.auth2authserver.domains.resource_owner.entity.static_entity.RoleEntity;
import click.porito.commons.auth2authserver.util.GenderConverter;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import java.util.HashSet;
import java.util.Set;

@Entity @Table(name = "resource_owner")
@Getter
@Setter @EqualsAndHashCode(of = {"id"})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ResourceOwnerEntity {

    @Id
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "uuid2")
    @Column(length = 50, nullable = false, updatable = false)
    private String id;

    @Column(name = "name", nullable = false, length = 255)
    private String name;

    @Convert(converter = GenderConverter.class)
    @Column(name = "gender", nullable = false)
    private Gender gender;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "resource_owner_role",
            joinColumns = @JoinColumn(name = "resource_owner_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<RoleEntity> roleEntities = new HashSet<>();

    public ResourceOwnerEntity(String name, Gender gender) {
        this.name = name;
        this.gender = gender;
    }

    public void addRole(RoleEntity roleEntity) {
        getRoleEntities().add(roleEntity);
    }

    public enum Gender {
        MAN('M'),WOMEN('W');

        private final char idChar;

        Gender(char idChar) {
            this.idChar = idChar;
        }

        public char getIdChar() {
            return idChar;
        }
    }
}
