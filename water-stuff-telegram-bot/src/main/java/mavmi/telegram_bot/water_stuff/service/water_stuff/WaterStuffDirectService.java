package mavmi.telegram_bot.water_stuff.service.water_stuff;

import lombok.extern.slf4j.Slf4j;
import mavmi.telegram_bot.common.aop.cache.api.SetupUserCaches;
import mavmi.telegram_bot.common.aop.secured.api.Secured;
import mavmi.telegram_bot.common.cache.api.AuthCache;
import mavmi.telegram_bot.common.cache.api.DataCache;
import mavmi.telegram_bot.common.cache.impl.CacheComponent;
import mavmi.telegram_bot.common.database.auth.BOT_NAME;
import mavmi.telegram_bot.common.database.auth.UserAuthentication;
import mavmi.telegram_bot.common.service.container.direct.impl.MenuToServiceModuleContainer;
import mavmi.telegram_bot.common.service.dto.common.MessageJson;
import mavmi.telegram_bot.common.service.menu.Menu;
import mavmi.telegram_bot.common.service.service.direct.DirectService;
import mavmi.telegram_bot.common.service.serviceModule.direct.ServiceModule;
import mavmi.telegram_bot.water_stuff.cache.WaterStuffServiceAuthCache;
import mavmi.telegram_bot.water_stuff.cache.WaterStuffServiceDataCache;
import mavmi.telegram_bot.water_stuff.service.dto.waterStuffService.WaterStuffServiceRq;
import mavmi.telegram_bot.water_stuff.service.dto.waterStuffService.WaterStuffServiceRs;
import mavmi.telegram_bot.water_stuff.service.water_stuff.menu.WaterStuffServiceMenu;
import mavmi.telegram_bot.water_stuff.service.water_stuff.serviceModule.*;
import mavmi.telegram_bot.water_stuff.service.water_stuff.serviceModule.common.CommonServiceModule;
import mavmi.telegram_bot.water_stuff.service.water_stuff.serviceModule.edit.EditGroupDiffServiceModule;
import mavmi.telegram_bot.water_stuff.service.water_stuff.serviceModule.edit.EditGroupFertilizeServiceModule;
import mavmi.telegram_bot.water_stuff.service.water_stuff.serviceModule.edit.EditGroupNameServiceModule;
import mavmi.telegram_bot.water_stuff.service.water_stuff.serviceModule.edit.EditGroupWaterServiceModule;
import mavmi.telegram_bot.water_stuff.constantsHandler.WaterStuffServiceConstantsHandler;
import mavmi.telegram_bot.water_stuff.constantsHandler.dto.WaterStuffServiceConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;

@Slf4j
@Component
public class WaterStuffDirectService implements DirectService<WaterStuffServiceRs, WaterStuffServiceRq> {

    private final UserAuthentication userAuthentication;
    private final WaterStuffServiceConstants constants;
    private final CommonServiceModule commonServiceModule;
    private final CancelRequestServiceModule cancelRequestServiceModule;
    private final MenuToServiceModuleContainer<WaterStuffServiceRs, WaterStuffServiceRq> menuToServiceModuleContainer;

    @Autowired
    private CacheComponent cacheComponent;

    public WaterStuffDirectService(
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
            WaterStuffServiceConstantsHandler constantsHandler
    ) {
        this.userAuthentication = userAuthentication;
        this.constants = constantsHandler.get();
        this.commonServiceModule = commonServiceModule;
        this.cancelRequestServiceModule = cancelRequestServiceModule;
        this.menuToServiceModuleContainer = new MenuToServiceModuleContainer<>(
                new HashMap<>() {{
                        put(WaterStuffServiceMenu.MAIN_MENU, mainMenuServiceModule);
                        put(WaterStuffServiceMenu.MANAGE_GROUP, manageGroupServiceModule);
                        put(WaterStuffServiceMenu.ADD, addGroupServiceModule);
                        put(WaterStuffServiceMenu.EDIT, editGroupServiceModule);
                        put(WaterStuffServiceMenu.EDIT_DIFF, editGroupDiffServiceModule);
                        put(WaterStuffServiceMenu.EDIT_NAME, editGroupNameServiceModule);
                        put(WaterStuffServiceMenu.EDIT_WATER, editGroupWaterServiceModule);
                        put(WaterStuffServiceMenu.EDIT_FERTILIZE, editGroupFertilizeServiceModule);
                        put(WaterStuffServiceMenu.PAUSE, pauseNotificationsServiceModule);
                        put(WaterStuffServiceMenu.RM, removeGroupServiceModule);
                        put(WaterStuffServiceMenu.SELECT_GROUP, selectGroupServiceModule);
                }}
        );
    }

    @SetupUserCaches
    @Secured
    @Override
    public WaterStuffServiceRs handleRequest(WaterStuffServiceRq request) {
        WaterStuffServiceDataCache dataCache = cacheComponent.getCacheBucket().getDataCache(WaterStuffServiceDataCache.class);
        MessageJson messageJson = request.getMessageJson();

        log.info("Got request from id: {}", dataCache.getUserId());

        if (messageJson != null && constants.getRequests().getCancel().equals(request.getMessageJson().getTextMessage())) {
            return cancelRequestServiceModule.handleRequest(request);
        } else {
            Menu menu = dataCache.getMenuContainer().getLast();
            ServiceModule<WaterStuffServiceRs, WaterStuffServiceRq> module = menuToServiceModuleContainer.get(menu);
            return module.handleRequest(request);
        }
    }

    @Override
    public DataCache initDataCache(long chatId) {
        return new WaterStuffServiceDataCache(chatId, WaterStuffServiceMenu.MAIN_MENU);
    }

    @Override
    public AuthCache initAuthCache(long chatId) {
        return new WaterStuffServiceAuthCache(userAuthentication.isPrivilegeGranted(chatId, BOT_NAME.WATER_STUFF_BOT));
    }
}
