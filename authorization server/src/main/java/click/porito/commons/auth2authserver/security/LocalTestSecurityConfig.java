package click.porito.commons.auth2authserver.security;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.oauth2.server.resource.OAuth2ResourceServerConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2AuthorizationCodeRequestAuthenticationContext;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2AuthorizationCodeRequestAuthenticationProvider;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2AuthorizationCodeRequestAuthenticationToken;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2AuthorizationCodeRequestAuthenticationValidator;
import org.springframework.security.oauth2.server.authorization.client.InMemoryRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

@Configuration
@Profile({"local", "test"})
@EnableWebSecurity
public class LocalTestSecurityConfig implements SecurityConfig {

    //OAuth2 Authorization Endpoints 설정
    //로컬이나 테스트 용도로, redirect_uri 를 localhost 가 가능하도록 설정
    @Override
    @Bean
    @Order(1)
    public SecurityFilterChain authorizationServerSecurityFilterChain(HttpSecurity http) throws Exception {

        //OAuth2 서버를 구성하는 빌더
        OAuth2AuthorizationServerConfigurer authorizationServerConfigurer = new OAuth2AuthorizationServerConfigurer();
        http.apply(authorizationServerConfigurer);
        authorizationServerConfigurer
                /*
                인증 서버의 정보를 얻는데 사용하는 엔드포인트
                일반적으로 /.well-known/oauth-authorization-server 를 사용한다.
                 */
                .authorizationServerMetadataEndpoint(Customizer.withDefaults())
                // Authorization Request 란 클라이언트가 처음에 인증 서버로 리다이렉트 하는 그 엔드포인트
                .authorizationEndpoint(authorizationEndpoint -> {
                    authorizationEndpoint
                            // Authorization Request 의 검증 방법 커스텀
                            .authenticationProviders(configureAuthenticationValidator());
                })
                .oidc(Customizer.withDefaults());	// Enable OpenID Connect 1.0

        //기본 spring security 설정
        http
                .csrf().disable()
                // Redirect to the login page when not authenticated from the
                // authorization endpoint
                .exceptionHandling((exceptions) -> exceptions
                        .authenticationEntryPoint(
                                new LoginUrlAuthenticationEntryPoint("/login"))
                )
                // Accept access tokens for User Info and/or ClientEntity Registration
                .oauth2ResourceServer(OAuth2ResourceServerConfigurer::jwt);

        return http.build();
    }


    // authorizedRequest 권한 체크 필터
    @Override
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
    @Override
    @Bean
    public UserDetailsService userDetailsService() {
        UserDetails userDetails = User.withDefaultPasswordEncoder()
                .username("user")
                .password("password")
                .roles("USER")
                .build();

        return new InMemoryUserDetailsManager(userDetails);
    }

    // client 를 관리하기 위한 RegisteredClientRepository
    @Override
    @Bean
    public RegisteredClientRepository registeredClientRepository() {
        RegisteredClient registeredClient = RegisteredClient.withId(UUID.randomUUID().toString())
                .clientId("messaging-client")
                .clientSecret("{noop}secret")
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
                .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
                .redirectUri("http://127.0.0.1:8080/login/oauth2/code/messaging-client-oidc")
                .redirectUri("http://127.0.0.1:8080/authorized")
                .scope(OidcScopes.OPENID)
                .scope(OidcScopes.PROFILE)
                .scope("message.read")
                .scope("message.write")
                .clientSettings(ClientSettings.builder().requireAuthorizationConsent(true).build())
                .build();

        return new InMemoryRegisteredClientRepository(registeredClient);
    }

    // access tokens 를 서명하기 위한 JwtDecoder
    @Override
    @Bean
    public JWKSource<SecurityContext> jwkSource() {
        KeyPair keyPair = generateRsaKey();
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
        RSAKey rsaKey = new RSAKey.Builder(publicKey)
                .privateKey(privateKey)
                .keyID(UUID.randomUUID().toString())
                .build();
        JWKSet jwkSet = new JWKSet(rsaKey);
        return new ImmutableJWKSet<>(jwkSet);
    }

    // 서명된 access token 을 해독하기 위한 JwtDecoder
    @Override
    @Bean
    public JwtDecoder jwtDecoder(JWKSource<SecurityContext> jwkSource) {
        return OAuth2AuthorizationServerConfiguration.jwtDecoder(jwkSource);
    }

    // authorization server 의 설정을 위한 AuthorizationServerSettings
    @Override
    @Bean
    public AuthorizationServerSettings authorizationServerSettings() {
        return AuthorizationServerSettings.builder().build();
    }


    /**
     * 테스트를 위한, Authorization validator
     */
    static class LocalHostAllowedValidator extends RegexBasedRedirectUriValidator {
        @Override
        public void accept(OAuth2AuthorizationCodeRequestAuthenticationContext authenticationContext) {
            OAuth2AuthorizationCodeRequestAuthenticationToken authorizationCodeRequestAuthentication = (OAuth2AuthorizationCodeRequestAuthenticationToken) authenticationContext.getAuthentication();
            String redirectUri = authorizationCodeRequestAuthentication.getRedirectUri();

            // 로컬 환경 테스트를 위해, localhost 를 허용한다.
            if (redirectUri.matches("^(https?://)?localhost(.*)$")) {
                return;
            }

            // 그외의 경우에는 정규식을 기반으로 검증한다.
            super.accept(authenticationContext);
        }
    }

    /**
     * @return {@code OAuth2AuthorizationCodeRequestAuthenticationProvider}를 찾아서 authenticationValidator 를 설정하는 컨슈머
     */
    private Consumer<List<AuthenticationProvider>> configureAuthenticationValidator() {
        return authenticationProviders -> {
            authenticationProviders.forEach(authenticationProvider -> {
                if (authenticationProvider instanceof OAuth2AuthorizationCodeRequestAuthenticationProvider auth2AuthenticationProvider) {
                    /*
                    1. uri 검사
                     - 로컬 호스트인지 검사 (로컬 테스트 목적 스탭)
                     - 정규식 검사 (본래 validator)
                    2. 스코프 검사
                     */
                    Consumer<OAuth2AuthorizationCodeRequestAuthenticationContext> authenticationValidator =
                            new LocalHostAllowedValidator()
                                    .andThen(OAuth2AuthorizationCodeRequestAuthenticationValidator.DEFAULT_SCOPE_VALIDATOR);
                    auth2AuthenticationProvider.setAuthenticationValidator(authenticationValidator);
                }
            });
        };
    }



    static KeyPair generateRsaKey() {
        KeyPair keyPair;
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(2048);
            keyPair = keyPairGenerator.generateKeyPair();
        } catch (Exception ex) {
            throw new IllegalStateException(ex);
        }
        return keyPair;
    }


}
