package click.porito.commons.auth2authserver.common.util;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class RepositoryHolderImpl implements RepositoryHolder, ApplicationContextAware {

    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public <T extends JpaRepository<?, ?>> T getRepository(Class<T> repositoryClass) throws BeansException {
        return applicationContext.getBean(repositoryClass);
    }

    @Override
    public <T extends JpaRepository<?, ?>> T getRepository(Class<T> repositoryClass, String beanName) throws BeansException {
        return applicationContext.getBean(beanName, repositoryClass);
    }

}
