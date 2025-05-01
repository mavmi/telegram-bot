package mavmi.telegram_bot.water_stuff.service.waterStuff.serviceModule;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import mavmi.telegram_bot.lib.dto.service.common.MessageJson;
import mavmi.telegram_bot.lib.service_api.serviceComponents.container.ServiceComponentsContainer;
import mavmi.telegram_bot.lib.service_api.serviceComponents.method.ServiceMethod;
import mavmi.telegram_bot.lib.service_api.serviceComponents.serviceModule.ServiceModule;
import mavmi.telegram_bot.water_stuff.cache.WaterDataCache;
import mavmi.telegram_bot.water_stuff.constantsHandler.dto.WaterConstants;
import mavmi.telegram_bot.water_stuff.service.dto.waterStuffService.WaterStuffServiceRq;
import mavmi.telegram_bot.water_stuff.service.waterStuff.menu.WaterStuffServiceMenu;
import mavmi.telegram_bot.water_stuff.service.waterStuff.serviceModule.common.CommonServiceModule;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SelectGroupServiceModule implements ServiceModule<WaterStuffServiceRq> {

    private final CommonServiceModule commonServiceModule;
    private final ServiceComponentsContainer<WaterStuffServiceRq> serviceComponentsContainer = new ServiceComponentsContainer<>();

    @PostConstruct
    public void setup() {
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
        WaterDataCache user = commonServiceModule.getUserCaches().getDataCache(WaterDataCache.class);

        if (commonServiceModule.getUsersWaterData().size(user.getUserId()) == 0) {
            commonServiceModule.sendText(request.getChatId(), constants.getPhrases().getManageGroup().getOnEmpty());
        } else {
            user.getMenuContainer().add(WaterStuffServiceMenu.SELECT_GROUP);
            commonServiceModule.sendReplyKeyboard(request.getChatId(), constants.getPhrases().getManageGroup().getEnterGroupName(), commonServiceModule.getGroupsNames());
        }
    }

    private void onDefault(WaterStuffServiceRq request) {
        WaterConstants constants = commonServiceModule.getConstants();
        String msg = request.getMessageJson().getTextMessage();
        WaterDataCache dataCache = commonServiceModule.getUserCaches().getDataCache(WaterDataCache.class);

        if (commonServiceModule.getUsersWaterData().get(dataCache.getUserId(), msg) == null) {
            dataCache.getMessagesContainer().clearMessages();
            commonServiceModule.dropUserMenu();
            commonServiceModule.sendTextDeleteKeyboard(request.getChatId(), constants.getPhrases().getManageGroup().getInvalidGroupName());
        } else {
            dataCache.getMenuContainer().add(WaterStuffServiceMenu.MANAGE_GROUP);
            dataCache.setSelectedGroup(msg);
            commonServiceModule.sendReplyKeyboard(request.getChatId(), constants.getPhrases().getManageGroup().getManageGroup(), commonServiceModule.getManageMenuButtons());
        }
    }
}
