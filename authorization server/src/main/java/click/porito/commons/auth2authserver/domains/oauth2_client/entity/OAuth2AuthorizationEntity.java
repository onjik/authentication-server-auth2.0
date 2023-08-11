package click.porito.commons.auth2authserver.domains.oauth2_client.entity;

import click.porito.commons.auth2authserver.domains.oauth2_client.entity.static_entity.AuthorizationGrantTypeEntity;
import click.porito.commons.auth2authserver.domains.oauth2_client.entity.static_entity.ScopeEntity;
import click.porito.commons.auth2authserver.domains.oauth2_client.entity.token.AccessTokenEntity;
import click.porito.commons.auth2authserver.domains.oauth2_client.entity.token.AuthorizationCode;
import click.porito.commons.auth2authserver.domains.oauth2_client.entity.token.OidcIdTokenEntity;
import click.porito.commons.auth2authserver.domains.oauth2_client.entity.token.RefreshTokenEntity;
import click.porito.commons.auth2authserver.domains.resource_owner.entity.ResourceOwnerEntity;
import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Entity @Table(name = "oauth2_authorization")
@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class OAuth2AuthorizationEntity {

    @Id
    @Column(name = "id")
    private String id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "client_id", nullable = false)
    private ClientEntity clientEntity;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "resource_owner_id", nullable = false)
    private ResourceOwnerEntity resourceOwnerEntity;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "authorization_grant_type_id", nullable = false)
    private AuthorizationGrantTypeEntity authorizationGrantTypeEntity;

    //optional = true
    @Type(JsonType.class)
    @Column(name = "attribute", columnDefinition = "json")
    private Map<String,String> attribute = new HashMap<>();

    @Column(name = "state", length = 500)
    private String state;


    @OneToOne(fetch = FetchType.EAGER, optional = true, orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinColumn(name = "access_token_id", nullable = true)
    private AccessTokenEntity accessToken;

    @OneToOne(fetch = FetchType.EAGER, optional = true, orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinColumn(name = "refresh_token_id", nullable = true)
    private RefreshTokenEntity refreshToken;

    @OneToOne(fetch = FetchType.EAGER, optional = true, orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinColumn(name = "authorization_code_id", nullable = true)
    private AuthorizationCode authorizationCode;

    @OneToOne(fetch = FetchType.EAGER, optional = true, orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinColumn(name = "oidc_token_id", nullable = true)
    private OidcIdTokenEntity oidcIdToken;

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinTable(name = "authorization_scope",
            joinColumns = @JoinColumn(name = "oauth2_authorization_id"),
            inverseJoinColumns = @JoinColumn(name = "scope_id"))
    private Set<ScopeEntity> scopeEntities = new HashSet<>();

    @Builder
    public OAuth2AuthorizationEntity(String id, ClientEntity clientEntity, ResourceOwnerEntity resourceOwnerEntity, AuthorizationGrantTypeEntity authorizationGrantTypeEntity, Map<String, String> attribute, String state, AccessTokenEntity accessToken, RefreshTokenEntity refreshToken, AuthorizationCode authorizationCode, OidcIdTokenEntity oidcIdToken, Set<ScopeEntity> scopeEntities) {
        this.id = id;
        this.clientEntity = clientEntity;
        this.resourceOwnerEntity = resourceOwnerEntity;
        this.authorizationGrantTypeEntity = authorizationGrantTypeEntity;
        this.attribute = attribute;
        this.state = state;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.authorizationCode = authorizationCode;
        this.oidcIdToken = oidcIdToken;
        this.scopeEntities = scopeEntities;
    }

    public void setState(String state) {
        this.state = state;
    }

    public void setAccessToken(AccessTokenEntity accessToken) {
        this.accessToken = accessToken;
    }

    public void setRefreshToken(RefreshTokenEntity refreshToken) {
        this.refreshToken = refreshToken;
    }

    public void setAuthorizationCode(AuthorizationCode authorizationCode) {
        this.authorizationCode = authorizationCode;
    }

    public void setOidcIdToken(OidcIdTokenEntity oidcIdToken) {
        this.oidcIdToken = oidcIdToken;
    }

    public void setScopeEntities(Set<ScopeEntity> scopeEntities) {
        this.scopeEntities = scopeEntities;
    }
}
