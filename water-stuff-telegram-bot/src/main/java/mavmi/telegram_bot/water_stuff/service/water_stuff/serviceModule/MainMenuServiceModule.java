package mavmi.telegram_bot.water_stuff.service.water_stuff.serviceModule;

import mavmi.telegram_bot.common.service.dto.common.MessageJson;
import mavmi.telegram_bot.water_stuff.cache.WaterStuffServiceDataCache;
import mavmi.telegram_bot.water_stuff.data.water.WaterInfo;
import mavmi.telegram_bot.water_stuff.service.water_stuff.container.WaterStuffServiceMessageToServiceMethodContainer;
import mavmi.telegram_bot.water_stuff.service.water_stuff.serviceModule.common.CommonServiceModule;
import mavmi.telegram_bot.water_stuff.service.dto.waterStuffService.WaterStuffServiceRq;
import mavmi.telegram_bot.water_stuff.service.dto.waterStuffService.WaterStuffServiceRs;
import mavmi.telegram_bot.common.service.method.ServiceMethod;
import mavmi.telegram_bot.common.service.serviceModule.ServiceModule;
import mavmi.telegram_bot.water_stuff.constantsHandler.WaterStuffServiceConstantsHandler;
import mavmi.telegram_bot.water_stuff.constantsHandler.dto.WaterStuffServiceConstants;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class MainMenuServiceModule implements ServiceModule<WaterStuffServiceRs, WaterStuffServiceRq> {

    private final WaterStuffServiceConstants constants;
    private final CommonServiceModule commonServiceModule;
    private final WaterStuffServiceMessageToServiceMethodContainer waterStuffServiceMessageToHandlerContainer;

    public MainMenuServiceModule(
            AddGroupServiceModule addGroupServiceModule,
            SelectGroupServiceModule selectGroupServiceModule,
            CommonServiceModule commonServiceModule,
            WaterStuffServiceConstantsHandler constantsHandler
    ) {
        this.constants = constantsHandler.get();
        this.commonServiceModule = commonServiceModule;
        this.waterStuffServiceMessageToHandlerContainer = new WaterStuffServiceMessageToServiceMethodContainer(
                Map.of(
                        constants.getRequests().getAdd(), addGroupServiceModule::handleRequest,
                        constants.getRequests().getGetGroup(), selectGroupServiceModule::handleRequest,
                        constants.getRequests().getGetFullInfo(), this::getFullInfo
                ),
                commonServiceModule::error
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

    private WaterStuffServiceRs getFullInfo(WaterStuffServiceRq request) {
        WaterStuffServiceDataCache dataCache = commonServiceModule.getCacheComponent().getCacheBucket().getDataCache(WaterStuffServiceDataCache.class);
        List<WaterInfo> waterInfoList = commonServiceModule.getUsersWaterData().getAll(dataCache.getUserId());

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
