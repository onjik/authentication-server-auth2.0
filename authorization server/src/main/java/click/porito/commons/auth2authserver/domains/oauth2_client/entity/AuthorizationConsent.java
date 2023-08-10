package click.porito.commons.auth2authserver.domains.oauth2_client.entity;

import click.porito.commons.auth2authserver.domains.resource_owner.entity.ResourceOwner;
import click.porito.commons.auth2authserver.domains.resource_owner.entity.static_entity.Role;
import click.porito.commons.auth2authserver.domains.oauth2_client.entity.static_entity.Scope;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.util.Assert;

import java.util.HashSet;
import java.util.Set;

@Entity @Table(name = "authorization_consent", uniqueConstraints = {
        @UniqueConstraint(name = "unique_consent", columnNames = {"client_id", "resource_owner_id"})
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AuthorizationConsent {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "resource_owner_id", nullable = false)
    private ResourceOwner resourceOwner;

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinTable(name = "authorization_consent_role",
            joinColumns = @JoinColumn(name = "authorization_consent_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinTable(name = "authorization_consent_scope",
            joinColumns = @JoinColumn(name = "authorization_consent_id"),
            inverseJoinColumns = @JoinColumn(name = "scope_id"))
    private Set<Scope> scopes = new HashSet<>();

    public AuthorizationConsent(@NonNull Client client, @NonNull ResourceOwner resourceOwner, Set<Role> roles, Set<Scope> scopes) {
        Assert.notNull(client, "client must not be null");
        Assert.notNull(resourceOwner, "resourceOwner must not be null");
        boolean isBothEmpty = roles.isEmpty() && scopes.isEmpty();
        Assert.isTrue(!isBothEmpty, "one of roles and scopes must not be empty");

        this.client = client;
        this.resourceOwner = resourceOwner;
        this.roles = roles;
        this.scopes = scopes;
    }
}
