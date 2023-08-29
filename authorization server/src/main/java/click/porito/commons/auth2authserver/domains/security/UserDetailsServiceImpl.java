package click.porito.commons.auth2authserver.domains.security;

import click.porito.commons.auth2authserver.domains.resource_owner.entity.authentication.PasswordAuthenticationEntity;
import click.porito.commons.auth2authserver.domains.resource_owner.repository.PasswordAuthenticationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final PasswordAuthenticationRepository passwordAuthenticationRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        PasswordAuthenticationEntity passwordAuthentication = passwordAuthenticationRepository.findByAccountEntity_Email(email)
                .orElseThrow(() -> new UsernameNotFoundException("username not found"));
        return UserDetailsImpl.of(passwordAuthentication);
    }
}
