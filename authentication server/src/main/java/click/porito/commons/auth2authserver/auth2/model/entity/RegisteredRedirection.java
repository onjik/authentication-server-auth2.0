package click.porito.commons.auth2authserver.auth2.model.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.URL;

import javax.persistence.*;

@Entity
@Table(name = "registered_redirections", uniqueConstraints = {@UniqueConstraint(columnNames = {"url_pattern", "service_id"})})
@NoArgsConstructor(access = AccessLevel.PROTECTED) @Getter
public final class RegisteredRedirection {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "red_id")
    private Long id;

    @URL
    @Column(name = "url_pattern", nullable = false)
    private String urlPattern;

    public RegisteredRedirection(String urlPattern) {
        this.urlPattern = urlPattern;
    }

    public static RegisteredRedirection of(String urlPattern){
        return new RegisteredRedirection(urlPattern);
    }


}
