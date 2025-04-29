package mavmi.telegram_bot.monitoring.service.monitoring;

import lombok.RequiredArgsConstructor;
import mavmi.telegram_bot.lib.database_starter.api.BOT_NAME;
import mavmi.telegram_bot.lib.database_starter.api.PRIVILEGE;
import mavmi.telegram_bot.lib.database_starter.auth.UserAuthentication;
import mavmi.telegram_bot.lib.database_starter.model.PrivilegesModel;
import mavmi.telegram_bot.lib.database_starter.repository.PrivilegesRepository;
import mavmi.telegram_bot.lib.user_cache_starter.cache.api.AuthCache;
import mavmi.telegram_bot.lib.user_cache_starter.cache.api.DataCache;
import mavmi.telegram_bot.lib.user_cache_starter.cacheInitializer.api.CacheInitializer;
import mavmi.telegram_bot.monitoring.cache.MonitoringAuthCache;
import mavmi.telegram_bot.monitoring.cache.MonitoringDataCache;
import mavmi.telegram_bot.monitoring.service.monitoring.menu.MonitoringServiceMenu;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class MonitoringBotCacheInitializer implements CacheInitializer {

    private final PrivilegesRepository privilegesRepository;
    private final UserAuthentication userAuthentication;

    @Override
    public DataCache initDataCache(long chatId) {
        Optional<PrivilegesModel> optional = privilegesRepository.findById(chatId);
        List<PRIVILEGE> privileges = (optional.isPresent()) ? optional.get().getPrivileges() : Collections.emptyList();

        return new MonitoringDataCache(chatId, MonitoringServiceMenu.MAIN_MENU, privileges);
    }

    @Override
    public AuthCache initAuthCache(long chatId) {
        return new MonitoringAuthCache(userAuthentication.isPrivilegeGranted(chatId, BOT_NAME.MONITORING_BOT));
    }
}
