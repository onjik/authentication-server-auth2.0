package click.porito.commons.auth2authserver.domains.oauth2_client.entity;

import click.porito.commons.auth2authserver.domains.oauth2_client.entity.static_entity.AuthorizationGrantTypeEntity;
import click.porito.commons.auth2authserver.domains.oauth2_client.entity.static_entity.ScopeEntity;
import click.porito.commons.auth2authserver.domains.oauth2_client.entity.token.CommonTokenEntity;
import click.porito.commons.auth2authserver.domains.resource_owner.entity.ResourceOwnerEntity;
import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Type;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;

import java.util.*;
import java.util.stream.Collectors;

@Entity @Table(name = "oauth2_authorization")
@Getter
@Setter @EqualsAndHashCode(of = "id")
@NoArgsConstructor
public class OAuth2AuthorizationEntity {

    @Id
    @Column(name = "id")
    private String id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "client_id", nullable = false)
    private ClientEntity client;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "resource_owner_id", nullable = false)
    private ResourceOwnerEntity resourceOwner;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "authorization_grant_type_id", nullable = false)
    private AuthorizationGrantTypeEntity authorizationGrantType;

    //optional = true
    @Type(JsonType.class)
    @Column(name = "attribute", columnDefinition = "json")
    private Map<String, Object> attribute = new HashMap<String, Object>();

    @Column(name = "state", length = 500)
    private String state;


    @OneToMany(fetch = FetchType.EAGER, mappedBy = "authorization",cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<CommonTokenEntity> tokens = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "authorization_scope",
            joinColumns = @JoinColumn(name = "oauth2_authorization_id"),
            inverseJoinColumns = @JoinColumn(name = "scope_id"))
    private Set<ScopeEntity> scopes = new HashSet<>();

    @Builder
    public OAuth2AuthorizationEntity(String id, ClientEntity client, ResourceOwnerEntity resourceOwner, AuthorizationGrantTypeEntity authorizationGrantType, Map<String, Object> attribute, String state) {
        this.id = id;
        this.client = client;
        this.resourceOwner = resourceOwner;
        this.authorizationGrantType = authorizationGrantType;
        if (attribute != null) this.attribute = attribute;
        this.state = state;
    }

    public void addToken(CommonTokenEntity token) {
        this.tokens.add(token);
        token.setAuthorization(this);
    }

    public void addScope(ScopeEntity scope) {
        this.scopes.add(scope);
    }

    public OAuth2Authorization toObject() {
        OAuth2Authorization.Builder builder = OAuth2Authorization.withRegisteredClient(this.client.toObject())
                .id(this.id)
                .principalName(this.resourceOwner.getId())
                .authorizationGrantType(new AuthorizationGrantType(this.authorizationGrantType.getName()))
                .authorizedScopes(this.scopes.stream()
                        .map(ScopeEntity::getName)
                        .collect(Collectors.toSet())
                )
                .attributes(attributeMap -> {
                    attributeMap.putAll(this.attribute);
                });

        tokens.stream()
                .filter(Objects::nonNull)
                .forEach(token -> {
                    builder.token(
                            token.obtainObjectType().cast(token.toObject()),
                            metadataMap -> metadataMap.putAll(token.getMetadata())
                    );
                });
        return builder.build();
    }
}
