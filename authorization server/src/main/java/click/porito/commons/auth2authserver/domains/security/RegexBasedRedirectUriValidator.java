package click.porito.commons.auth2authserver.domains.security;

import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2ErrorCodes;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2AuthorizationCodeRequestAuthenticationContext;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2AuthorizationCodeRequestAuthenticationException;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2AuthorizationCodeRequestAuthenticationToken;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;

import java.util.function.Consumer;

/**
 * registeredClient 의 허용된 redirect uri 패턴 인지 정규식을 통해 확인하는 클래스.
 * 주로 Auth2AuthorizationCodeRequestAuthenticationProvider 에서 Auth2 Authorization 요청이 들어왔을 때 사용된다.
 */
public class RegexBasedRedirectUriValidator implements Consumer<OAuth2AuthorizationCodeRequestAuthenticationContext> {

    @Override
    public void accept(OAuth2AuthorizationCodeRequestAuthenticationContext authenticationContext) {
        OAuth2AuthorizationCodeRequestAuthenticationToken authorizationCodeRequestAuthentication = authenticationContext.getAuthentication();
        RegisteredClient registeredClient = authenticationContext.getRegisteredClient();
        String requestedRedirectUri = authorizationCodeRequestAuthentication.getAuthorizationUri();

        // 정규식 패턴 매칭을 통해 허용된 redirect uri 인지 확인
        boolean anyMatch = registeredClient.getRedirectUris().stream()
                .anyMatch(requestedRedirectUri::matches);

        // 만약 맞지 않다면, 예외를 던진다.
        if (!anyMatch) {
            OAuth2Error oAuth2Error = new OAuth2Error(OAuth2ErrorCodes.INVALID_REQUEST);
            throw new OAuth2AuthorizationCodeRequestAuthenticationException(oAuth2Error, null);
        }
    }
}
