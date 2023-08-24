package click.porito.commons.auth2authserver.domains.oauth2_client.entity;

import click.porito.commons.auth2authserver.domains.oauth2_client.entity.static_entity.ScopeEntity;
import click.porito.commons.auth2authserver.domains.resource_owner.entity.ResourceOwnerEntity;
import click.porito.commons.auth2authserver.domains.resource_owner.entity.static_entity.RoleEntity;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationConsent;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Entity @Table(name = "authorization_consent", uniqueConstraints = {
        @UniqueConstraint(name = "unique_consent", columnNames = {"client_id", "resource_owner_id"})
})
@Getter
@Setter
@EqualsAndHashCode(of = {"id"})
@NoArgsConstructor
public class OAuth2AuthorizationConsentEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "client_id", nullable = false)
    private ClientEntity client;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "resource_owner_id", nullable = false)
    private ResourceOwnerEntity resourceOwner;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "authorization_consent_role",
            joinColumns = @JoinColumn(name = "authorization_consent_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<RoleEntity> roles = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "authorization_consent_scope",
            joinColumns = @JoinColumn(name = "authorization_consent_id"),
            inverseJoinColumns = @JoinColumn(name = "scope_id"))
    private Set<ScopeEntity> scopes = new HashSet<>();

    @Builder
    public OAuth2AuthorizationConsentEntity(ClientEntity client, ResourceOwnerEntity resourceOwner) {
        this.client = client;
        this.resourceOwner = resourceOwner;
    }

    public OAuth2AuthorizationConsent toObject() {
        //merge authorities
        Set<GrantedAuthority> grantedAuthorities = Stream.of(getRoles(), getScopes())
                .flatMap(Set::stream)
                .map(GrantedAuthority.class::cast)
                .collect(Collectors.toSet());
        return OAuth2AuthorizationConsent.withId(getClient().getId(), getResourceOwner().getId())
                .authorities(authoritySet -> authoritySet.addAll(grantedAuthorities))
                .build();
    }
}
