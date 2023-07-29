package click.porito.commons.auth2authserver.auth2.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;

@Entity
@Table(name = "passwords")
@DiscriminatorValue("PW")
@PrimaryKeyJoinColumn(name = "credential_id")
@NoArgsConstructor(access = AccessLevel.PROTECTED) @Getter
public final class Password extends Credential{

    @JsonIgnore
    @NotBlank
    @Column(name = "password", nullable = false)
    private String password;

    public Password(String password) {
        this.password = password;
    }

    public static Password of(String password){
        return new Password(password);
    }
}
