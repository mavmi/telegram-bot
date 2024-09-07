package mavmi.telegram_bot.water_stuff.service.water_stuff.serviceModule;

import lombok.extern.slf4j.Slf4j;
import mavmi.telegram_bot.common.service.dto.common.MessageJson;
import mavmi.telegram_bot.common.service.method.direct.ServiceMethod;
import mavmi.telegram_bot.common.service.serviceModule.direct.ServiceModule;
import mavmi.telegram_bot.water_stuff.cache.WaterStuffServiceDataCache;
import mavmi.telegram_bot.water_stuff.constantsHandler.dto.WaterStuffServiceConstants;
import mavmi.telegram_bot.water_stuff.data.DataException;
import mavmi.telegram_bot.water_stuff.data.water.UsersWaterData;
import mavmi.telegram_bot.water_stuff.data.water.WaterInfo;
import mavmi.telegram_bot.water_stuff.service.dto.waterStuffService.WaterStuffServiceRq;
import mavmi.telegram_bot.water_stuff.service.dto.waterStuffService.WaterStuffServiceRs;
import mavmi.telegram_bot.water_stuff.service.water_stuff.container.WaterStuffServiceMessageToServiceMethodContainer;
import mavmi.telegram_bot.water_stuff.service.water_stuff.menu.WaterStuffServiceMenu;
import mavmi.telegram_bot.water_stuff.service.water_stuff.serviceModule.common.CommonServiceModule;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
public class AddGroupServiceModule implements ServiceModule<WaterStuffServiceRs, WaterStuffServiceRq> {

    private final CommonServiceModule commonServiceModule;
    private final ApproveServiceModule approveServiceModule;
    private final WaterStuffServiceMessageToServiceMethodContainer waterStuffServiceMessageToHandlerContainer;

    public AddGroupServiceModule(
            CommonServiceModule commonServiceModule,
            ApproveServiceModule approveServiceModule
    ) {
        this.commonServiceModule = commonServiceModule;
        this.approveServiceModule = approveServiceModule;
        this.waterStuffServiceMessageToHandlerContainer = new WaterStuffServiceMessageToServiceMethodContainer(
                Map.of(
                        commonServiceModule.getConstants().getRequests().getAdd(), this::askForData,
                        commonServiceModule.getConstants().getButtons().getYes(), this::processYes,
                        commonServiceModule.getConstants().getButtons().getNo(), this::processNo
                ),
                this::approve
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

    private WaterStuffServiceRs askForData(WaterStuffServiceRq request) {
        commonServiceModule.getCacheComponent().getCacheBucket().getDataCache(WaterStuffServiceDataCache.class).getMenuContainer().add(WaterStuffServiceMenu.ADD);
        return commonServiceModule.createSendTextResponse(commonServiceModule.getConstants().getPhrases().getAdd());
    }

    private WaterStuffServiceRs approve(WaterStuffServiceRq request) {
        String msg = request.getMessageJson().getTextMessage();
        commonServiceModule.getCacheComponent().getCacheBucket().getDataCache(WaterStuffServiceDataCache.class).getMessagesContainer().addMessage(msg);
        return approveServiceModule.handleRequest(request);
    }

    private WaterStuffServiceRs processYes(WaterStuffServiceRq request) {
        WaterStuffServiceConstants constants = commonServiceModule.getConstants();
        WaterStuffServiceDataCache dataCache = commonServiceModule.getCacheComponent().getCacheBucket().getDataCache(WaterStuffServiceDataCache.class);
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

            return commonServiceModule.createSendTextResponse(constants.getPhrases().getSuccess());
        } catch (NumberFormatException | DataException e) {
            log.error(e.getMessage(), e);
            return commonServiceModule.createSendTextResponse(constants.getPhrases().getInvalidGroupNameFormat());
        } finally {
            dataCache.getMessagesContainer().clearMessages();
            commonServiceModule.dropMenu();
        }
    }

    private WaterStuffServiceRs processNo(WaterStuffServiceRq request) {
        return commonServiceModule.cancel(request);
    }
}
