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
    private RegisteredClientEntity registeredClientEntity;

    public RedirectUriEntity(String uri, RegisteredClientEntity registeredClientEntity) {
        this.uri = uri;
        this.registeredClientEntity = registeredClientEntity;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }
}
