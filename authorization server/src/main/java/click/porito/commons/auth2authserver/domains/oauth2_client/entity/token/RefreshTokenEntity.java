package click.porito.commons.auth2authserver.domains.oauth2_client.entity.token;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.Duration;
import java.util.Map;

@Entity
@Table(name = "refresh_token")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RefreshTokenEntity extends CommonTokenEntity {

    public RefreshTokenEntity(Map<String, Object> metadata, String value) {
        super(metadata, value);
    }

    public RefreshTokenEntity(Map<String, Object> metadata, String value, Duration expiresAfter) {
        super(metadata, value, expiresAfter);
    }
}
