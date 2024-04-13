package mavmi.telegram_bot.water_stuff.service.service.water_stuff;

import lombok.extern.slf4j.Slf4j;
import mavmi.telegram_bot.common.cache.userData.UserDataCache;
import mavmi.telegram_bot.common.dto.dto.impl.water_stuff.water_stuff_service.WaterStuffServiceRq;
import mavmi.telegram_bot.common.dto.dto.impl.water_stuff.water_stuff_service.WaterStuffServiceRs;
import mavmi.telegram_bot.common.service.container.impl.MenuToServiceServiceModuleContainer;
import mavmi.telegram_bot.common.service.menu.Menu;
import mavmi.telegram_bot.common.service.service.AbstractService;
import mavmi.telegram_bot.common.service.serviceModule.ServiceModule;
import mavmi.telegram_bot.water_stuff.service.cache.WaterStuffServiceUserDataCache;
import mavmi.telegram_bot.water_stuff.service.constants.Buttons;
import mavmi.telegram_bot.water_stuff.service.service.water_stuff.menu.WaterStuffServiceMenu;
import mavmi.telegram_bot.water_stuff.service.service.water_stuff.serviceModule.*;
import mavmi.telegram_bot.water_stuff.service.service.water_stuff.serviceModule.common.CommonServiceModule;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
public class WaterStuffService extends AbstractService {

    private static final String[] MANAGE_MENU_BUTTONS = new String[] {
            Buttons.INFO_BTN,
            Buttons.PAUSE_BTN,
            Buttons.CONTINUE_BTN,
            Buttons.WATER_BTN,
            Buttons.FERTILIZE_BTN,
            Buttons.EDIT_BTN,
            Buttons.RM_BTN,
            Buttons.EXIT_BTN
    };

    private final CommonServiceModule commonServiceModule;
    private final MenuToServiceServiceModuleContainer<WaterStuffServiceRs, WaterStuffServiceRq> menuToServiceServiceModuleContainer;

    public WaterStuffService(
            CommonServiceModule commonServiceModule,
            MainMenuServiceModule mainMenuServiceModule,
            ManageGroupServiceModule manageGroupServiceModule,
            AddGroupServiceModule addGroupServiceModule,
            EditGroupServiceModule editGroupServiceModule,
            RemoveGroupServiceModule removeGroupServiceModule,
            SelectGroupServiceModule selectGroupServiceModule
    ) {
        this.commonServiceModule = commonServiceModule;
        this.menuToServiceServiceModuleContainer = new MenuToServiceServiceModuleContainer<>(
                Map.of(
                        WaterStuffServiceMenu.MAIN_MENU, mainMenuServiceModule,
                        WaterStuffServiceMenu.MANAGE_GROUP, manageGroupServiceModule,
                        WaterStuffServiceMenu.ADD, addGroupServiceModule,
                        WaterStuffServiceMenu.EDIT, editGroupServiceModule,
                        WaterStuffServiceMenu.RM, removeGroupServiceModule,
                        WaterStuffServiceMenu.SELECT_GROUP, selectGroupServiceModule
                )
        );
    }

    public WaterStuffServiceRs handleRequest(WaterStuffServiceRq request) {
        WaterStuffServiceUserDataCache userCache = commonServiceModule.getUserSession().getCache();
        log.info("Got request. id: {}; username: {}, first name: {}; last name: {}, message: {}",
                userCache.getUserId(),
                userCache.getUsername(),
                userCache.getFirstName(),
                userCache.getLastName(),
                request.getMessageJson().getTextMessage()
        );

        Menu menu = commonServiceModule.getUserSession().getCache().getMenuContainer().getLast();
        ServiceModule<WaterStuffServiceRs, WaterStuffServiceRq> module = menuToServiceServiceModuleContainer.get(menu);
        return module.process(request);
    }

    @Override
    public UserDataCache initCache() {
        return new WaterStuffServiceUserDataCache(commonServiceModule.getUserSession().getId(), WaterStuffServiceMenu.MAIN_MENU);
    }
}
