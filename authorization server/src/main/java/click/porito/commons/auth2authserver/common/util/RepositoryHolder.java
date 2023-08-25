package click.porito.commons.auth2authserver.common.util;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanNotOfRequiredTypeException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.NoUniqueBeanDefinitionException;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Spring Context 에 등록된 JpaRepository 를 관리하는 클래스 입니다. <br>
 * 다음과 같은 이점이 있습니다.
 * <ul>다른 빈에 과도하게 많은 repository 가 주입되는 것을 방지하여, 가독성과 유지보수를 좋게합니다.</ul>
 * <ul>인자로 받는 클래스 타입을 JpaRepository 의 하위 클래스로 제한 하므로 잘못된 호출을 방지할 수 있습니다.</ul>
 * <ul>테스트 코드 작성시, repository 에 대한 목업을 처리하기 편합니다.</ul>
 * @see RepositoryHolderImpl
 */
public interface RepositoryHolder {

    /**
     * 원하는 타입의 JpaRepository 를 반환한다.
     * @param repositoryClass JpaRepository 의 타입
     * @return 빈으로 등록된 JpaRepository
     * @throws NoSuchBeanDefinitionException - if no bean of the given type was found
     * @throws NoUniqueBeanDefinitionException - if more than one bean of the given type was found
     * @throws BeansException - if the bean could not be created
     */
    <T extends JpaRepository<?, ?>> T getRepository(Class<T> repositoryClass) throws BeansException;

    /**
     * 이름과 원하는 타입의 JpaRepository 를 반환한다.
     * @param repositoryClass JpaRepository 의 타입
     * @param beanName JpaRepository 의 빈 이름
     * @return 빈으로 등록된 JpaRepository
     * @throws NoSuchBeanDefinitionException - if there is no such bean definition
     * @throws BeanNotOfRequiredTypeException - if the bean is not of the required type
     * @throws BeansException - if the bean could not be created
     */
    <T extends JpaRepository<?, ?>> T getRepository(Class<T> repositoryClass, String beanName) throws BeansException;

}
