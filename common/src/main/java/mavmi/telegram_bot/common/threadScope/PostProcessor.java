package mavmi.telegram_bot.common.threadScope;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.cloud.context.scope.thread.ThreadScope;
import org.springframework.stereotype.Component;

/**
 * PostProcessor to register thread scope
 */
@Component
public class PostProcessor implements BeanFactoryPostProcessor {

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        beanFactory.registerScope("thread", new ThreadScope());
    }
}
