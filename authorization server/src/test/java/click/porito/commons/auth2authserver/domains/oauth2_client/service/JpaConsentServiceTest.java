package click.porito.commons.auth2authserver.domains.oauth2_client.service;

import click.porito.commons.auth2authserver.domains.oauth2_client.entity.ClientEntity;
import click.porito.commons.auth2authserver.domains.oauth2_client.entity.OAuth2AuthorizationConsentEntity;
import click.porito.commons.auth2authserver.domains.oauth2_client.entity.static_entity.AuthorizationGrantTypeEntity;
import click.porito.commons.auth2authserver.domains.oauth2_client.entity.static_entity.ClientAuthenticationMethodEntity;
import click.porito.commons.auth2authserver.domains.oauth2_client.entity.static_entity.ScopeEntity;
import click.porito.commons.auth2authserver.domains.oauth2_client.repository.ClientRepository;
import click.porito.commons.auth2authserver.domains.oauth2_client.repository.ConsentRepository;
import click.porito.commons.auth2authserver.domains.oauth2_client.repository.ScopeRepository;
import click.porito.commons.auth2authserver.domains.resource_owner.entity.ResourceOwnerEntity;
import click.porito.commons.auth2authserver.domains.resource_owner.entity.static_entity.RoleEntity;
import click.porito.commons.auth2authserver.domains.resource_owner.repository.ResourceOwnerRepository;
import click.porito.commons.auth2authserver.domains.resource_owner.repository.RoleRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationConsent;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.lang.reflect.Field;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static test_util.TestEntityFactory.*;

@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@DataJpaTest
class JpaConsentServiceTest {

    @Autowired
    EntityManager em;
    @Autowired
    ConsentRepository consentRepository;
    @Autowired
    ClientRepository clientRepository;
    @Autowired
    ResourceOwnerRepository resourceOwnerRepository;
    @Autowired
    RoleRepository roleRepository;
    @Autowired
    ScopeRepository scopeRepository;


    JpaConsentService jpaConsentService;
    OAuth2AuthorizationConsentEntity consentEntity;


    @BeforeEach
    void setUp() {
        jpaConsentService = new JpaConsentService(consentRepository,clientRepository,resourceOwnerRepository,roleRepository,scopeRepository);

        //given
        RoleEntity role = getRoleEntity();
        ScopeEntity scope = getScopeEntity();
        AuthorizationGrantTypeEntity grantType = getAuthorizationGrantTypeEntity();
        ClientAuthenticationMethodEntity method = getClientAuthenticationMethodEntity();
        em.persist(role);
        em.persist(scope);
        em.persist(grantType);
        em.persist(method);

        ResourceOwnerEntity resourceOwner = getResourceOwnerEntity(role);
        em.persist(resourceOwner);
        ClientEntity client = getClientEntity(scope, method, grantType);
        em.persist(client);
        consentEntity = getOAuth2AuthorizationConsentEntity(resourceOwner, client, Set.of(role), Set.of(scope));
    }

    @Nested
    @DisplayName("save 메서드 테스트")
    class save {
        @Test
        @DisplayName("정상적으로 저장하는 경우")
        void saveCorrectly() {
            //given
            OAuth2AuthorizationConsent oAuth2AuthorizationConsent = consentEntity.toObject();
            //when
            jpaConsentService.save(oAuth2AuthorizationConsent);

            //then
            String clientId = consentEntity.getClient().getId();
            String resourceOwnerId = consentEntity.getResourceOwner().getId();
            OAuth2AuthorizationConsentEntity findedEntity = consentRepository.findByClientIdAndResourceOwnerId(clientId, resourceOwnerId);
            assertEquals(consentEntity.getScopes(), findedEntity.getScopes());
            assertEquals(consentEntity.getRoles(), findedEntity.getRoles());
            assertEquals(consentEntity.getResourceOwner(), findedEntity.getResourceOwner());
            assertEquals(consentEntity.getClient(), findedEntity.getClient());
        }

        @Test
        @DisplayName("이미 저장된 경우")
        void alreadySaved() {
            //given
            OAuth2AuthorizationConsent oAuth2AuthorizationConsent = consentEntity.toObject();
            jpaConsentService.save(oAuth2AuthorizationConsent);

            //when
            Assertions.assertThrows(
                    DataIntegrityViolationException.class,
                    () -> jpaConsentService.save(oAuth2AuthorizationConsent)
            );
        }

        @Test
        @DisplayName("인자로 null이 들어온 경우")
        void nullArgument() {
            //given
            OAuth2AuthorizationConsent oAuth2AuthorizationConsent = null;

            //when
            Assertions.assertThrows(
                    IllegalArgumentException.class,
                    () -> jpaConsentService.save(oAuth2AuthorizationConsent)
            );
        }

        @Test
        @DisplayName("연관 관계에 있는 엔티티가 persist되지 않은 경우")
        void invalidRelatedEntity() {
            //given
            OAuth2AuthorizationConsent oAuth2AuthorizationConsent = consentEntity.toObject();
            em.remove(consentEntity.getClient());

            //when
            Assertions.assertThrows(
                    DataRetrievalFailureException.class,
                    () -> jpaConsentService.save(oAuth2AuthorizationConsent)
            );
        }



    }

    @Nested
    @DisplayName("remove 메서드 테스트")
    class remove {

        @Test
        @DisplayName("정상적으로 삭제하는 경우")
        void removeCorrectly() {
            //given
            OAuth2AuthorizationConsent oAuth2AuthorizationConsent = consentEntity.toObject();
            jpaConsentService.save(oAuth2AuthorizationConsent);

            //when
            jpaConsentService.remove(oAuth2AuthorizationConsent);

            //then
            String clientId = consentEntity.getClient().getId();
            String resourceOwnerId = consentEntity.getResourceOwner().getId();
            OAuth2AuthorizationConsentEntity findedEntity = consentRepository.findByClientIdAndResourceOwnerId(clientId, resourceOwnerId);
            assertEquals(null, findedEntity);
        }

        @Test
        @DisplayName("없는 consent를 삭제하는 경우")
        void removeAbsentConsent() {
            //given
            OAuth2AuthorizationConsent oAuth2AuthorizationConsent = consentEntity.toObject();

            //when
            assertThrows(
                    EmptyResultDataAccessException.class,
                    () -> jpaConsentService.remove(oAuth2AuthorizationConsent)
            );
        }

        @Test
        @DisplayName("인자로 null이 들어온 경우")
        void nullArgument() {
            //given
            OAuth2AuthorizationConsent oAuth2AuthorizationConsent = null;

            //when
            assertThrows(
                    IllegalArgumentException.class,
                    () -> jpaConsentService.remove(oAuth2AuthorizationConsent)
            );
        }

        @Test
        @DisplayName("인자로 주어진 객체에서, 필수적인 필드가 전달되지 않은 경우")
        void invalidArgument() throws NoSuchFieldException, IllegalAccessException {
            //given
            OAuth2AuthorizationConsent oAuth2AuthorizationConsent = consentEntity.toObject();
            Field field = OAuth2AuthorizationConsent.class.getDeclaredField("registeredClientId");
            field.setAccessible(true);
            field.set(oAuth2AuthorizationConsent, null);


            //when
            assertThrows(
                    IllegalArgumentException.class,
                    () -> jpaConsentService.remove(oAuth2AuthorizationConsent)
            );
        }
    }

    @Nested
    @DisplayName("findById 메서드 테스트")
    class findById {

        @Test
        @DisplayName("정상적인 경우")
        void callCorrectly(){
            //given
            OAuth2AuthorizationConsent oAuth2AuthorizationConsent = consentEntity.toObject();
            jpaConsentService.save(oAuth2AuthorizationConsent);

            //when
            OAuth2AuthorizationConsent findedConsent = jpaConsentService.findById(oAuth2AuthorizationConsent.getRegisteredClientId(), oAuth2AuthorizationConsent.getPrincipalName());

            //then
            assertEquals(oAuth2AuthorizationConsent, findedConsent);
        }

        @Test
        @DisplayName("인자로 null이 들어온 경우")
        void nullArgument(){
            //given
            String clientId = null;
            String resourceOwnerId = null;

            //when
            assertThrows(
                    IllegalArgumentException.class,
                    () -> jpaConsentService.findById(clientId, resourceOwnerId)
            );
        }

        @Test
        @DisplayName("없는 consent를 찾는 경우")
        void findAbsentConsent(){
            //given
            OAuth2AuthorizationConsent oAuth2AuthorizationConsent = consentEntity.toObject();

            //when
            OAuth2AuthorizationConsent result = jpaConsentService.findById(oAuth2AuthorizationConsent.getRegisteredClientId(), oAuth2AuthorizationConsent.getPrincipalName());

            //then
            assertNull(result);
        }
    }


}