package click.porito.commons.auth2authserver.auth2.model.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Entity
@Table(name = "remember_me_tokens")
@NoArgsConstructor(access = AccessLevel.PROTECTED) @Getter
public final class RememberMeToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "remember_me_token_id")
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id",nullable = false)
    private User owner;

    @Column(name = "token_value", nullable = false, unique = true)
    @NotBlank
    private String tokenValue;

    @Column(name = "last_used_date", nullable = false)
    private LocalDateTime lastUsedDate;

    @Column(name = "created_date", nullable = false)
    private LocalDateTime createdDate;


    @PrePersist
    private void recordCurrentTime(){
        this.createdDate = LocalDateTime.now();
        this.lastUsedDate = LocalDateTime.now();
    }

    public RememberMeToken(User owner, String tokenValue) {
        this.owner = owner;
        this.tokenValue = tokenValue;
    }
}
