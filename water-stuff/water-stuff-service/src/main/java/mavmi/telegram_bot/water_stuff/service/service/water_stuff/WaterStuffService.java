package mavmi.telegram_bot.water_stuff.service.service.water_stuff;

import lombok.extern.slf4j.Slf4j;
import mavmi.telegram_bot.common.cache.userData.UserDataCache;
import mavmi.telegram_bot.common.dto.common.MessageJson;
import mavmi.telegram_bot.common.dto.dto.impl.water_stuff.water_stuff_service.WaterStuffServiceRq;
import mavmi.telegram_bot.common.dto.dto.impl.water_stuff.water_stuff_service.WaterStuffServiceRs;
import mavmi.telegram_bot.common.service.container.impl.MenuToServiceServiceModuleContainer;
import mavmi.telegram_bot.common.service.menu.Menu;
import mavmi.telegram_bot.common.service.service.AbstractService;
import mavmi.telegram_bot.common.service.serviceModule.ServiceModule;
import mavmi.telegram_bot.water_stuff.service.cache.WaterStuffServiceUserDataCache;
import mavmi.telegram_bot.water_stuff.service.constantsHandler.WaterStuffServiceConstantsHandler;
import mavmi.telegram_bot.water_stuff.service.constantsHandler.dto.WaterStuffServiceConstants;
import mavmi.telegram_bot.water_stuff.service.service.water_stuff.menu.WaterStuffServiceMenu;
import mavmi.telegram_bot.water_stuff.service.service.water_stuff.serviceModule.*;
import mavmi.telegram_bot.water_stuff.service.service.water_stuff.serviceModule.common.CommonServiceModule;
import mavmi.telegram_bot.water_stuff.service.service.water_stuff.serviceModule.edit.EditGroupDiffServiceModule;
import mavmi.telegram_bot.water_stuff.service.service.water_stuff.serviceModule.edit.EditGroupFertilizeServiceModule;
import mavmi.telegram_bot.water_stuff.service.service.water_stuff.serviceModule.edit.EditGroupNameServiceModule;
import mavmi.telegram_bot.water_stuff.service.service.water_stuff.serviceModule.edit.EditGroupWaterServiceModule;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Slf4j
@Service
public class WaterStuffService extends AbstractService {

    private final WaterStuffServiceConstants constants;
    private final CommonServiceModule commonServiceModule;
    private final CancelRequestServiceModule cancelRequestServiceModule;
    private final MenuToServiceServiceModuleContainer<WaterStuffServiceRs, WaterStuffServiceRq> menuToServiceServiceModuleContainer;

    public WaterStuffService(
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
        this.constants = constantsHandler.get();
        this.commonServiceModule = commonServiceModule;
        this.cancelRequestServiceModule = cancelRequestServiceModule;
        this.menuToServiceServiceModuleContainer = new MenuToServiceServiceModuleContainer<>(
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

    public WaterStuffServiceRs handleRequest(WaterStuffServiceRq request) {
        MessageJson messageJson = request.getMessageJson();

        WaterStuffServiceUserDataCache userCache = commonServiceModule.getUserSession().getCache();
        log.info("Got request from id: {}", userCache.getUserId());

        if (messageJson != null && constants.getRequests().getCancel().equals(request.getMessageJson().getTextMessage())) {
            return cancelRequestServiceModule.process(request);
        } else {
            Menu menu = commonServiceModule.getUserSession().getCache().getMenuContainer().getLast();
            ServiceModule<WaterStuffServiceRs, WaterStuffServiceRq> module = menuToServiceServiceModuleContainer.get(menu);
            return module.process(request);
        }
    }

    @Override
    public UserDataCache initCache() {
        return new WaterStuffServiceUserDataCache(commonServiceModule.getUserSession().getId(), WaterStuffServiceMenu.MAIN_MENU);
    }
}
