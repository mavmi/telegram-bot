package mavmi.telegram_bot.water_stuff.service.service.water_stuff.serviceModule;

import mavmi.telegram_bot.common.dto.dto.impl.water_stuff.water_stuff_service.WaterStuffServiceRq;
import mavmi.telegram_bot.common.dto.dto.impl.water_stuff.water_stuff_service.WaterStuffServiceRs;
import mavmi.telegram_bot.common.service.method.ServiceMethod;
import mavmi.telegram_bot.common.service.serviceModule.ServiceModule;
import mavmi.telegram_bot.water_stuff.service.cache.WaterStuffServiceUserDataCache;
import mavmi.telegram_bot.water_stuff.service.constantsHandler.WaterStuffServiceConstantsHandler;
import mavmi.telegram_bot.water_stuff.service.constantsHandler.dto.WaterStuffServiceConstants;
import mavmi.telegram_bot.water_stuff.service.data.DataException;
import mavmi.telegram_bot.water_stuff.service.data.water.UsersWaterData;
import mavmi.telegram_bot.water_stuff.service.data.water.WaterInfo;
import mavmi.telegram_bot.water_stuff.service.service.water_stuff.container.WaterStuffServiceMessageToHandlerContainer;
import mavmi.telegram_bot.water_stuff.service.service.water_stuff.menu.WaterStuffServiceMenu;
import mavmi.telegram_bot.water_stuff.service.service.water_stuff.serviceModule.common.CommonServiceModule;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class AddGroupServiceModule implements ServiceModule<WaterStuffServiceRs, WaterStuffServiceRq> {

    private final WaterStuffServiceConstants constants;
    private final CommonServiceModule commonServiceModule;
    private final ApproveServiceModule approveServiceModule;
    private final WaterStuffServiceMessageToHandlerContainer waterStuffServiceMessageToHandlerContainer;

    public AddGroupServiceModule(
            CommonServiceModule commonServiceModule,
            ApproveServiceModule approveServiceModule,
            WaterStuffServiceConstantsHandler constantsHandler
    ) {
        this.constants = constantsHandler.get();
        this.commonServiceModule = commonServiceModule;
        this.approveServiceModule = approveServiceModule;
        this.waterStuffServiceMessageToHandlerContainer = new WaterStuffServiceMessageToHandlerContainer(
                Map.of(
                        constants.getRequests().getAdd(), this::askForData,
                        constants.getButtons().getYes(), this::processYes,
                        constants.getButtons().getNo(), this::processNo
                ),
                this::approve
        );
    }

    @Override
    public WaterStuffServiceRs process(WaterStuffServiceRq request) {
        String msg = request.getMessageJson().getTextMessage();
        ServiceMethod<WaterStuffServiceRs, WaterStuffServiceRq> method = waterStuffServiceMessageToHandlerContainer.getMethod(msg);
        return method.process(request);
    }

    private WaterStuffServiceRs askForData(WaterStuffServiceRq request) {
        commonServiceModule.getUserSession().getCache().getMenuContainer().add(WaterStuffServiceMenu.ADD);
        return commonServiceModule.createSendTextResponse(constants.getPhrases().getAdd());
    }

    private WaterStuffServiceRs approve(WaterStuffServiceRq request) {
        String msg = request.getMessageJson().getTextMessage();
        commonServiceModule.getUserSession().getCache().getMessagesContainer().addMessage(msg);
        return approveServiceModule.process(request);
    }

    private WaterStuffServiceRs processYes(WaterStuffServiceRq request) {
        WaterStuffServiceUserDataCache user = commonServiceModule.getUserSession().getCache();
        UsersWaterData usersWaterData = commonServiceModule.getUsersWaterData();

        try {
            String[] splitted = user
                    .getMessagesContainer()
                    .getLastMessage()
                    .replaceAll(" ", "").split(";");
            if (splitted.length != 2) {
                throw new NumberFormatException();
            }
            user.getMessagesContainer().removeLastMessage();

            String name = splitted[0];
            int diff = Integer.parseInt(splitted[1]);

            WaterInfo waterInfo = new WaterInfo();
            waterInfo.setUserId(user.getUserId());
            waterInfo.setName(name);
            waterInfo.setDiff(diff);
            waterInfo.setWaterFromString(WaterInfo.NULL_STR);
            waterInfo.setFertilizeFromString(WaterInfo.NULL_STR);
            usersWaterData.put(user.getUserId(), waterInfo);

            return commonServiceModule.createSendTextResponse(constants.getPhrases().getSuccess());
        } catch (NumberFormatException | DataException e) {
            e.printStackTrace(System.out);
            return commonServiceModule.createSendTextResponse(constants.getPhrases().getInvalidGroupNameFormat());
        } finally {
            user.getMessagesContainer().clearMessages();
            commonServiceModule.dropMenu();
        }
    }

    private WaterStuffServiceRs processNo(WaterStuffServiceRq request) {
        return commonServiceModule.cancel(request);
    }
}
