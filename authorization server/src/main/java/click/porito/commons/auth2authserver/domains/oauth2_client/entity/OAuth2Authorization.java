package click.porito.commons.auth2authserver.domains.oauth2_client.entity;

import click.porito.commons.auth2authserver.domains.oauth2_client.entity.static_entity.OAuth2AuthorizationGrantType;
import click.porito.commons.auth2authserver.domains.oauth2_client.entity.static_entity.Scope;
import click.porito.commons.auth2authserver.domains.oauth2_client.entity.token.AccessToken;
import click.porito.commons.auth2authserver.domains.oauth2_client.entity.token.AuthorizationCode;
import click.porito.commons.auth2authserver.domains.oauth2_client.entity.token.OidcIdToken;
import click.porito.commons.auth2authserver.domains.oauth2_client.entity.token.RefreshToken;
import click.porito.commons.auth2authserver.domains.resource_owner.entity.ResourceOwner;
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
public class OAuth2Authorization {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "resource_owner_id", nullable = false)
    private ResourceOwner resourceOwner;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "authorization_grant_type_id", nullable = false)
    private OAuth2AuthorizationGrantType oAuth2AuthorizationGrantType;

    //optional = true
    @Type(JsonType.class)
    @Column(name = "attribute", columnDefinition = "json")
    private Map<String,String> attribute = new HashMap<>();

    @Column(name = "state", length = 500)
    private String state;


    @OneToOne(fetch = FetchType.EAGER, optional = true, orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinColumn(name = "access_token_id", nullable = true)
    private AccessToken accessToken;

    @OneToOne(fetch = FetchType.EAGER, optional = true, orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinColumn(name = "refresh_token_id", nullable = true)
    private RefreshToken refreshToken;

    @OneToOne(fetch = FetchType.EAGER, optional = true, orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinColumn(name = "authorization_code_id", nullable = true)
    private AuthorizationCode authorizationCode;

    @OneToOne(fetch = FetchType.EAGER, optional = true, orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinColumn(name = "oidc_token_id", nullable = true)
    private OidcIdToken oidcIdToken;

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinTable(name = "authorization_scope",
            joinColumns = @JoinColumn(name = "oauth2_authorization_id"),
            inverseJoinColumns = @JoinColumn(name = "scope_id"))
    private Set<Scope> scopes = new HashSet<>();

    @Builder
    public OAuth2Authorization(ResourceOwner resourceOwner, OAuth2AuthorizationGrantType oAuth2AuthorizationGrantType, Map<String, String> attribute, String state, AccessToken accessToken, RefreshToken refreshToken, AuthorizationCode authorizationCode, OidcIdToken oidcIdToken, Set<Scope> scopes) {
        this.resourceOwner = resourceOwner;
        this.oAuth2AuthorizationGrantType = oAuth2AuthorizationGrantType;
        this.attribute = attribute;
        this.state = state;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.authorizationCode = authorizationCode;
        this.oidcIdToken = oidcIdToken;
        this.scopes = scopes;
    }

    public void setState(String state) {
        this.state = state;
    }

    public void setAccessToken(AccessToken accessToken) {
        this.accessToken = accessToken;
    }

    public void setRefreshToken(RefreshToken refreshToken) {
        this.refreshToken = refreshToken;
    }

    public void setAuthorizationCode(AuthorizationCode authorizationCode) {
        this.authorizationCode = authorizationCode;
    }

    public void setOidcIdToken(OidcIdToken oidcIdToken) {
        this.oidcIdToken = oidcIdToken;
    }

    public void setScopes(Set<Scope> scopes) {
        this.scopes = scopes;
    }
}
