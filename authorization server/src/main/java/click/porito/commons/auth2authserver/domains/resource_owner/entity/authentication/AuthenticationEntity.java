package click.porito.commons.auth2authserver.domains.resource_owner.entity.authentication;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 계정과 비밀 번호의 조합을 나타냅니다. 한개의 계정에는 여러개, 그리고 다양한 타입의 인증 수단이 존재할 수 있습니다.
 */
@Entity @Table(name = "authentication",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"account_id", "authentication_type"})
        }
)
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "authentication_type")
@Getter
@Setter
@EqualsAndHashCode(of = "id")
@NoArgsConstructor
public abstract class AuthenticationEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "account_id", nullable = false)
    protected AccountEntity accountEntity;

    public AuthenticationEntity(AccountEntity accountEntity) {
        this.accountEntity = accountEntity;
    }
}
