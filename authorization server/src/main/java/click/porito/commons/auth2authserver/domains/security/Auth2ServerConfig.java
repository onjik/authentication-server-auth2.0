package click.porito.commons.auth2authserver.domains.security;

import click.porito.commons.auth2authserver.common.util.RepositoryHolder;
import click.porito.commons.auth2authserver.domains.oauth2_client.service.JpaAuthorizationService;
import click.porito.commons.auth2authserver.domains.oauth2_client.service.JpaConsentService;
import click.porito.commons.auth2authserver.domains.oauth2_client.service.JpaRegisteredClientService;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.oauth2.server.resource.OAuth2ResourceServerConfigurer;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2AuthorizationCodeRequestAuthenticationContext;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2AuthorizationCodeRequestAuthenticationProvider;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2AuthorizationCodeRequestAuthenticationToken;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2AuthorizationCodeRequestAuthenticationValidator;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
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
@RequiredArgsConstructor
public class Auth2ServerConfig {

    private final RepositoryHolder repositoryHolder;

    //OAuth2 Authorization Endpoints 설정
    //로컬이나 테스트 용도로, redirect_uri 를 localhost 가 가능하도록 설정
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


    // client 를 관리하기 위한 RegisteredClientRepository
    @Bean
    public RegisteredClientRepository registeredClientRepository() {
        return new JpaRegisteredClientService(repositoryHolder);
    }

    public OAuth2AuthorizationService oAuth2AuthorizationService() {
        return new JpaAuthorizationService(repositoryHolder);
    }

    public JpaConsentService jpaConsentService() {
        return new JpaConsentService(repositoryHolder);
    }

    // access tokens 를 서명하기 위한 JwtDecoder
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
    @Bean
    public JwtDecoder jwtDecoder(JWKSource<SecurityContext> jwkSource) {
        return OAuth2AuthorizationServerConfiguration.jwtDecoder(jwkSource);
    }

    // authorization server 의 설정을 위한 AuthorizationServerSettings
    @Bean
    public AuthorizationServerSettings authorizationServerSettings() {
        return AuthorizationServerSettings.builder().build();
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
