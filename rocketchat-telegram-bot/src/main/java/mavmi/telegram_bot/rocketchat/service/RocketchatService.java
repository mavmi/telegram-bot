package mavmi.telegram_bot.rocketchat.service;

import lombok.extern.slf4j.Slf4j;
import mavmi.telegram_bot.common.aop.cache.api.SetupUserCaches;
import mavmi.telegram_bot.common.aop.metric.api.Metric;
import mavmi.telegram_bot.common.aop.secured.api.Secured;
import mavmi.telegram_bot.common.cache.api.AuthCache;
import mavmi.telegram_bot.common.cache.api.DataCache;
import mavmi.telegram_bot.common.database.auth.BOT_NAME;
import mavmi.telegram_bot.common.database.model.RocketchatModel;
import mavmi.telegram_bot.common.service.container.direct.impl.MenuToChainedServiceModuleContainer;
import mavmi.telegram_bot.common.service.dto.common.MessageJson;
import mavmi.telegram_bot.common.service.menu.Menu;
import mavmi.telegram_bot.common.service.method.chained.ChainedServiceModuleSecondaryMethod;
import mavmi.telegram_bot.common.service.service.chained.ChainedService;
import mavmi.telegram_bot.common.service.serviceModule.chained.ChainedServiceModule;
import mavmi.telegram_bot.rocketchat.aop.timeout.api.RequestsTimeout;
import mavmi.telegram_bot.rocketchat.cache.RocketchatServiceAuthCache;
import mavmi.telegram_bot.rocketchat.cache.RocketchatServiceDataCache;
import mavmi.telegram_bot.rocketchat.cache.inner.dataCache.RocketchatServiceDataCacheMessagesIdsHistory;
import mavmi.telegram_bot.rocketchat.service.dto.rocketchatService.RocketchatServiceRq;
import mavmi.telegram_bot.rocketchat.service.dto.rocketchatService.RocketchatServiceRs;
import mavmi.telegram_bot.rocketchat.service.menu.RocketchatServiceMenu;
import mavmi.telegram_bot.rocketchat.service.serviceModule.MainMenuServiceModule;
import mavmi.telegram_bot.rocketchat.service.serviceModule.auth.AuthGetLoginServiceModule;
import mavmi.telegram_bot.rocketchat.service.serviceModule.auth.AuthGetPasswordServiceModule;
import mavmi.telegram_bot.rocketchat.service.serviceModule.common.CommonServiceModule;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component
public class RocketchatService implements ChainedService<RocketchatServiceRs, RocketchatServiceRq> {

    private final CommonServiceModule commonServiceModule;
    private final MenuToChainedServiceModuleContainer<RocketchatServiceRs, RocketchatServiceRq> menuToServiceModuleContainer;

    public RocketchatService(
            MainMenuServiceModule mainMenuServiceModule,
            AuthGetLoginServiceModule authGetLoginServiceModule,
            AuthGetPasswordServiceModule authGetPasswordServiceModule,
            CommonServiceModule commonServiceModule) {
        this.menuToServiceModuleContainer = new MenuToChainedServiceModuleContainer<>(
                new HashMap<>() {{
                    put(RocketchatServiceMenu.MAIN_MENU, mainMenuServiceModule);
                    put(RocketchatServiceMenu.AUTH_ENTER_LOGIN, authGetLoginServiceModule);
                    put(RocketchatServiceMenu.AUTH_ENTER_PASSWORD, authGetPasswordServiceModule);
                }}
        );
        this.commonServiceModule = commonServiceModule;
    }

    @Secured
    @Override
    @RequestsTimeout
    @SetupUserCaches
    @Metric(BOT_NAME.ROCKETCHAT_BOT)
    public List<ChainedServiceModuleSecondaryMethod<RocketchatServiceRs, RocketchatServiceRq>> prepareMethodsChain(RocketchatServiceRq request) {
        RocketchatServiceDataCache dataCache = commonServiceModule.getCacheComponent().getCacheBucket().getDataCache(RocketchatServiceDataCache.class);
        MessageJson messageJson = request.getMessageJson();

        log.info("Got request from id: {}", dataCache.getUserId());

        if (messageJson == null) {
            return badRequest();
        } else {
            Menu menu = dataCache.getMenuContainer().getLast();
            ChainedServiceModule<RocketchatServiceRs, RocketchatServiceRq> module = menuToServiceModuleContainer.get(menu);
            return module.prepareMethodsChain(request);
        }
    }

    @Secured
    @Override
    @SetupUserCaches
    public RocketchatServiceRs handleRequest(RocketchatServiceRq serviceRequest, ChainedServiceModuleSecondaryMethod<RocketchatServiceRs, RocketchatServiceRq> method) {
        return method.process(serviceRequest);
    }

    @Override
    public DataCache initDataCache(long chatId) {
        Optional<RocketchatModel> databaseRecord = commonServiceModule.getRocketchatRepository().findById(chatId);
        if (databaseRecord.isEmpty()) {
            return new RocketchatServiceDataCache(chatId);
        } else {
            return commonServiceModule.getRocketchatMapper().rocketchatDatabaseModelToRocketchatDataCache(databaseRecord.get());
        }
    }

    public RocketchatServiceDataCacheMessagesIdsHistory getMessagesIdsHistory() {
        return commonServiceModule.getCacheComponent().getCacheBucket().getDataCache(RocketchatServiceDataCache.class).getMessagesIdsHistory();
    }

    public Long getActiveCommandCache() {
        return commonServiceModule.getCacheComponent().getCacheBucket().getDataCache(RocketchatServiceDataCache.class).getActiveCommandHash();
    }

    @Override
    public AuthCache initAuthCache(long chatId) {
        return new RocketchatServiceAuthCache(true);
    }

    private List<ChainedServiceModuleSecondaryMethod<RocketchatServiceRs, RocketchatServiceRq>> badRequest() {
        return List.of(this::createBadRequestResponse);
    }

    private RocketchatServiceRs createBadRequestResponse(RocketchatServiceRq request) {
        return commonServiceModule.createBadRequestResponse();
    }
}
