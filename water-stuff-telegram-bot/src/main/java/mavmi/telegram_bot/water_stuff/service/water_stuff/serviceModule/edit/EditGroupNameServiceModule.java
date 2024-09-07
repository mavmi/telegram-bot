package mavmi.telegram_bot.water_stuff.service.water_stuff.serviceModule.edit;

import mavmi.telegram_bot.common.service.dto.common.MessageJson;
import mavmi.telegram_bot.common.service.method.direct.ServiceMethod;
import mavmi.telegram_bot.common.service.serviceModule.direct.ServiceModule;
import mavmi.telegram_bot.water_stuff.cache.WaterStuffServiceDataCache;
import mavmi.telegram_bot.water_stuff.data.water.UsersWaterData;
import mavmi.telegram_bot.water_stuff.data.water.WaterInfo;
import mavmi.telegram_bot.water_stuff.service.dto.waterStuffService.WaterStuffServiceRq;
import mavmi.telegram_bot.water_stuff.service.dto.waterStuffService.WaterStuffServiceRs;
import mavmi.telegram_bot.water_stuff.service.water_stuff.container.WaterStuffServiceMessageToServiceMethodContainer;
import mavmi.telegram_bot.water_stuff.service.water_stuff.menu.WaterStuffServiceMenu;
import mavmi.telegram_bot.water_stuff.service.water_stuff.serviceModule.common.CommonServiceModule;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class EditGroupNameServiceModule implements ServiceModule<WaterStuffServiceRs, WaterStuffServiceRq> {

    private final CommonServiceModule commonServiceModule;
    private final WaterStuffServiceMessageToServiceMethodContainer waterStuffServiceMessageToHandlerContainer;

    public EditGroupNameServiceModule(
            CommonServiceModule commonServiceModule
    ) {
        this.commonServiceModule = commonServiceModule;
        this.waterStuffServiceMessageToHandlerContainer = new WaterStuffServiceMessageToServiceMethodContainer(
                Map.of(
                        commonServiceModule.getConstants().getButtons().getChangeName(), this::onChangeName,
                        commonServiceModule.getConstants().getRequests().getCancel(), this::cancel
                ),
                this::changeName
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

    private WaterStuffServiceRs onChangeName(WaterStuffServiceRq request) {
        commonServiceModule.getCacheComponent().getCacheBucket().getDataCache(WaterStuffServiceDataCache.class).getMenuContainer().add(WaterStuffServiceMenu.EDIT_NAME);
        return commonServiceModule.createSendTextResponse(commonServiceModule.getConstants().getPhrases().getEnterGroupName());
    }

    private WaterStuffServiceRs changeName(WaterStuffServiceRq request) {
        WaterStuffServiceDataCache dataCache = commonServiceModule.getCacheComponent().getCacheBucket().getDataCache(WaterStuffServiceDataCache.class);
        UsersWaterData usersWaterData = commonServiceModule.getUsersWaterData();
        WaterInfo waterInfo = usersWaterData.get(dataCache.getUserId(), dataCache.getSelectedGroup());
        String newGroupName = request.getMessageJson().getTextMessage();

        dataCache.setSelectedGroup(newGroupName);
        waterInfo.setName(newGroupName);

        usersWaterData.saveToFile();
        dataCache.getMessagesContainer().clearMessages();
        commonServiceModule.dropMenu();

        return commonServiceModule.createSendReplyKeyboardResponse(commonServiceModule.getConstants().getPhrases().getSuccess(), commonServiceModule.getEditMenuButtons());
    }

    private WaterStuffServiceRs cancel(WaterStuffServiceRq request) {
        return commonServiceModule.cancel(request);
    }
}
