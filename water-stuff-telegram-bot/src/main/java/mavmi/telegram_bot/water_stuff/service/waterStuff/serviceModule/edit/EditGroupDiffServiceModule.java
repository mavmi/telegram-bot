package mavmi.telegram_bot.water_stuff.service.waterStuff.serviceModule.edit;

import lombok.extern.slf4j.Slf4j;
import mavmi.telegram_bot.common.service.serviceComponents.container.ServiceComponentsContainer;
import mavmi.telegram_bot.common.service.dto.common.MessageJson;
import mavmi.telegram_bot.common.service.serviceComponents.method.ServiceMethod;
import mavmi.telegram_bot.common.service.serviceComponents.serviceModule.ServiceModule;
import mavmi.telegram_bot.water_stuff.cache.WaterDataCache;
import mavmi.telegram_bot.water_stuff.constantsHandler.dto.WaterConstants;
import mavmi.telegram_bot.water_stuff.data.water.UsersWaterData;
import mavmi.telegram_bot.water_stuff.data.water.inner.WaterInfo;
import mavmi.telegram_bot.water_stuff.service.dto.waterStuffService.WaterStuffServiceRq;
import mavmi.telegram_bot.water_stuff.service.waterStuff.menu.WaterStuffServiceMenu;
import mavmi.telegram_bot.water_stuff.service.waterStuff.serviceModule.common.CommonServiceModule;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class EditGroupDiffServiceModule implements ServiceModule<WaterStuffServiceRq> {

    private final CommonServiceModule commonServiceModule;
    private final ServiceComponentsContainer<WaterStuffServiceRq> serviceComponentsContainer = new ServiceComponentsContainer<>();
    public EditGroupDiffServiceModule(
            CommonServiceModule commonServiceModule
    ) {
        this.commonServiceModule = commonServiceModule;
        this.serviceComponentsContainer.add(commonServiceModule.getConstants().getButtons().getChangeDiff(), this::onChangeDiff)
                .add(commonServiceModule.getConstants().getRequests().getCancel(), this::cancel)
                .setDefaultServiceMethod(this::changeDiff);
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

    private void onChangeDiff(WaterStuffServiceRq request) {
        commonServiceModule.getCacheComponent().getCacheBucket().getDataCache(WaterDataCache.class).getMenuContainer().add(WaterStuffServiceMenu.EDIT_DIFF);
        commonServiceModule.sendText(request.getChatId(), commonServiceModule.getConstants().getPhrases().getEnterGroupDiff());
    }

    private void changeDiff(WaterStuffServiceRq request) {
        WaterDataCache dataCache = commonServiceModule.getCacheComponent().getCacheBucket().getDataCache(WaterDataCache.class);
        UsersWaterData usersWaterData = commonServiceModule.getUsersWaterData();
        WaterInfo waterInfo = usersWaterData.get(dataCache.getUserId(), dataCache.getSelectedGroup());
        WaterConstants constants = commonServiceModule.getConstants();

        try {
            int newDiffValue = Integer.parseInt(request.getMessageJson().getTextMessage());
            waterInfo.setDiff(newDiffValue);
            usersWaterData.saveToFile();

            commonServiceModule.sendReplyKeyboard(request.getChatId(), constants.getPhrases().getSuccess(), commonServiceModule.getEditMenuButtons());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            commonServiceModule.sendReplyKeyboard(request.getChatId(), constants.getPhrases().getError(), commonServiceModule.getEditMenuButtons());
        } finally {
            dataCache.getMessagesContainer().clearMessages();
            commonServiceModule.dropUserMenu();
        }
    }

    private void cancel(WaterStuffServiceRq request) {
        commonServiceModule.cancel(request);
    }
}
