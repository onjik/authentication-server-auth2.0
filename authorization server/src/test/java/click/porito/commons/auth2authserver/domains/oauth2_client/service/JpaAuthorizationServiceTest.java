package click.porito.commons.auth2authserver.domains.oauth2_client.service;

import click.porito.commons.auth2authserver.domains.oauth2_client.entity.ClientEntity;
import click.porito.commons.auth2authserver.domains.oauth2_client.entity.OAuth2AuthorizationEntity;
import click.porito.commons.auth2authserver.domains.oauth2_client.entity.static_entity.AuthorizationGrantTypeEntity;
import click.porito.commons.auth2authserver.domains.oauth2_client.entity.static_entity.ClientAuthenticationMethodEntity;
import click.porito.commons.auth2authserver.domains.oauth2_client.entity.static_entity.ScopeEntity;
import click.porito.commons.auth2authserver.domains.oauth2_client.entity.token.AccessTokenEntity;
import click.porito.commons.auth2authserver.domains.oauth2_client.repository.AuthorizationGrantTypeRepository;
import click.porito.commons.auth2authserver.domains.oauth2_client.repository.CommonTokenRepository;
import click.porito.commons.auth2authserver.domains.oauth2_client.repository.OAuth2AuthorizationRepository;
import click.porito.commons.auth2authserver.domains.oauth2_client.repository.ScopeRepository;
import click.porito.commons.auth2authserver.domains.resource_owner.entity.ResourceOwnerEntity;
import click.porito.commons.auth2authserver.domains.resource_owner.entity.static_entity.RoleEntity;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.lang.reflect.Field;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static test_util.TestEntityFactory.*;


@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@DataJpaTest
class JpaAuthorizationServiceTest {

    @Autowired
    private EntityManager em;

    @Autowired
    private ScopeRepository scopeRepository;

    @Autowired
    private CommonTokenRepository commonTokenRepository;

    @Autowired
    private OAuth2AuthorizationRepository oAuth2AuthorizationRepository;

    @Autowired
    private AuthorizationGrantTypeRepository authorizationGrantTypeRepository;


    private JpaAuthorizationService authorizationService;

    OAuth2AuthorizationEntity authorization;



    @BeforeEach
    void setUp() {
        authorizationService = new JpaAuthorizationService(em,scopeRepository,commonTokenRepository,oAuth2AuthorizationRepository,authorizationGrantTypeRepository);
        ScopeEntity scopeEntity = getScopeEntity();
        ClientAuthenticationMethodEntity method = getClientAuthenticationMethodEntity();
        AuthorizationGrantTypeEntity grantType = getAuthorizationGrantTypeEntity();
        RoleEntity role = getRoleEntity();
        em.persist(scopeEntity);
        em.persist(method);
        em.persist(grantType);
        em.persist(role);

        ClientEntity client = getClientEntity(scopeEntity, method, grantType);
        em.persist(client);
        ResourceOwnerEntity resourceOwner = getResourceOwnerEntity(role);
        em.persist(resourceOwner);
        this.authorization = getOAuth2AuthorizationEntity(client, resourceOwner, grantType);
    }

    @Nested
    @DisplayName("save 메서드 테스트")
    class save {
        @Test
        @DisplayName("적절히 호출하였을 때")
        void saveProperly() {
            //given
            AccessTokenEntity accessToken = getAccessTokenEntity(authorization);
            authorization.addToken(accessToken);

            OAuth2Authorization oAuth2Authorization = authorization.toObject();

            //when
            authorizationService.save(oAuth2Authorization);

            //then
            OAuth2AuthorizationEntity foundEntity = em.find(OAuth2AuthorizationEntity.class, authorization.getId());
            assertNotNull(foundEntity);
            assertEquals(foundEntity.getId(), authorization.getId());
        }

        @Test
        @DisplayName("인자로 null을 넘겼을 때")
        void nullArgument(){
            //when
            assertThrows(
                    IllegalArgumentException.class,
                    () -> authorizationService.save(null)
            );
        }

        @Test
        @DisplayName("연관 엔티티에 대한 잘못된 지시")
        void nullNessaaryField() throws NoSuchFieldException, IllegalAccessException {
            //given
            OAuth2Authorization oAuth2Authorization = authorization.toObject();
            Field field = oAuth2Authorization.getClass().getDeclaredField("principalName");
            field.setAccessible(true);
            field.set(oAuth2Authorization, "wrong");

            //when
            assertThrows(
                    DataRetrievalFailureException.class,
                    () -> authorizationService.save(oAuth2Authorization)
            );
        }

        @Test
        @DisplayName("필수적인 자체 필드에 대해 잘못된 값을 전달")
        void invalidProperty() throws NoSuchFieldException, IllegalAccessException {
            //given
            OAuth2Authorization oAuth2Authorization = authorization.toObject();
            Field id = oAuth2Authorization.getClass().getDeclaredField("id");
            id.setAccessible(true);
            id.set(oAuth2Authorization,null);

            //then
            assertThrows(
                    DataAccessException.class,
                    () -> authorizationService.save(oAuth2Authorization)
            );

        }

    }

    @Nested
    @DisplayName("remove 메서드 테스트")
    class remove{
        @Test
        @DisplayName("적절히 호출하였을 때")
        void removeProperly() {
            //given
            OAuth2Authorization oAuth2Authorization = authorization.toObject();
            authorizationService.save(oAuth2Authorization);

            //when
            authorizationService.remove(oAuth2Authorization);

            //then
            OAuth2AuthorizationEntity foundEntity = em.find(OAuth2AuthorizationEntity.class, authorization.getId());
            assertNull(foundEntity);
        }

        @Test
        @DisplayName("인자로 null을 넘겼을 때")
        void nullArgument(){
            //when
            assertThrows(
                    IllegalArgumentException.class,
                    () -> authorizationService.remove(null)
            );
        }

        @Test
        @DisplayName("저장되지 않은 엔티티를 삭제하려고 할때")
        void removeNonPersistedEntity(){
            //given
            OAuth2Authorization oAuth2Authorization = authorization.toObject();

            //when
            assertThrows(
                    EmptyResultDataAccessException.class,
                    () -> authorizationService.remove(oAuth2Authorization)
            );
        }
    }

    @Nested
    @DisplayName("findById 메서드 테스트")
    class findById {
        @Test
        @DisplayName("적절히 호출하였을 때")
        void callProperly() {
            //given
            OAuth2Authorization oAuth2Authorization = authorization.toObject();
            authorizationService.save(oAuth2Authorization);

            //when
            OAuth2Authorization foundAuthorization = authorizationService.findById(oAuth2Authorization.getId());

            //then
            assertNotNull(foundAuthorization);
            assertEquals(foundAuthorization.getId(), authorization.getId());
        }

        @Test
        @DisplayName("인자로 null을 넘겼을 때")
        void nullArgument(){
            //when
            assertThrows(
                    IllegalArgumentException.class,
                    () -> authorizationService.findById(null)
            );
        }

        @Test
        @DisplayName("존재하지 않는 엔티티를 찾으려고 할 때")
        void findNonExistentEntity(){
            //when
            OAuth2Authorization result = authorizationService.findById(UUID.randomUUID().toString());

            //then
            assertNull(result);
        }
    }



    @Nested
    @DisplayName("findByClient 메서드 테스트")
    class findByToken {
        @Test
        @DisplayName("적절히 호출하였을 때")
        void callProperly() {
            //given
            AccessTokenEntity accessTokenEntity = getAccessTokenEntity(authorization);
            authorization.addToken(accessTokenEntity); //persist cascade
            em.persist(authorization);

            OAuth2Authorization oAuth2Authorization = authorization.toObject();
            authorizationService.save(oAuth2Authorization);

            //when
            OAuth2Authorization foundAuthorization = authorizationService.findByToken(accessTokenEntity.getValue(), OAuth2TokenType.ACCESS_TOKEN);

            //then
            assertNotNull(foundAuthorization);
            assertEquals(foundAuthorization.getId(), authorization.getId());
        }

        @Test
        @DisplayName("인자로 null을 넘겼을 때")
        void nullArgument(){
            //when
            assertThrows(
                    IllegalArgumentException.class,
                    () -> authorizationService.findByToken(null, OAuth2TokenType.ACCESS_TOKEN)
            );
        }

        @Test
        @DisplayName("존재하지 않는 엔티티를 찾으려고 할 때")
        void findAbsenceEntity() {
            //given
            AccessTokenEntity accessTokenEntity = getAccessTokenEntity(authorization);
            authorization.addToken(accessTokenEntity); //persist cascade
            em.persist(authorization);

            OAuth2Authorization oAuth2Authorization = authorization.toObject();
            authorizationService.save(oAuth2Authorization);

            //when
            OAuth2Authorization foundAuthorization = authorizationService.findByToken(accessTokenEntity.getValue() + "diff", OAuth2TokenType.ACCESS_TOKEN);

            //then
            assertNull(foundAuthorization);
        }
    }


}