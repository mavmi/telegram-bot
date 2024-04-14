package mavmi.telegram_bot.water_stuff.service.service.water_stuff.serviceModule;

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
public class EditGroupServiceModule implements ServiceModule<WaterStuffServiceRs, WaterStuffServiceRq> {

    private final WaterStuffServiceConstants constants;
    private final CommonServiceModule commonServiceModule;
    private final WaterStuffServiceMessageToHandlerContainer waterStuffServiceMessageToHandlerContainer;

    public EditGroupServiceModule(
            CommonServiceModule commonServiceModule,
            WaterStuffServiceConstantsHandler constantsHandler
    ) {
        this.constants = constantsHandler.get();
        this.commonServiceModule = commonServiceModule;
        this.waterStuffServiceMessageToHandlerContainer = new WaterStuffServiceMessageToHandlerContainer(
                Map.of(constants.getButtons().getEdit(), this::askForData),
                this::handleRequest
        );
    }

    @Override
    public WaterStuffServiceRs process(WaterStuffServiceRq request) {
        String msg = request.getMessageJson().getTextMessage();
        ServiceMethod<WaterStuffServiceRs, WaterStuffServiceRq> method = waterStuffServiceMessageToHandlerContainer.getMethod(msg);
        return method.process(request);
    }

    private WaterStuffServiceRs askForData(WaterStuffServiceRq request) {
        commonServiceModule.getUserSession().getCache().getMenuContainer().add(WaterStuffServiceMenu.EDIT);
        return commonServiceModule.createSendTextResponse(constants.getPhrases().getEnterGroupData());
    }

    private WaterStuffServiceRs handleRequest(WaterStuffServiceRq request) {
        WaterStuffServiceUserDataCache user = commonServiceModule.getUserSession().getCache();
        String msg = request.getMessageJson().getTextMessage();
        String[] splitted = msg.split("\n");

        try {
            if (splitted.length != 4) {
                throw new RuntimeException(constants.getPhrases().getInvalidGroupNameFormat());
            }

            UsersWaterData usersWaterData = commonServiceModule.getUsersWaterData();
            WaterInfo waterInfo = usersWaterData.get(user.getUserId(), user.getSelectedGroup());

            user.setSelectedGroup(splitted[0]);
            waterInfo.setName(splitted[0]);
            waterInfo.setDiff(Integer.parseInt(splitted[1]));
            waterInfo.setWaterFromString(splitted[2]);
            waterInfo.setFertilizeFromString(splitted[3]);
            usersWaterData.saveToFile();

            return commonServiceModule.createSendKeyboardResponse(constants.getPhrases().getSuccess(), commonServiceModule.getManageMenuButtons());
        } catch (RuntimeException e) {
            e.printStackTrace(System.out);
            return commonServiceModule.createSendKeyboardResponse(constants.getPhrases().getInvalidGroupNameFormat(), commonServiceModule.getManageMenuButtons());
        } finally {
            user.getMessagesContainer().clearMessages();
            commonServiceModule.dropMenu();
        }
    }
}
