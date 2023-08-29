package click.porito.commons.auth2authserver.domains.security;

import click.porito.commons.auth2authserver.domains.resource_owner.entity.authentication.AccountEntity;
import click.porito.commons.auth2authserver.domains.resource_owner.entity.authentication.PasswordAuthenticationEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.Instant;
import java.util.Collection;

@Getter
public class UserDetailsImpl implements UserDetails {
    private final Collection<? extends GrantedAuthority> authorities;
    private final String password;
    private final String username;
    private final boolean isAccountExpired;
    private final boolean isAccountLocked;
    private final boolean isCredentialsExpired;
    private final boolean isDisabled;

    @Builder(access = AccessLevel.PRIVATE)
    private UserDetailsImpl(Collection<? extends GrantedAuthority> authorities, String password, String username, boolean isAccountExpired, boolean isAccountLocked, boolean isCredentialsExpired, boolean isDisabled) {
        this.authorities = authorities;
        this.password = password;
        this.username = username;
        this.isAccountExpired = isAccountExpired;
        this.isAccountLocked = isAccountLocked;
        this.isCredentialsExpired = isCredentialsExpired;
        this.isDisabled = isDisabled;
    }

    public static UserDetailsImpl of(PasswordAuthenticationEntity passwordAuthenticationEntity){
        AccountEntity accountEntity = passwordAuthenticationEntity.getAccountEntity();
        return builder().authorities(accountEntity.getRoleEntities())
                .password(passwordAuthenticationEntity.getValue())
                .username(accountEntity.getEmail())
                .isAccountExpired(Instant.now().isAfter(accountEntity.getExpiresAt()))
                .isAccountLocked(accountEntity.isLocked())
                .isCredentialsExpired(Instant.now().isAfter(passwordAuthenticationEntity.getExpiresAt()))
                .isDisabled(accountEntity.isDisabled())
                .build();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.authorities;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return !isAccountExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !isAccountLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return !isCredentialsExpired;
    }

    @Override
    public boolean isEnabled() {
        return !isDisabled;
    }
}
