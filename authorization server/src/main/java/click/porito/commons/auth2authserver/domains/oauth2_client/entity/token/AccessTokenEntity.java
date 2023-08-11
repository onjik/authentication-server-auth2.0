package click.porito.commons.auth2authserver.domains.oauth2_client.entity.token;

import click.porito.commons.auth2authserver.domains.oauth2_client.entity.static_entity.ScopeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Duration;
import java.util.Map;
import java.util.Set;

@Entity @Table(name = "access_token")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AccessTokenEntity extends CommonTokenEntity {

    @Column(name = "token_type", nullable = false)
    private String tokenType;

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinTable(name = "access_token_scope",
            joinColumns = @JoinColumn(name = "token_id"),
            inverseJoinColumns = @JoinColumn(name = "scope_id"))
    private Set<ScopeEntity> scopeEntities;

    @Builder
    public AccessTokenEntity(Map<String, Object> metadata, String value, Duration expiresAfter, String tokenType, Set<ScopeEntity> scopeEntities) {
        super(metadata, value, expiresAfter);
        this.tokenType = tokenType;
        this.scopeEntities = scopeEntities;
    }
}
