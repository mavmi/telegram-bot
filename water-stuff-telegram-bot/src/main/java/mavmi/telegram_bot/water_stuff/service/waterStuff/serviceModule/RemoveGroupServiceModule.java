package mavmi.telegram_bot.water_stuff.service.waterStuff.serviceModule;

import mavmi.telegram_bot.lib.dto.service.common.MessageJson;
import mavmi.telegram_bot.lib.service_api.serviceComponents.container.ServiceComponentsContainer;
import mavmi.telegram_bot.lib.service_api.serviceComponents.method.ServiceMethod;
import mavmi.telegram_bot.lib.service_api.serviceComponents.serviceModule.ServiceModule;
import mavmi.telegram_bot.water_stuff.cache.WaterDataCache;
import mavmi.telegram_bot.water_stuff.data.water.UsersWaterData;
import mavmi.telegram_bot.water_stuff.service.dto.waterStuffService.WaterStuffServiceRq;
import mavmi.telegram_bot.water_stuff.service.waterStuff.menu.WaterStuffServiceMenu;
import mavmi.telegram_bot.water_stuff.service.waterStuff.serviceModule.common.CommonServiceModule;
import org.springframework.stereotype.Component;

@Component
public class RemoveGroupServiceModule implements ServiceModule<WaterStuffServiceRq> {

    private final CommonServiceModule commonServiceModule;
    private final ApproveServiceModule approveServiceModule;
    private final ServiceComponentsContainer<WaterStuffServiceRq> serviceComponentsContainer = new ServiceComponentsContainer<>();

    public RemoveGroupServiceModule(
            CommonServiceModule commonServiceModule,
            ApproveServiceModule approveServiceModule
    ) {
        this.commonServiceModule = commonServiceModule;
        this.approveServiceModule = approveServiceModule;
        this.serviceComponentsContainer.add(commonServiceModule.getConstants().getButtons().getManageGroup().getRm(), this::approve)
                .add(commonServiceModule.getConstants().getButtons().getCommon().getYes(), this::processYes)
                .add(commonServiceModule.getConstants().getButtons().getCommon().getNo(), this::processNo);
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

    private void approve(WaterStuffServiceRq request) {
        commonServiceModule.getUserCaches().getDataCache(WaterDataCache.class).getMenuContainer().add(WaterStuffServiceMenu.RM);
        approveServiceModule.handleRequest(request);
    }

    private void processYes(WaterStuffServiceRq request) {
        WaterDataCache dataCache = commonServiceModule.getUserCaches().getDataCache(WaterDataCache.class);
        UsersWaterData usersWaterData = commonServiceModule.getUsersWaterData();

        usersWaterData.remove(dataCache.getUserId(), dataCache.getSelectedGroup());
        dataCache.getMessagesContainer().clearMessages();
        commonServiceModule.dropUserMenu(WaterStuffServiceMenu.MAIN_MENU);

        commonServiceModule.sendText(request.getChatId(), commonServiceModule.getConstants().getPhrases().getCommon().getSuccess());
    }

    private void processNo(WaterStuffServiceRq request) {
        commonServiceModule.cancel(request);
    }
}
