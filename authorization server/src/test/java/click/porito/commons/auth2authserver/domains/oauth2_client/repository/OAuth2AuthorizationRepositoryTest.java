package click.porito.commons.auth2authserver.domains.oauth2_client.repository;

import click.porito.commons.auth2authserver.domains.oauth2_client.entity.static_entity.AuthorizationGrantTypeEntity;
import click.porito.commons.auth2authserver.domains.oauth2_client.entity.static_entity.ClientAuthenticationMethodEntity;
import click.porito.commons.auth2authserver.domains.oauth2_client.entity.static_entity.ScopeEntity;
import click.porito.commons.auth2authserver.domains.resource_owner.entity.static_entity.RoleEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@DataJpaTest
class OAuth2AuthorizationRepositoryTest {

    @Autowired
    OAuth2AuthorizationRepository oAuth2AuthorizationRepository;


    @PersistenceContext
    EntityManager em;

    ScopeEntity scope;
    RoleEntity roleEntity;
    ClientAuthenticationMethodEntity method;
    AuthorizationGrantTypeEntity grantType;

    @BeforeEach
    void staticDataSetUp(){
        //static data
        scope = new ScopeEntity("http://localhost/test", "test");
        roleEntity = RoleEntity.ofUser("USER", RoleEntity.DEFAULT_USER_PRIORITY);
        method = ClientAuthenticationMethodEntity.of(ClientAuthenticationMethod.CLIENT_SECRET_JWT);
        grantType = AuthorizationGrantTypeEntity.from(AuthorizationGrantType.JWT_BEARER);
        em.persist(scope);
        em.persist(roleEntity);
        em.persist(method);
        em.persist(grantType);
    }




}