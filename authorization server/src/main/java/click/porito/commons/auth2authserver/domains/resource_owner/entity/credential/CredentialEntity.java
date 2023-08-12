package click.porito.commons.auth2authserver.domains.resource_owner.entity.credential;

import click.porito.commons.auth2authserver.domains.resource_owner.entity.ResourceOwnerEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity @Table(name = "credential")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "credential_type")
@Getter
@Setter
@EqualsAndHashCode(of = "id")
@NoArgsConstructor
public abstract class CredentialEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "resource_owner_id", nullable = false)
    protected ResourceOwnerEntity resourceOwner;

    public CredentialEntity(ResourceOwnerEntity resourceOwner) {
        this.resourceOwner = resourceOwner;
    }
}
