package click.porito.commons.auth2authserver.auth2.model.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;

@Entity
@Table(name = "scopes")
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED) @Getter
public final class Scope {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "scope_id")
    private Long id;

    @Column(name = "name", nullable = false, unique = true)
    @NotBlank
    private String name;

    public Scope(String name) {
        this.name = name;
    }

    public static Scope of(String name){
        return new Scope(name);
    }
}
