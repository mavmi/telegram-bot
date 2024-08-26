package mavmi.telegram_bot.water_stuff.service.water_stuff.serviceModule;

import mavmi.telegram_bot.common.service.dto.common.MessageJson;
import mavmi.telegram_bot.common.service.method.direct.ServiceMethod;
import mavmi.telegram_bot.common.service.serviceModule.direct.ServiceModule;
import mavmi.telegram_bot.water_stuff.cache.WaterStuffServiceDataCache;
import mavmi.telegram_bot.water_stuff.constantsHandler.dto.WaterStuffServiceConstants;
import mavmi.telegram_bot.water_stuff.service.dto.waterStuffService.WaterStuffServiceRq;
import mavmi.telegram_bot.water_stuff.service.dto.waterStuffService.WaterStuffServiceRs;
import mavmi.telegram_bot.water_stuff.service.water_stuff.container.WaterStuffServiceMessageToServiceMethodContainer;
import mavmi.telegram_bot.water_stuff.service.water_stuff.menu.WaterStuffServiceMenu;
import mavmi.telegram_bot.water_stuff.service.water_stuff.serviceModule.common.CommonServiceModule;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class SelectGroupServiceModule implements ServiceModule<WaterStuffServiceRs, WaterStuffServiceRq> {

    private final CommonServiceModule commonServiceModule;
    private final WaterStuffServiceMessageToServiceMethodContainer waterStuffServiceMessageToHandlerContainer;

    public SelectGroupServiceModule(
            CommonServiceModule commonServiceModule
    ) {
        this.commonServiceModule = commonServiceModule;
        this.waterStuffServiceMessageToHandlerContainer = new WaterStuffServiceMessageToServiceMethodContainer(
                Map.of(commonServiceModule.getConstants().getRequests().getGetGroup(), this::askForGroupTitle),
                this::onDefault
        );
    }

    @Override
    public WaterStuffServiceRs handleRequest(WaterStuffServiceRq request) {
        MessageJson messageJson = request.getMessageJson();
        if (messageJson == null) {
            return commonServiceModule.createEmptyResponse();
        }
        String msg = messageJson.getTextMessage();
        ServiceMethod<WaterStuffServiceRs, WaterStuffServiceRq> method = waterStuffServiceMessageToHandlerContainer.getMethod(msg);
        return method.process(request);
    }

    private WaterStuffServiceRs askForGroupTitle(WaterStuffServiceRq request) {
        WaterStuffServiceConstants constants = commonServiceModule.getConstants();
        WaterStuffServiceDataCache user = commonServiceModule.getCacheComponent().getCacheBucket().getDataCache(WaterStuffServiceDataCache.class);

        if (commonServiceModule.getUsersWaterData().size(user.getUserId()) == 0) {
            return commonServiceModule.createSendTextResponse(constants.getPhrases().getOnEmpty());
        } else {
            user.getMenuContainer().add(WaterStuffServiceMenu.SELECT_GROUP);
            return commonServiceModule.createSendReplyKeyboardResponse(constants.getPhrases().getEnterGroupName(), commonServiceModule.getGroupsNames());
        }
    }

    private WaterStuffServiceRs onDefault(WaterStuffServiceRq request) {
        WaterStuffServiceConstants constants = commonServiceModule.getConstants();
        String msg = request.getMessageJson().getTextMessage();
        WaterStuffServiceDataCache dataCache = commonServiceModule.getCacheComponent().getCacheBucket().getDataCache(WaterStuffServiceDataCache.class);

        if (commonServiceModule.getUsersWaterData().get(dataCache.getUserId(), msg) == null) {
            dataCache.getMessagesContainer().clearMessages();
            commonServiceModule.dropMenu();
            return commonServiceModule.createSendTextResponse(constants.getPhrases().getInvalidGroupName());
        } else {
            dataCache.getMenuContainer().add(WaterStuffServiceMenu.MANAGE_GROUP);
            dataCache.setSelectedGroup(msg);
            return commonServiceModule.createSendReplyKeyboardResponse(constants.getPhrases().getManageGroup(), commonServiceModule.getManageMenuButtons());
        }
    }
}
