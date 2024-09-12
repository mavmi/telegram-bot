package mavmi.telegram_bot.water_stuff.service.waterStuff.serviceModule;

import mavmi.telegram_bot.common.service.serviceComponents.container.ServiceComponentsContainer;
import mavmi.telegram_bot.common.service.dto.common.MessageJson;
import mavmi.telegram_bot.common.service.serviceComponents.method.ServiceMethod;
import mavmi.telegram_bot.common.service.serviceComponents.serviceModule.ServiceModule;
import mavmi.telegram_bot.water_stuff.cache.WaterDataCache;
import mavmi.telegram_bot.water_stuff.constantsHandler.dto.WaterConstants;
import mavmi.telegram_bot.water_stuff.service.dto.waterStuffService.WaterStuffServiceRq;
import mavmi.telegram_bot.water_stuff.service.waterStuff.menu.WaterStuffServiceMenu;
import mavmi.telegram_bot.water_stuff.service.waterStuff.serviceModule.common.CommonServiceModule;
import org.springframework.stereotype.Component;

@Component
public class SelectGroupServiceModule implements ServiceModule<WaterStuffServiceRq> {

    private final CommonServiceModule commonServiceModule;
    private final ServiceComponentsContainer<WaterStuffServiceRq> serviceComponentsContainer = new ServiceComponentsContainer<>();

    public SelectGroupServiceModule(CommonServiceModule commonServiceModule) {
        this.commonServiceModule = commonServiceModule;
        this.serviceComponentsContainer.add(commonServiceModule.getConstants().getRequests().getGetGroup(), this::askForGroupTitle)
                .setDefaultServiceMethod(this::onDefault);
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

    private void askForGroupTitle(WaterStuffServiceRq request) {
        WaterConstants constants = commonServiceModule.getConstants();
        WaterDataCache user = commonServiceModule.getCacheComponent().getCacheBucket().getDataCache(WaterDataCache.class);

        if (commonServiceModule.getUsersWaterData().size(user.getUserId()) == 0) {
            commonServiceModule.sendText(request.getChatId(), constants.getPhrases().getOnEmpty());
        } else {
            user.getMenuContainer().add(WaterStuffServiceMenu.SELECT_GROUP);
            commonServiceModule.sendReplyKeyboard(request.getChatId(), constants.getPhrases().getEnterGroupName(), commonServiceModule.getGroupsNames());
        }
    }

    private void onDefault(WaterStuffServiceRq request) {
        WaterConstants constants = commonServiceModule.getConstants();
        String msg = request.getMessageJson().getTextMessage();
        WaterDataCache dataCache = commonServiceModule.getCacheComponent().getCacheBucket().getDataCache(WaterDataCache.class);

        if (commonServiceModule.getUsersWaterData().get(dataCache.getUserId(), msg) == null) {
            dataCache.getMessagesContainer().clearMessages();
            commonServiceModule.dropMenu();
            commonServiceModule.sendTextDeleteKeyboard(request.getChatId(), constants.getPhrases().getInvalidGroupName());
        } else {
            dataCache.getMenuContainer().add(WaterStuffServiceMenu.MANAGE_GROUP);
            dataCache.setSelectedGroup(msg);
            commonServiceModule.sendReplyKeyboard(request.getChatId(), constants.getPhrases().getManageGroup(), commonServiceModule.getManageMenuButtons());
        }
    }
}
