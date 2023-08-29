package click.porito.commons.auth2authserver.domains.security;

import click.porito.commons.auth2authserver.domains.resource_owner.repository.PasswordAuthenticationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@Profile({"local", "test"})
@EnableWebSecurity
@RequiredArgsConstructor
public class SpringSecurityConfig {

    private final PasswordAuthenticationRepository passwordAuthenticationRepository;


    // authorizedRequest 권한 체크 필터
    @Bean
    @Order(2)
    public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests((authorize) -> authorize
                        .anyRequest().authenticated()
                )
                // Form login handles the redirect to the login page from the
                // authorization server filter chain
                .formLogin(Customizer.withDefaults());

        return http.build();
    }

    // 인증할 사용자를 검색하기 위한 UserDetailsService
    @Bean
    public UserDetailsService userDetailsService() {
        return new UserDetailsServiceImpl(passwordAuthenticationRepository);
    }
}
