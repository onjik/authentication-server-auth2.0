package click.porito.commons.auth2authserver.domains.oauth2_client.entity.token;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Duration;
import java.util.Map;

@Entity
@Table(name = "authorization_code")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AuthorizationCode extends CommonTokenEntity {
    public AuthorizationCode(Map<String, Object> metadata, String value, Duration expiresAfter) {
        super(metadata, value, expiresAfter);
    }

    public AuthorizationCode(Map<String, Object> metadata, String value) {
        super(metadata, value);
    }
}
