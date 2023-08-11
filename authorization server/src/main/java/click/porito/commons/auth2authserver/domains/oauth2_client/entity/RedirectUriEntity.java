package click.porito.commons.auth2authserver.domains.oauth2_client.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity @Table(name = "redirect_uri")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RedirectUriEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "uri", nullable = false)
    private String uri;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "client_id", nullable = false)
    private ClientEntity clientEntity;

    public RedirectUriEntity(String uri, ClientEntity clientEntity) {
        this.uri = uri;
        this.clientEntity = clientEntity;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }
}
