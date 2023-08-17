package click.porito.commons.auth2authserver.domains.oauth2_client.repository;

import click.porito.commons.auth2authserver.domains.oauth2_client.entity.OAuth2AuthorizationEntity;
import click.porito.commons.auth2authserver.domains.oauth2_client.entity.token.CommonTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OAuth2AuthorizationRepository extends JpaRepository<OAuth2AuthorizationEntity,String> {
    List<OAuth2AuthorizationEntity> findByTokensValue(String value);

    @Query("SELECT a FROM OAuth2AuthorizationEntity a join fetch CommonTokenEntity t ON t.authorization = a " +
            "WHERE t.value = :value AND TYPE(t) = :clazz")
    Optional<OAuth2AuthorizationEntity> findByTokensValueAndTokensInstance(String value, Class<? extends CommonTokenEntity> clazz);

    Optional<OAuth2AuthorizationEntity> findByState(String state);


}
