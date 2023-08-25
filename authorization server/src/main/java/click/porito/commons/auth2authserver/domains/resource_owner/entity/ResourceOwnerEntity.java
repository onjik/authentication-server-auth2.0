package click.porito.commons.auth2authserver.domains.resource_owner.entity;

import click.porito.commons.auth2authserver.domains.resource_owner.entity.authentication.AccountEntity;
import click.porito.commons.auth2authserver.domains.resource_owner.util.GenderConverter;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

@Entity @Table(name = "resource_owner")
@Getter
@Setter @EqualsAndHashCode(of = {"id","name","gender"})
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

    @OneToOne(mappedBy = "resourceOwner", fetch = FetchType.LAZY)
    private AccountEntity accountEntity;

    public ResourceOwnerEntity(String name, Gender gender) {
        this.name = name;
        this.gender = gender;
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
