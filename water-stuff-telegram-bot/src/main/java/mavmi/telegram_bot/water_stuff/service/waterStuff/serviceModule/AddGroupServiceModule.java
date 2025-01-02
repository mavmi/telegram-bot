package mavmi.telegram_bot.water_stuff.service.waterStuff.serviceModule;

import lombok.extern.slf4j.Slf4j;
import mavmi.telegram_bot.common.service.serviceComponents.container.ServiceComponentsContainer;
import mavmi.telegram_bot.common.service.dto.common.MessageJson;
import mavmi.telegram_bot.common.service.serviceComponents.method.ServiceMethod;
import mavmi.telegram_bot.common.service.serviceComponents.serviceModule.ServiceModule;
import mavmi.telegram_bot.water_stuff.cache.WaterDataCache;
import mavmi.telegram_bot.water_stuff.constantsHandler.dto.WaterConstants;
import mavmi.telegram_bot.water_stuff.data.exception.DataException;
import mavmi.telegram_bot.water_stuff.data.water.UsersWaterData;
import mavmi.telegram_bot.water_stuff.data.water.inner.WaterInfo;
import mavmi.telegram_bot.water_stuff.service.dto.waterStuffService.WaterStuffServiceRq;
import mavmi.telegram_bot.water_stuff.service.waterStuff.menu.WaterStuffServiceMenu;
import mavmi.telegram_bot.water_stuff.service.waterStuff.serviceModule.common.CommonServiceModule;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class AddGroupServiceModule implements ServiceModule<WaterStuffServiceRq> {

    private final CommonServiceModule commonServiceModule;
    private final ApproveServiceModule approveServiceModule;
    private final ServiceComponentsContainer<WaterStuffServiceRq> serviceComponentsContainer = new ServiceComponentsContainer<>();

    public AddGroupServiceModule(
            CommonServiceModule commonServiceModule,
            ApproveServiceModule approveServiceModule
    ) {
        this.commonServiceModule = commonServiceModule;
        this.approveServiceModule = approveServiceModule;
        this.serviceComponentsContainer.add(commonServiceModule.getConstants().getRequests().getAdd(), this::askForData)
                .add(commonServiceModule.getConstants().getButtons().getYes(), this::processYes)
                .add(commonServiceModule.getConstants().getButtons().getNo(), this::processNo)
                .setDefaultServiceMethod(this::approve);
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

    private void askForData(WaterStuffServiceRq request) {
        commonServiceModule.getCacheComponent().getCacheBucket().getDataCache(WaterDataCache.class).getMenuContainer().add(WaterStuffServiceMenu.ADD);
        commonServiceModule.sendText(request.getChatId(), commonServiceModule.getConstants().getPhrases().getAdd());
    }

    private void processYes(WaterStuffServiceRq request) {
        WaterConstants constants = commonServiceModule.getConstants();
        WaterDataCache dataCache = commonServiceModule.getCacheComponent().getCacheBucket().getDataCache(WaterDataCache.class);
        UsersWaterData usersWaterData = commonServiceModule.getUsersWaterData();

        try {
            String[] splitted = dataCache
                    .getMessagesContainer()
                    .getLastMessage()
                    .replaceAll(" ", "").split(";");
            if (splitted.length != 2) {
                throw new NumberFormatException();
            }
            dataCache.getMessagesContainer().removeLastMessage();

            String name = splitted[0];
            int diff = Integer.parseInt(splitted[1]);

            WaterInfo waterInfo = new WaterInfo();
            waterInfo.setUserId(dataCache.getUserId());
            waterInfo.setName(name);
            waterInfo.setDiff(diff);
            waterInfo.setWaterFromString(WaterInfo.NULL_STR);
            waterInfo.setFertilizeFromString(WaterInfo.NULL_STR);
            usersWaterData.put(dataCache.getUserId(), waterInfo);

            commonServiceModule.sendText(request.getChatId(), constants.getPhrases().getSuccess());
        } catch (NumberFormatException | DataException e) {
            log.error(e.getMessage(), e);
            commonServiceModule.sendText(request.getChatId(), constants.getPhrases().getInvalidGroupNameFormat());
        } finally {
            dataCache.getMessagesContainer().clearMessages();
            commonServiceModule.dropUserMenu();
        }
    }

    private void processNo(WaterStuffServiceRq request) {
        commonServiceModule.cancel(request);
    }

    private void approve(WaterStuffServiceRq request) {
        String msg = request.getMessageJson().getTextMessage();
        commonServiceModule.getCacheComponent().getCacheBucket().getDataCache(WaterDataCache.class).getMessagesContainer().addMessage(msg);
        approveServiceModule.handleRequest(request);
    }
}
