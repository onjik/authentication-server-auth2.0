package click.porito.commons.auth2authserver.domains.oauth2_client.entity.token;

import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Type;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@MappedSuperclass
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class CommonToken {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Type(JsonType.class)
    @Column(name = "metadata", nullable = true, columnDefinition = "json")
    private Map<String,Object> metadata = new HashMap<>();

    @Column(name = "token_value", nullable = false)
    protected String value;

    @CreationTimestamp
    @Column(name = "issued_at")
    private Instant issuedAt;

    @Column(name = "expires_at")
    private Instant expiresAt;

    protected CommonToken(Map<String, Object> metadata, String value, Duration expiresAfter) {
        this.metadata = metadata;
        this.value = value;
        this.issuedAt = Instant.now();
        this.expiresAt = issuedAt.plus(expiresAfter);
    }

    protected CommonToken(Map<String, Object> metadata, String value) {
        this.metadata = metadata;
        this.value = value;
        this.issuedAt = Instant.now();
    }

    public void setExpiresAt(Instant expiresAt) {
        this.expiresAt = expiresAt;
    }
}
