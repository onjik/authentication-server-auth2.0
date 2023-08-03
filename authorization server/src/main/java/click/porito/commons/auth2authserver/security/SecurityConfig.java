package click.porito.commons.auth2authserver.security;

import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
import org.springframework.security.web.SecurityFilterChain;

public interface SecurityConfig {


    //OAuth2 Authorization Endpoints 설정
    //로컬이나 테스트 용도로, redirect_uri 를 localhost 가 가능하도록 설정
    SecurityFilterChain authorizationServerSecurityFilterChain(HttpSecurity http) throws Exception;

    // authorizedRequest 권한 체크 필터
    SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception;

    // 인증할 사용자를 검색하기 위한 UserDetailsService
    UserDetailsService userDetailsService();

    // client 를 관리하기 위한 RegisteredClientRepository
    RegisteredClientRepository registeredClientRepository();

    // access tokens 를 서명하기 위한 JwtDecoder
    JWKSource<SecurityContext> jwkSource();

    // 서명된 access token 을 해독하기 위한 JwtDecoder
    JwtDecoder jwtDecoder(JWKSource<SecurityContext> jwkSource);

    // authorization server 의 설정을 위한 AuthorizationServerSettings
    AuthorizationServerSettings authorizationServerSettings();
}
