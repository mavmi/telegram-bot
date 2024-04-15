package mavmi.telegram_bot.water_stuff.service.service.water_stuff.serviceModule;

import mavmi.telegram_bot.common.dto.dto.impl.water_stuff.water_stuff_service.WaterStuffServiceRq;
import mavmi.telegram_bot.common.dto.dto.impl.water_stuff.water_stuff_service.WaterStuffServiceRs;
import mavmi.telegram_bot.common.service.method.ServiceMethod;
import mavmi.telegram_bot.common.service.serviceModule.ServiceModule;
import mavmi.telegram_bot.water_stuff.service.cache.WaterStuffServiceUserDataCache;
import mavmi.telegram_bot.water_stuff.service.constantsHandler.WaterStuffServiceConstantsHandler;
import mavmi.telegram_bot.water_stuff.service.constantsHandler.dto.WaterStuffServiceConstants;
import mavmi.telegram_bot.water_stuff.service.service.water_stuff.container.WaterStuffServiceMessageToHandlerContainer;
import mavmi.telegram_bot.water_stuff.service.service.water_stuff.menu.WaterStuffServiceMenu;
import mavmi.telegram_bot.water_stuff.service.service.water_stuff.serviceModule.common.CommonServiceModule;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class SelectGroupServiceModule implements ServiceModule<WaterStuffServiceRs, WaterStuffServiceRq> {

    private final WaterStuffServiceConstants constants;
    private final CommonServiceModule commonServiceModule;
    private final WaterStuffServiceMessageToHandlerContainer waterStuffServiceMessageToHandlerContainer;

    public SelectGroupServiceModule(
            CommonServiceModule commonServiceModule,
            WaterStuffServiceConstantsHandler constantsHandler
    ) {
        this.constants = constantsHandler.get();
        this.commonServiceModule = commonServiceModule;
        this.waterStuffServiceMessageToHandlerContainer = new WaterStuffServiceMessageToHandlerContainer(
                Map.of(constants.getRequests().getGetGroup(), this::askForGroupTitle),
                this::handleRequest
        );
    }

    @Override
    public WaterStuffServiceRs process(WaterStuffServiceRq request) {
        String msg = request.getMessageJson().getTextMessage();
        ServiceMethod<WaterStuffServiceRs, WaterStuffServiceRq> method = waterStuffServiceMessageToHandlerContainer.getMethod(msg);
        return method.process(request);
    }

    private WaterStuffServiceRs askForGroupTitle(WaterStuffServiceRq request) {
        WaterStuffServiceUserDataCache user = commonServiceModule.getUserSession().getCache();

        if (commonServiceModule.getUsersWaterData().size(user.getUserId()) == 0) {
            return commonServiceModule.createSendTextResponse(constants.getPhrases().getOnEmpty());
        } else {
            user.getMenuContainer().add(WaterStuffServiceMenu.SELECT_GROUP);
            return commonServiceModule.createSendKeyboardResponse(constants.getPhrases().getEnterGroupName(), commonServiceModule.getGroupsNames());
        }
    }

    private WaterStuffServiceRs handleRequest(WaterStuffServiceRq request) {
        String msg = request.getMessageJson().getTextMessage();
        WaterStuffServiceUserDataCache user = commonServiceModule.getUserSession().getCache();

        if (commonServiceModule.getUsersWaterData().get(user.getUserId(), msg) == null) {
            commonServiceModule.getUserSession().getCache().getMessagesContainer().clearMessages();
            commonServiceModule.dropMenu();
            return commonServiceModule.createSendTextResponse(constants.getPhrases().getInvalidGroupName());
        } else {
            user.getMenuContainer().add(WaterStuffServiceMenu.MANAGE_GROUP);
            user.setSelectedGroup(msg);
            return commonServiceModule.createSendKeyboardResponse(constants.getPhrases().getManageGroup(), commonServiceModule.getManageMenuButtons());
        }
    }
}
