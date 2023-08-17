package click.porito.commons.auth2authserver.domains.oauth2_client.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity @Table(name = "redirect_uri")
@Getter
@Setter
@EqualsAndHashCode(of = {"id","uri"})
@NoArgsConstructor
public class RedirectUriEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "uri", nullable = false)
    private String uri;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "client_id", nullable = false)
    private ClientEntity client;

    public RedirectUriEntity(String uri, ClientEntity client) {
        this.uri = uri;
        this.client = client;
    }
}
