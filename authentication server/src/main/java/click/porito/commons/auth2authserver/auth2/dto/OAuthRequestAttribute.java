package click.porito.commons.auth2authserver.auth2.dto;

import click.porito.commons.auth2authserver.auth2.model.constant.ResponseType;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.URL;
import org.springframework.lang.Nullable;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Arrays;
import java.util.List;


@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class OAuthRequestAttribute {

    /**
     * Constructor for mapping query string to object
     * @param response_type : auth2.0 response type (code or token)
     * @param client_id : client id (registered service)
     * @param redirect_uri : redirect uri, used after authentication
     * @param scope : requested resource scope
     * @param state : string expected to be returned without change after authentication is over
     */
    public OAuthRequestAttribute(String response_type, String client_id, String redirect_uri, String scope, @Nullable String state) {
        //mapping
        if (response_type != null){
            this.responseType = ResponseType.fromStringIgnoreCase(response_type);
        }
        if (scope != null){
            this.scope = Arrays.asList(scope.split(" "));
        }
        this.clientId = client_id;
        this.redirectUri = redirect_uri;
        this.state = state;
    }

    @NotNull(message = "response_type must not be null")
    private ResponseType responseType;

    @NotBlank(message = "client_id must not be empty")
    private String clientId;

    @URL(message = "redirect_uri must be a valid URL")
    private String redirectUri;

    @NotEmpty(message = "scope must not be empty")
    private List<@NotBlank String> scope;
    private String state;

}
