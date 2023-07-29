package click.porito.commons.auth2authserver.auth2.model.entity;

import javax.persistence.*;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "credential_type")
@Table(name = "credentials",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"credential_type", "user_id"})
        })
public abstract class Credential {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "credential_id")
    private Long id;

    //OneToMany mono-directional mapping - user
}
