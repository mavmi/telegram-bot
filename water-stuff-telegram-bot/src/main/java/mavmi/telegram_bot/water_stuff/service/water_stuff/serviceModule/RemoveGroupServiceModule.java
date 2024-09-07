package mavmi.telegram_bot.water_stuff.service.water_stuff.serviceModule;

import mavmi.telegram_bot.common.service.dto.common.MessageJson;
import mavmi.telegram_bot.common.service.method.direct.ServiceMethod;
import mavmi.telegram_bot.common.service.serviceModule.direct.ServiceModule;
import mavmi.telegram_bot.water_stuff.cache.WaterStuffServiceDataCache;
import mavmi.telegram_bot.water_stuff.data.water.UsersWaterData;
import mavmi.telegram_bot.water_stuff.service.dto.waterStuffService.WaterStuffServiceRq;
import mavmi.telegram_bot.water_stuff.service.dto.waterStuffService.WaterStuffServiceRs;
import mavmi.telegram_bot.water_stuff.service.water_stuff.container.WaterStuffServiceMessageToServiceMethodContainer;
import mavmi.telegram_bot.water_stuff.service.water_stuff.menu.WaterStuffServiceMenu;
import mavmi.telegram_bot.water_stuff.service.water_stuff.serviceModule.common.CommonServiceModule;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class RemoveGroupServiceModule implements ServiceModule<WaterStuffServiceRs, WaterStuffServiceRq> {

    private final CommonServiceModule commonServiceModule;
    private final ApproveServiceModule approveServiceModule;
    private final WaterStuffServiceMessageToServiceMethodContainer waterStuffServiceMessageToHandlerContainer;

    public RemoveGroupServiceModule(
            CommonServiceModule commonServiceModule,
            ApproveServiceModule approveServiceModule
    ) {
        this.commonServiceModule = commonServiceModule;
        this.approveServiceModule = approveServiceModule;
        this.waterStuffServiceMessageToHandlerContainer = new WaterStuffServiceMessageToServiceMethodContainer(
                Map.of(
                        commonServiceModule.getConstants().getButtons().getRm(), this::approve,
                        commonServiceModule.getConstants().getButtons().getYes(), this::processYes,
                        commonServiceModule.getConstants().getButtons().getNo(), this::processNo
                )
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

    private WaterStuffServiceRs approve(WaterStuffServiceRq request) {
        commonServiceModule.getCacheComponent().getCacheBucket().getDataCache(WaterStuffServiceDataCache.class).getMenuContainer().add(WaterStuffServiceMenu.RM);
        return approveServiceModule.handleRequest(request);
    }

    private WaterStuffServiceRs processYes(WaterStuffServiceRq request) {
        WaterStuffServiceDataCache dataCache = commonServiceModule.getCacheComponent().getCacheBucket().getDataCache(WaterStuffServiceDataCache.class);
        UsersWaterData usersWaterData = commonServiceModule.getUsersWaterData();

        usersWaterData.remove(dataCache.getUserId(), dataCache.getSelectedGroup());
        dataCache.getMessagesContainer().clearMessages();
        commonServiceModule.dropMenu(WaterStuffServiceMenu.MAIN_MENU);

        return commonServiceModule.createSendTextResponse(commonServiceModule.getConstants().getPhrases().getSuccess());
    }

    private WaterStuffServiceRs processNo(WaterStuffServiceRq request) {
        return commonServiceModule.cancel(request);
    }
}
