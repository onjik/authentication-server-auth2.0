package click.porito.commons.auth2authserver.domains.oauth2_client.entity;

import click.porito.commons.auth2authserver.domains.oauth2_client.entity.static_entity.AuthorizationGrantTypeEntity;
import click.porito.commons.auth2authserver.domains.oauth2_client.entity.static_entity.ClientAuthenticationMethodEntity;
import click.porito.commons.auth2authserver.domains.oauth2_client.entity.static_entity.ScopeEntity;
import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Type;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Entity @Table(name = "client")
@Getter
@Setter
@EqualsAndHashCode(of = {"id","clientId"})
@NoArgsConstructor
public class ClientEntity {

    @Id
    @Column(name = "id")
    private String id;

    @Column(name = "client_id", unique = true, updatable = false,
        nullable = false, length = 255)
    private String clientId;

    @CreationTimestamp
    @Column(name = "client_id_issued_at", updatable = false)
    private Instant clientIdIssuedAt;

    @Column(name = "client_name", nullable = false, length = 100)
    private String clientName;

    @Column(name = "client_secret", nullable = false, length = 255)
    private String clientSecret;

    @Column(name = "client_secret_expires_at", nullable = false)
    private Instant clientSecretExpiresAt;

    @Type(JsonType.class)
    @Column(name = "client_settings", nullable = false, columnDefinition = "json")
    private Map<String,Object> clientSettings;

    @Type(JsonType.class)
    @Column(name = "token_settings", nullable = false, columnDefinition = "json")
    private Map<String,Object> tokenSettings;

    @OneToMany(mappedBy = "client", fetch = FetchType.LAZY,
        cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RedirectUriEntity> redirectUris = new ArrayList<>(4);

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "client_scope",
            joinColumns = @JoinColumn(name = "client_id"),
            inverseJoinColumns = @JoinColumn(name = "scope_id"))
    private Set<ScopeEntity> scopes = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "client_authentication_method" ,
            joinColumns = @JoinColumn(name = "client_id"),
            inverseJoinColumns = @JoinColumn(name = "authentication_method_id"))
    private Set<ClientAuthenticationMethodEntity> clientAuthenticationMethods = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "client_authorization_grant_type",
            joinColumns = @JoinColumn(name = "client_id"),
            inverseJoinColumns = @JoinColumn(name = "authorization_grant_type_id"))
    private Set<AuthorizationGrantTypeEntity> authorizationGrantTypes = new HashSet<>();


    @Builder
    public ClientEntity(String id, String clientId, Instant clientIdIssuedAt, String clientName, String clientSecret, Instant clientSecretExpiresAt, Map<String, Object> clientSettings, Map<String, Object> tokenSettings) {
        this.id = id;
        this.clientId = clientId;
        this.clientIdIssuedAt = clientIdIssuedAt;
        this.clientName = clientName;
        this.clientSecret = clientSecret;
        this.clientSecretExpiresAt = clientSecretExpiresAt;
        this.clientSettings = clientSettings;
        this.tokenSettings = tokenSettings;
    }

    public void addRedirectUri(RedirectUriEntity redirectUriEntity){
        getRedirectUris().add(redirectUriEntity);
        redirectUriEntity.setClient(this);
    }

    public void addScope(ScopeEntity scopeEntity) {
        getScopes().add(scopeEntity);
    }

    public void addClientAuthenticationMethod(ClientAuthenticationMethodEntity clientAuthenticationMethodEntity){
        getClientAuthenticationMethods().add(clientAuthenticationMethodEntity);
    }

    public void addAuthorizationGrantType(AuthorizationGrantTypeEntity authorizationGrantTypeEntity){
        getAuthorizationGrantTypes().add(authorizationGrantTypeEntity);
    }

    public RegisteredClient toObject(){
        Set<AuthorizationGrantType> grantTypes = this.getAuthorizationGrantTypes().stream()
                .map(AuthorizationGrantTypeEntity::getName)
                .map(AuthorizationGrantType::new)
                .collect(Collectors.toSet());
        // column mapping
        return RegisteredClient.withId(this.getId())
                .clientId(this.getClientId())
                .clientIdIssuedAt(this.getClientIdIssuedAt())
                .clientName(this.getClientName())
                .clientSecret(this.getClientSecret())
                .clientSecretExpiresAt(this.getClientSecretExpiresAt())
                .authorizationGrantTypes(grantTypeSet -> {
                    grantTypeSet.addAll(grantTypes);
                })
                .clientAuthenticationMethods(methodSet -> {
                    this.getClientAuthenticationMethods().stream()
                            .map(ClientAuthenticationMethodEntity::getName)
                            .map(ClientAuthenticationMethod::new)
                            .forEach(methodSet::add);
                })
                .authorizationGrantTypes(grantSet -> {
                    this.getAuthorizationGrantTypes().stream()
                            .map(AuthorizationGrantTypeEntity::getName)
                            .map(AuthorizationGrantType::new)
                            .forEach(grantSet::add);
                })
                .redirectUris(redirectUriSet -> {
                    this.getRedirectUris().stream()
                            .map(RedirectUriEntity::getUri)
                            .forEach(redirectUriSet::add);
                })
                .scopes(scopeSet -> {
                    this.getScopes().stream()
                            .map(ScopeEntity::getName)
                            .forEach(scopeSet::add);
                })
                .clientSettings(ClientSettings.withSettings(this.getClientSettings()).build())
                .tokenSettings(TokenSettings.withSettings(this.getTokenSettings()).build())
                .build();
    }




}
