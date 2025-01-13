package mavmi.telegram_bot.water_stuff.service.waterStuff.serviceModule;

import mavmi.telegram_bot.common.service.serviceComponents.container.ServiceComponentsContainer;
import mavmi.telegram_bot.common.service.dto.common.MessageJson;
import mavmi.telegram_bot.common.service.serviceComponents.method.ServiceMethod;
import mavmi.telegram_bot.common.service.serviceComponents.serviceModule.ServiceModule;
import mavmi.telegram_bot.water_stuff.cache.WaterDataCache;
import mavmi.telegram_bot.water_stuff.data.water.inner.WaterInfo;
import mavmi.telegram_bot.water_stuff.service.dto.waterStuffService.WaterStuffServiceRq;
import mavmi.telegram_bot.water_stuff.service.waterStuff.serviceModule.common.CommonServiceModule;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MainMenuServiceModule implements ServiceModule<WaterStuffServiceRq> {

    private final CommonServiceModule commonServiceModule;
    private final ServiceComponentsContainer<WaterStuffServiceRq> serviceComponentsContainer = new ServiceComponentsContainer<>();

    public MainMenuServiceModule(
            AddGroupServiceModule addGroupServiceModule,
            SelectGroupServiceModule selectGroupServiceModule,
            CommonServiceModule commonServiceModule
    ) {
        this.commonServiceModule = commonServiceModule;
        this.serviceComponentsContainer.add(commonServiceModule.getConstants().getRequests().getAdd(), addGroupServiceModule::handleRequest)
                .add(commonServiceModule.getConstants().getRequests().getGetGroup(), selectGroupServiceModule::handleRequest)
                .add(commonServiceModule.getConstants().getRequests().getGetFullInfo(), this::getFullInfo)
                .setDefaultServiceMethod(commonServiceModule::error);
    }

    @Override
    public void handleRequest(WaterStuffServiceRq request) {
        MessageJson messageJson = request.getMessageJson();
        if (messageJson == null) {
            return;
        }

        String msg = messageJson.getTextMessage();
        ServiceMethod<WaterStuffServiceRq> method = serviceComponentsContainer.getMethod(msg);
        method.process(request);
    }

    private void getFullInfo(WaterStuffServiceRq request) {
        WaterDataCache dataCache = commonServiceModule.getCacheComponent().getCacheBucket().getDataCache(WaterDataCache.class);
        List<WaterInfo> waterInfoList = commonServiceModule.getUsersWaterData().getAll(dataCache.getUserId());

        if (waterInfoList == null || waterInfoList.isEmpty()) {
            commonServiceModule.sendText(request.getChatId(), commonServiceModule.getConstants().getPhrases().getManageGroup().getOnEmpty());
        } else {
            StringBuilder builder = new StringBuilder();

            for (WaterInfo waterInfo : waterInfoList) {
                builder.append(commonServiceModule.getReadableWaterInfo(waterInfo)).append("\n\n");
            }

            commonServiceModule.sendText(request.getChatId(), builder.toString());
        }
    }
}
