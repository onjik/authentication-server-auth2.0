package click.porito.commons.auth2authserver.auth2.cotroller;

import click.porito.commons.auth2authserver.auth2.dto.OAuthRequestAttribute;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;


@Controller
@RequestMapping("/oauth2/v2")
public class OAuth2Controller {

    /**
     * OAuth2.0 entry point
     * @return
     */
    @GetMapping("/auth")
    public String oauthEntryPoint(@Valid @ModelAttribute OAuthRequestAttribute attribute) {



        System.out.println(attribute);

        return "index";
    }

}
