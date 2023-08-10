package click.porito.commons.auth2authserver.domains.oauth2_client.entity.token;

import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;

import java.time.Duration;
import java.util.Map;

@Entity @Table(name = "oidc_id_token")
@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class OidcIdToken extends CommonToken {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Type(JsonType.class)
    @Column(name = "claims" , nullable = false, columnDefinition = "json")
    private Map<String,String> claims;

    public OidcIdToken(Map<String, Object> metadata, String value, Duration expiresAfter, Map<String, String> claims) {
        super(metadata, value, expiresAfter);
        this.claims = claims;
    }

    public OidcIdToken(Map<String, Object> metadata, String value, Map<String, String> claims) {
        super(metadata, value);
        this.claims = claims;
    }
}
