package click.porito.commons.auth2authserver.domains.oauth2_client.entity.token;

import click.porito.commons.auth2authserver.domains.oauth2_client.entity.static_entity.Scope;
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
public class AccessToken extends CommonToken {

    @Column(name = "token_type", nullable = false)
    private String tokenType;

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinTable(name = "access_token_scope",
            joinColumns = @JoinColumn(name = "token_id"),
            inverseJoinColumns = @JoinColumn(name = "scope_id"))
    private Set<Scope> scopes;

    @Builder
    public AccessToken(Map<String, String> metadata, String value, Duration expiresAfter, String tokenType, Set<Scope> scopes) {
        super(metadata, value, expiresAfter);
        this.tokenType = tokenType;
        this.scopes = scopes;
    }
}
