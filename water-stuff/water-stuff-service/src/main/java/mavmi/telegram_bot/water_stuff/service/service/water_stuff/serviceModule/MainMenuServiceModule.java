package mavmi.telegram_bot.water_stuff.service.service.water_stuff.serviceModule;

import mavmi.telegram_bot.common.dto.common.MessageJson;
import mavmi.telegram_bot.common.dto.dto.impl.water_stuff.water_stuff_service.WaterStuffServiceRq;
import mavmi.telegram_bot.common.dto.dto.impl.water_stuff.water_stuff_service.WaterStuffServiceRs;
import mavmi.telegram_bot.common.service.method.ServiceMethod;
import mavmi.telegram_bot.common.service.serviceModule.ServiceModule;
import mavmi.telegram_bot.water_stuff.service.cache.WaterStuffServiceUserDataCache;
import mavmi.telegram_bot.water_stuff.service.constantsHandler.WaterStuffServiceConstantsHandler;
import mavmi.telegram_bot.water_stuff.service.constantsHandler.dto.WaterStuffServiceConstants;
import mavmi.telegram_bot.water_stuff.service.data.water.WaterInfo;
import mavmi.telegram_bot.water_stuff.service.service.water_stuff.container.WaterStuffServiceMessageToHandlerContainer;
import mavmi.telegram_bot.water_stuff.service.service.water_stuff.serviceModule.common.CommonServiceModule;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class MainMenuServiceModule implements ServiceModule<WaterStuffServiceRs, WaterStuffServiceRq> {

    private final WaterStuffServiceConstants constants;
    private final CommonServiceModule commonServiceModule;
    private final WaterStuffServiceMessageToHandlerContainer waterStuffServiceMessageToHandlerContainer;

    public MainMenuServiceModule(
            AddGroupServiceModule addGroupServiceModule,
            SelectGroupServiceModule selectGroupServiceModule,
            CommonServiceModule commonServiceModule,
            WaterStuffServiceConstantsHandler constantsHandler
    ) {
        this.constants = constantsHandler.get();
        this.commonServiceModule = commonServiceModule;
        this.waterStuffServiceMessageToHandlerContainer = new WaterStuffServiceMessageToHandlerContainer(
                Map.of(
                        constants.getRequests().getAdd(), addGroupServiceModule::process,
                        constants.getRequests().getGetGroup(), selectGroupServiceModule::process,
                        constants.getRequests().getGetFullInfo(), this::getFullInfo
                ),
                commonServiceModule::error
        );
    }

    @Override
    public WaterStuffServiceRs process(WaterStuffServiceRq request) {
        MessageJson messageJson = request.getMessageJson();
        if (messageJson == null) {
            return commonServiceModule.createEmptyResponse();
        }
        String msg = messageJson.getTextMessage();
        ServiceMethod<WaterStuffServiceRs, WaterStuffServiceRq> method = waterStuffServiceMessageToHandlerContainer.getMethod(msg);
        return method.process(request);
    }

    private WaterStuffServiceRs getFullInfo(WaterStuffServiceRq request) {
        WaterStuffServiceUserDataCache user = commonServiceModule.getUserSession().getCache();
        List<WaterInfo> waterInfoList = commonServiceModule.getUsersWaterData().getAll(user.getUserId());

        if (waterInfoList == null || waterInfoList.isEmpty()) {
            return commonServiceModule.createSendTextResponse(constants.getPhrases().getOnEmpty());
        } else {
            StringBuilder builder = new StringBuilder();

            for (WaterInfo waterInfo : waterInfoList) {
                builder.append(commonServiceModule.getReadableWaterInfo(waterInfo)).append("\n\n");
            }

            return commonServiceModule.createSendTextResponse(builder.toString());
        }
    }
}
