package mavmi.telegram_bot.water_stuff.service.water_stuff.serviceModule.edit;

import lombok.extern.slf4j.Slf4j;
import mavmi.telegram_bot.common.service.dto.common.MessageJson;
import mavmi.telegram_bot.common.service.method.direct.ServiceMethod;
import mavmi.telegram_bot.common.service.serviceModule.direct.ServiceModule;
import mavmi.telegram_bot.water_stuff.cache.WaterStuffServiceDataCache;
import mavmi.telegram_bot.water_stuff.constantsHandler.dto.WaterStuffServiceConstants;
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
public class EditGroupDiffServiceModule implements ServiceModule<WaterStuffServiceRs, WaterStuffServiceRq> {

    private final CommonServiceModule commonServiceModule;
    private final WaterStuffServiceMessageToServiceMethodContainer waterStuffServiceMessageToHandlerContainer;

    public EditGroupDiffServiceModule(
            CommonServiceModule commonServiceModule
    ) {
        this.commonServiceModule = commonServiceModule;
        this.waterStuffServiceMessageToHandlerContainer = new WaterStuffServiceMessageToServiceMethodContainer(
                Map.of(
                        commonServiceModule.getConstants().getButtons().getChangeDiff(), this::onChangeDiff,
                        commonServiceModule.getConstants().getRequests().getCancel(), this::cancel
                ),
                this::changeDiff
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

    private WaterStuffServiceRs onChangeDiff(WaterStuffServiceRq request) {
        commonServiceModule.getCacheComponent().getCacheBucket().getDataCache(WaterStuffServiceDataCache.class).getMenuContainer().add(WaterStuffServiceMenu.EDIT_DIFF);
        return commonServiceModule.createSendTextResponse(commonServiceModule.getConstants().getPhrases().getEnterGroupDiff());
    }

    private WaterStuffServiceRs changeDiff(WaterStuffServiceRq request) {
        WaterStuffServiceDataCache dataCache = commonServiceModule.getCacheComponent().getCacheBucket().getDataCache(WaterStuffServiceDataCache.class);
        UsersWaterData usersWaterData = commonServiceModule.getUsersWaterData();
        WaterInfo waterInfo = usersWaterData.get(dataCache.getUserId(), dataCache.getSelectedGroup());
        WaterStuffServiceConstants constants = commonServiceModule.getConstants();

        try {
            int newDiffValue = Integer.parseInt(request.getMessageJson().getTextMessage());
            waterInfo.setDiff(newDiffValue);
            usersWaterData.saveToFile();
            return commonServiceModule.createSendReplyKeyboardResponse(constants.getPhrases().getSuccess(), commonServiceModule.getEditMenuButtons());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return commonServiceModule.createSendReplyKeyboardResponse(constants.getPhrases().getError(), commonServiceModule.getEditMenuButtons());
        } finally {
            dataCache.getMessagesContainer().clearMessages();
            commonServiceModule.dropMenu();
        }
    }

    private WaterStuffServiceRs cancel(WaterStuffServiceRq request) {
        return commonServiceModule.cancel(request);
    }
}
