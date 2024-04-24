package mavmi.telegram_bot.water_stuff.service.service.water_stuff.serviceModule.edit;

import mavmi.telegram_bot.common.dto.common.MessageJson;
import mavmi.telegram_bot.common.dto.dto.impl.water_stuff.water_stuff_service.WaterStuffServiceRq;
import mavmi.telegram_bot.common.dto.dto.impl.water_stuff.water_stuff_service.WaterStuffServiceRs;
import mavmi.telegram_bot.common.service.method.ServiceMethod;
import mavmi.telegram_bot.common.service.serviceModule.ServiceModule;
import mavmi.telegram_bot.water_stuff.service.cache.WaterStuffServiceUserDataCache;
import mavmi.telegram_bot.water_stuff.service.constantsHandler.WaterStuffServiceConstantsHandler;
import mavmi.telegram_bot.water_stuff.service.constantsHandler.dto.WaterStuffServiceConstants;
import mavmi.telegram_bot.water_stuff.service.data.water.UsersWaterData;
import mavmi.telegram_bot.water_stuff.service.data.water.WaterInfo;
import mavmi.telegram_bot.water_stuff.service.service.water_stuff.container.WaterStuffServiceMessageToHandlerContainer;
import mavmi.telegram_bot.water_stuff.service.service.water_stuff.menu.WaterStuffServiceMenu;
import mavmi.telegram_bot.water_stuff.service.service.water_stuff.serviceModule.common.CommonServiceModule;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class EditGroupDiffServiceModule implements ServiceModule<WaterStuffServiceRs, WaterStuffServiceRq> {

    private final WaterStuffServiceConstants constants;
    private final CommonServiceModule commonServiceModule;
    private final WaterStuffServiceMessageToHandlerContainer waterStuffServiceMessageToHandlerContainer;

    public EditGroupDiffServiceModule(
            CommonServiceModule commonServiceModule,
            WaterStuffServiceConstantsHandler constantsHandler
    ) {
        this.constants = constantsHandler.get();
        this.commonServiceModule = commonServiceModule;
        this.waterStuffServiceMessageToHandlerContainer = new WaterStuffServiceMessageToHandlerContainer(
                Map.of(
                        constants.getButtons().getChangeDiff(), this::handleRequest,
                        constants.getRequests().getCancel(), this::cancel
                ),
                this::changeDiff
        );
    }

    @Override
    public WaterStuffServiceRs process(WaterStuffServiceRq request) {
        MessageJson messageJson = request.getMessageJson();
        if (messageJson == null) {
            return commonServiceModule.createEmptyResponse();
        }
        String msg = messageJson.getTextMessage();
        ServiceMethod<WaterStuffServiceRs, WaterStuffServiceRq> method = waterStuffServiceMessageToHandlerContainer.getMethod(msg);
        return method.process(request);
    }

    private WaterStuffServiceRs handleRequest(WaterStuffServiceRq request) {
        commonServiceModule.getUserSession().getCache().getMenuContainer().add(WaterStuffServiceMenu.EDIT_DIFF);
        return commonServiceModule.createSendTextResponse(constants.getPhrases().getEnterGroupDiff());
    }

    private WaterStuffServiceRs changeDiff(WaterStuffServiceRq request) {
        WaterStuffServiceUserDataCache user = commonServiceModule.getUserSession().getCache();
        UsersWaterData usersWaterData = commonServiceModule.getUsersWaterData();
        WaterInfo waterInfo = usersWaterData.get(user.getUserId(), user.getSelectedGroup());

        try {
            int newDiffValue = Integer.parseInt(request.getMessageJson().getTextMessage());
            waterInfo.setDiff(newDiffValue);
            usersWaterData.saveToFile();
            return commonServiceModule.createSendReplyKeyboardResponse(constants.getPhrases().getSuccess(), commonServiceModule.getEditMenuButtons());
        } catch (Exception e) {
            e.printStackTrace(System.out);
            return commonServiceModule.createSendReplyKeyboardResponse(constants.getPhrases().getError(), commonServiceModule.getEditMenuButtons());
        } finally {
            user.getMessagesContainer().clearMessages();
            commonServiceModule.dropMenu();
        }
    }

    private WaterStuffServiceRs cancel(WaterStuffServiceRq request) {
        return commonServiceModule.cancel(request);
    }
}
