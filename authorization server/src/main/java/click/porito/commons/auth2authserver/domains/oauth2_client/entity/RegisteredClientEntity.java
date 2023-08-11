package click.porito.commons.auth2authserver.domains.oauth2_client.entity;

import click.porito.commons.auth2authserver.domains.oauth2_client.entity.static_entity.ClientAuthenticationMethodEntity;
import click.porito.commons.auth2authserver.domains.oauth2_client.entity.static_entity.AuthorizationGrantTypeEntity;
import click.porito.commons.auth2authserver.domains.oauth2_client.entity.static_entity.ScopeEntity;
import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Type;

import java.time.Instant;
import java.util.*;

@Entity @Table(name = "client")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RegisteredClientEntity {

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
    private Set<RedirectUriEntity> redirectUrisEntities = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinTable(name = "client_scope",
            joinColumns = @JoinColumn(name = "client_id"),
            inverseJoinColumns = @JoinColumn(name = "scope_id"))
    private Set<ScopeEntity> scopeEntities = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinTable(name = "client_authentication_method" ,
            joinColumns = @JoinColumn(name = "client_id"),
            inverseJoinColumns = @JoinColumn(name = "authentication_method_id"))
    private Set<ClientAuthenticationMethodEntity> clientAuthenticationMethodEntities = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinTable(name = "client_authorization_grant_type",
            joinColumns = @JoinColumn(name = "client_id"),
            inverseJoinColumns = @JoinColumn(name = "authorization_grant_type_id"))
    private Set<AuthorizationGrantTypeEntity> authorizationGrantTypes = new HashSet<>();


    @Builder
    public RegisteredClientEntity(String id, String clientId, Instant clientIdIssuedAt, String clientName, String clientSecret, Instant clientSecretExpiresAt, Map<String, Object> clientSettings, Map<String, Object> tokenSettings) {
        this.id = id;
        this.clientId = clientId;
        this.clientIdIssuedAt = clientIdIssuedAt;
        this.clientName = clientName;
        this.clientSecret = clientSecret;
        this.clientSecretExpiresAt = clientSecretExpiresAt;
        this.clientSettings = clientSettings;
        this.tokenSettings = tokenSettings;
    }




}
