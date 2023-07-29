package click.porito.commons.auth2authserver.auth2.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "services")
@NoArgsConstructor(access = AccessLevel.PROTECTED) @Getter
public final class Service {

    @Id @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name = "service_id")
    private Long id;

    @Column(name = "service_name", nullable = false, unique = true, length = 50)
    @NotBlank @Length(max = 50)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User owner;

    @Column(name = "service_key", nullable = false, unique = true)
    private String serviceKey;

    @JsonIgnore
    @Column(name = "secret_key",nullable = false)
    private String secretKey;


    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "service_id")
    private Set<RegisteredRedirection> allowedRedirection;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "service_scope",
            joinColumns = @JoinColumn(name = "service_id",nullable = false),
            inverseJoinColumns = @JoinColumn(name = "scope_id",nullable = false)
    )
    private Set<Scope> allowedScope;

    public Service(String name, User owner, String secretKey, Set<RegisteredRedirection> allowedRedirection) {
        this.name = name;
        this.owner = owner;
        this.secretKey = secretKey;
        this.allowedRedirection = allowedRedirection;
    }

    @PrePersist
    public void prePersist(){
        this.serviceKey = UUID.randomUUID().toString();
    }

    @Builder(builderMethodName = "builder")
    public Service(String name, User owner, String serviceKey, String secretKey, Set<RegisteredRedirection> allowedRedirection, Set<Scope> allowedScope) {
        this.name = name;
        this.owner = owner;
        this.serviceKey = serviceKey;
        this.secretKey = secretKey;
        this.allowedRedirection = allowedRedirection;
        this.allowedScope = allowedScope;
    }


}
