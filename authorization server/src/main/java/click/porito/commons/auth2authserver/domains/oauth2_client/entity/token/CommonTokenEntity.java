package click.porito.commons.auth2authserver.domains.oauth2_client.entity.token;

import click.porito.commons.auth2authserver.domains.oauth2_client.entity.OAuth2AuthorizationEntity;
import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Type;
import org.springframework.security.oauth2.core.OAuth2Token;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;

import java.time.Instant;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Entity @Table(name = "token")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "dtype", discriminatorType = DiscriminatorType.STRING)
@Getter
@Setter
@NoArgsConstructor
public abstract class CommonTokenEntity {

    public static final Map<String,Object> DEFAULT_METADATA = Collections.singletonMap(OAuth2Authorization.Token.INVALIDATED_METADATA_NAME, false);

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "token_id")
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "oauth2_authorization_id", nullable = false)
    private OAuth2AuthorizationEntity authorization;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CommonTokenEntity entity = (CommonTokenEntity) o;
        return getValue().equals(entity.getValue());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getValue());
    }

    public abstract Object toObject();

    public abstract Class<? extends OAuth2Token> obtainObjectType();

    public CommonTokenEntity(Map<String, Object> metadata, String value, Instant issuedAt, Instant expiresAt, OAuth2AuthorizationEntity authorization) {
        this.metadata = metadata;
        this.value = value;
        this.issuedAt = issuedAt;
        this.expiresAt = expiresAt;
        this.authorization = authorization;
    }
}
