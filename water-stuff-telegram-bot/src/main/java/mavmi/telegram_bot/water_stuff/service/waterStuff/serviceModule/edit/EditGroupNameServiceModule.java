package mavmi.telegram_bot.water_stuff.service.waterStuff.serviceModule.edit;

import mavmi.telegram_bot.common.service.serviceComponents.container.ServiceComponentsContainer;
import mavmi.telegram_bot.common.service.dto.common.MessageJson;
import mavmi.telegram_bot.common.service.serviceComponents.method.ServiceMethod;
import mavmi.telegram_bot.common.service.serviceComponents.serviceModule.ServiceModule;
import mavmi.telegram_bot.water_stuff.cache.WaterDataCache;
import mavmi.telegram_bot.water_stuff.data.water.UsersWaterData;
import mavmi.telegram_bot.water_stuff.data.water.inner.WaterInfo;
import mavmi.telegram_bot.water_stuff.service.dto.waterStuffService.WaterStuffServiceRq;
import mavmi.telegram_bot.water_stuff.service.waterStuff.menu.WaterStuffServiceMenu;
import mavmi.telegram_bot.water_stuff.service.waterStuff.serviceModule.common.CommonServiceModule;
import org.springframework.stereotype.Component;

@Component
public class EditGroupNameServiceModule implements ServiceModule<WaterStuffServiceRq> {

    private final CommonServiceModule commonServiceModule;
    private final ServiceComponentsContainer<WaterStuffServiceRq> serviceComponentsContainer = new ServiceComponentsContainer<>();

    public EditGroupNameServiceModule(
            CommonServiceModule commonServiceModule
    ) {
        this.commonServiceModule = commonServiceModule;
        this.serviceComponentsContainer.add(commonServiceModule.getConstants().getButtons().getChangeName(), this::onChangeName)
                .add(commonServiceModule.getConstants().getRequests().getCancel(), this::cancel)
                .setDefaultServiceMethod(this::changeName);
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

    private void onChangeName(WaterStuffServiceRq request) {
        commonServiceModule.getCacheComponent().getCacheBucket().getDataCache(WaterDataCache.class).getMenuContainer().add(WaterStuffServiceMenu.EDIT_NAME);
        commonServiceModule.sendText(request.getChatId(), commonServiceModule.getConstants().getPhrases().getEnterGroupName());
    }

    private void changeName(WaterStuffServiceRq request) {
        WaterDataCache dataCache = commonServiceModule.getCacheComponent().getCacheBucket().getDataCache(WaterDataCache.class);
        UsersWaterData usersWaterData = commonServiceModule.getUsersWaterData();
        WaterInfo waterInfo = usersWaterData.get(dataCache.getUserId(), dataCache.getSelectedGroup());
        String newGroupName = request.getMessageJson().getTextMessage();

        dataCache.setSelectedGroup(newGroupName);
        waterInfo.setName(newGroupName);

        usersWaterData.saveToFile();
        dataCache.getMessagesContainer().clearMessages();
        commonServiceModule.dropMenu();

        commonServiceModule.sendReplyKeyboard(request.getChatId(), commonServiceModule.getConstants().getPhrases().getSuccess(), commonServiceModule.getEditMenuButtons());
    }

    private void cancel(WaterStuffServiceRq request) {
        commonServiceModule.cancel(request);
    }
}
