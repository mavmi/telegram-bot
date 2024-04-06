package mavmi.telegram_bot.water_stuff.service.service.water_stuff.serviceModule;

import mavmi.telegram_bot.common.dto.dto.impl.water_stuff.water_stuff_service.WaterStuffServiceRq;
import mavmi.telegram_bot.common.dto.dto.impl.water_stuff.water_stuff_service.WaterStuffServiceRs;
import mavmi.telegram_bot.common.service.method.ServiceMethod;
import mavmi.telegram_bot.common.service.serviceModule.ServiceModule;
import mavmi.telegram_bot.water_stuff.service.cache.WaterStuffServiceUserDataCache;
import mavmi.telegram_bot.water_stuff.service.constants.Buttons;
import mavmi.telegram_bot.water_stuff.service.constants.Phrases;
import mavmi.telegram_bot.water_stuff.service.data.water.UsersWaterData;
import mavmi.telegram_bot.water_stuff.service.service.water_stuff.container.WaterStuffServiceMessageToHandlerContainer;
import mavmi.telegram_bot.water_stuff.service.service.water_stuff.menu.WaterStuffServiceMenu;
import mavmi.telegram_bot.water_stuff.service.service.water_stuff.serviceModule.common.CommonServiceModule;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class RemoveGroupServiceModule implements ServiceModule<WaterStuffServiceRs, WaterStuffServiceRq> {

    private final CommonServiceModule commonServiceModule;
    private final ApproveServiceModule approveServiceModule;
    private final WaterStuffServiceMessageToHandlerContainer waterStuffServiceMessageToHandlerContainer;

    public RemoveGroupServiceModule(
            CommonServiceModule commonServiceModule,
            ApproveServiceModule approveServiceModule
    ) {
        this.commonServiceModule = commonServiceModule;
        this.approveServiceModule = approveServiceModule;
        this.waterStuffServiceMessageToHandlerContainer = new WaterStuffServiceMessageToHandlerContainer(
                Map.of(
                        Buttons.RM_BTN, this::approve,
                        Buttons.YES_BTN, this::processYes,
                        Buttons.NO_BTN, this::processNo
                ),
                null
        );
    }

    @Override
    public WaterStuffServiceRs process(WaterStuffServiceRq request) {
        String msg = request.getMessageJson().getTextMessage();
        ServiceMethod<WaterStuffServiceRs, WaterStuffServiceRq> method = waterStuffServiceMessageToHandlerContainer.getMethod(msg);
        return method.process(request);
    }

    private WaterStuffServiceRs approve(WaterStuffServiceRq request) {
        commonServiceModule.getUserSession().getCache().getMenuContainer().add(WaterStuffServiceMenu.RM);
        return approveServiceModule.process(request);
    }

    private WaterStuffServiceRs processYes(WaterStuffServiceRq request) {
        WaterStuffServiceUserDataCache user = commonServiceModule.getUserSession().getCache();
        UsersWaterData usersWaterData = commonServiceModule.getUsersWaterData();

        usersWaterData.remove(user.getUserId(), user.getSelectedGroup());
        commonServiceModule.getUserSession().getCache().getMessagesContainer().clearMessages();
        commonServiceModule.dropMenu(WaterStuffServiceMenu.MAIN_MENU);

        return commonServiceModule.createSendTextResponse(Phrases.SUCCESS_MSG);
    }

    private WaterStuffServiceRs processNo(WaterStuffServiceRq request) {
        commonServiceModule.getUserSession().getCache().getMessagesContainer().clearMessages();
        commonServiceModule.dropMenu();

        return commonServiceModule.createSendKeyboardResponse(
                Phrases.OPERATION_CANCELED_MSG,
                CommonServiceModule.MANAGE_MENU_BUTTONS
        );
    }
}
