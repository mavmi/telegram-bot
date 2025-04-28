package mavmi.telegram_bot.water_stuff.service.waterStuff;

import lombok.extern.slf4j.Slf4j;
import mavmi.telegram_bot.lib.database_starter.api.BOT_NAME;
import mavmi.telegram_bot.lib.database_starter.auth.UserAuthentication;
import mavmi.telegram_bot.lib.dto.service.common.MessageJson;
import mavmi.telegram_bot.lib.dto.service.menu.Menu;
import mavmi.telegram_bot.lib.secured_starter.secured.api.Secured;
import mavmi.telegram_bot.lib.service_api.Service;
import mavmi.telegram_bot.lib.service_api.serviceComponents.container.ServiceComponentsContainer;
import mavmi.telegram_bot.lib.service_api.serviceComponents.serviceModule.ServiceModule;
import mavmi.telegram_bot.lib.user_cache_starter.aop.api.SetupUserCaches;
import mavmi.telegram_bot.lib.user_cache_starter.cache.api.AuthCache;
import mavmi.telegram_bot.lib.user_cache_starter.cache.api.DataCache;
import mavmi.telegram_bot.lib.user_cache_starter.cache.api.UserCaches;
import mavmi.telegram_bot.water_stuff.cache.WaterAuthCache;
import mavmi.telegram_bot.water_stuff.cache.WaterDataCache;
import mavmi.telegram_bot.water_stuff.constantsHandler.WaterConstantsHandler;
import mavmi.telegram_bot.water_stuff.constantsHandler.dto.WaterConstants;
import mavmi.telegram_bot.water_stuff.service.dto.waterStuffService.WaterStuffServiceRq;
import mavmi.telegram_bot.water_stuff.service.waterStuff.menu.WaterStuffServiceMenu;
import mavmi.telegram_bot.water_stuff.service.waterStuff.serviceModule.*;
import mavmi.telegram_bot.water_stuff.service.waterStuff.serviceModule.common.CommonServiceModule;
import mavmi.telegram_bot.water_stuff.service.waterStuff.serviceModule.edit.EditGroupDiffServiceModule;
import mavmi.telegram_bot.water_stuff.service.waterStuff.serviceModule.edit.EditGroupFertilizeServiceModule;
import mavmi.telegram_bot.water_stuff.service.waterStuff.serviceModule.edit.EditGroupNameServiceModule;
import mavmi.telegram_bot.water_stuff.service.waterStuff.serviceModule.edit.EditGroupWaterServiceModule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class WaterService implements Service<WaterStuffServiceRq> {

    private final UserAuthentication userAuthentication;
    private final WaterConstants constants;
    private final CancelRequestServiceModule cancelRequestServiceModule;
    private final ServiceComponentsContainer<WaterStuffServiceRq> serviceComponentsContainer = new ServiceComponentsContainer<>();

    @Autowired
    private UserCaches userCaches;

    public WaterService(
            UserAuthentication userAuthentication,
            CommonServiceModule commonServiceModule,
            MainMenuServiceModule mainMenuServiceModule,
            ManageGroupServiceModule manageGroupServiceModule,
            AddGroupServiceModule addGroupServiceModule,
            EditGroupServiceModule editGroupServiceModule,
            EditGroupDiffServiceModule editGroupDiffServiceModule,
            EditGroupNameServiceModule editGroupNameServiceModule,
            EditGroupWaterServiceModule editGroupWaterServiceModule,
            EditGroupFertilizeServiceModule editGroupFertilizeServiceModule,
            PauseNotificationsServiceModule pauseNotificationsServiceModule,
            RemoveGroupServiceModule removeGroupServiceModule,
            SelectGroupServiceModule selectGroupServiceModule,
            CancelRequestServiceModule cancelRequestServiceModule,
            WaterConstantsHandler constantsHandler
    ) {
        this.userAuthentication = userAuthentication;
        this.constants = constantsHandler.get();
        this.cancelRequestServiceModule = cancelRequestServiceModule;
        this.serviceComponentsContainer.add(WaterStuffServiceMenu.MAIN_MENU, mainMenuServiceModule)
                .add(WaterStuffServiceMenu.MANAGE_GROUP, manageGroupServiceModule)
                .add(WaterStuffServiceMenu.ADD, addGroupServiceModule)
                .add(WaterStuffServiceMenu.EDIT, editGroupServiceModule)
                .add(WaterStuffServiceMenu.EDIT_DIFF, editGroupDiffServiceModule)
                .add(WaterStuffServiceMenu.EDIT_NAME, editGroupNameServiceModule)
                .add(WaterStuffServiceMenu.EDIT_WATER, editGroupWaterServiceModule)
                .add(WaterStuffServiceMenu.EDIT_FERTILIZE, editGroupFertilizeServiceModule)
                .add(WaterStuffServiceMenu.PAUSE, pauseNotificationsServiceModule)
                .add(WaterStuffServiceMenu.SELECT_GROUP, selectGroupServiceModule)
                .add(WaterStuffServiceMenu.RM, removeGroupServiceModule);
    }

    @SetupUserCaches
    @Secured
    @Override
    public void handleRequest(WaterStuffServiceRq request) {
        WaterDataCache dataCache = userCaches.getDataCache(WaterDataCache.class);
        MessageJson messageJson = request.getMessageJson();

        log.info("Got request from id: {}", dataCache.getUserId());

        if (messageJson != null && constants.getRequests().getCancel().equals(request.getMessageJson().getTextMessage())) {
            cancelRequestServiceModule.handleRequest(request);
        } else {
            Menu menu = dataCache.getMenuContainer().getLast();
            ServiceModule<WaterStuffServiceRq> module = serviceComponentsContainer.getModule(menu);
            module.handleRequest(request);
        }
    }

    @Override
    public DataCache initDataCache(long chatId) {
        return new WaterDataCache(chatId, WaterStuffServiceMenu.MAIN_MENU);
    }

    @Override
    public AuthCache initAuthCache(long chatId) {
        return new WaterAuthCache(userAuthentication.isPrivilegeGranted(chatId, BOT_NAME.WATER_STUFF_BOT));
    }
}
