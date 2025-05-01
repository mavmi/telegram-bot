package mavmi.telegram_bot.lib.user_cache_starter.provider;

import lombok.extern.slf4j.Slf4j;
import mavmi.telegram_bot.lib.user_cache_starter.cache.api.UserCaches;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class UserCachesProvider {

    private static final String SCOPE_NAME = "thread";
    private static final String BEAN_NAME = "userCaches";

    @Autowired
    private ObjectProvider<UserCaches> objectProvider;
    @Autowired
    private ConfigurableBeanFactory configurableBeanFactory;

    public UserCaches get() {
        return objectProvider.getObject();
    }

    public void clean() {
        try {
            UserCaches userCaches = configurableBeanFactory.getBean(BEAN_NAME, UserCaches.class);
            configurableBeanFactory.getRegisteredScope(SCOPE_NAME).remove(BEAN_NAME);
            log.info("Delete UserCaches bean with id {}", userCaches.getId());
        } catch (NullPointerException e) {
            log.error(e.getMessage(), e);
        }
    }
}
