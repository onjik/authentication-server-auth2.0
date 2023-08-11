package click.porito.commons.auth2authserver.domains.oauth2_client.entity;

import click.porito.commons.auth2authserver.domains.oauth2_client.entity.static_entity.ScopeEntity;
import click.porito.commons.auth2authserver.domains.resource_owner.entity.ResourceOwnerEntity;
import click.porito.commons.auth2authserver.domains.resource_owner.entity.static_entity.Role;
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
public class OAuth2AuthorizationConsentEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "client_id", nullable = false)
    private ClientEntity clientEntity;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "resource_owner_id", nullable = false)
    private ResourceOwnerEntity resourceOwnerEntity;

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinTable(name = "authorization_consent_role",
            joinColumns = @JoinColumn(name = "authorization_consent_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinTable(name = "authorization_consent_scope",
            joinColumns = @JoinColumn(name = "authorization_consent_id"),
            inverseJoinColumns = @JoinColumn(name = "scope_id"))
    private Set<ScopeEntity> scopeEntities = new HashSet<>();

    public OAuth2AuthorizationConsentEntity(@NonNull ClientEntity clientEntity, @NonNull ResourceOwnerEntity resourceOwnerEntity, Set<Role> roles, Set<ScopeEntity> scopeEntities) {
        Assert.notNull(clientEntity, "clientEntity must not be null");
        Assert.notNull(resourceOwnerEntity, "resourceOwnerEntity must not be null");
        boolean isBothEmpty = roles.isEmpty() && scopeEntities.isEmpty();
        Assert.isTrue(!isBothEmpty, "one of roles and scopeEntities must not be empty");

        this.clientEntity = clientEntity;
        this.resourceOwnerEntity = resourceOwnerEntity;
        this.roles = roles;
        this.scopeEntities = scopeEntities;
    }
}
