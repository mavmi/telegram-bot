package mavmi.telegram_bot.water_stuff.service.water_stuff.serviceModule;

import mavmi.telegram_bot.common.service.dto.common.MessageJson;
import mavmi.telegram_bot.common.service.method.direct.ServiceMethod;
import mavmi.telegram_bot.common.service.serviceModule.direct.ServiceModule;
import mavmi.telegram_bot.water_stuff.cache.WaterStuffServiceDataCache;
import mavmi.telegram_bot.water_stuff.service.dto.waterStuffService.WaterStuffServiceRq;
import mavmi.telegram_bot.water_stuff.service.dto.waterStuffService.WaterStuffServiceRs;
import mavmi.telegram_bot.water_stuff.service.water_stuff.container.WaterStuffServiceMessageToServiceMethodContainer;
import mavmi.telegram_bot.water_stuff.service.water_stuff.menu.WaterStuffServiceMenu;
import mavmi.telegram_bot.water_stuff.service.water_stuff.serviceModule.common.CommonServiceModule;
import mavmi.telegram_bot.water_stuff.service.water_stuff.serviceModule.edit.EditGroupDiffServiceModule;
import mavmi.telegram_bot.water_stuff.service.water_stuff.serviceModule.edit.EditGroupFertilizeServiceModule;
import mavmi.telegram_bot.water_stuff.service.water_stuff.serviceModule.edit.EditGroupNameServiceModule;
import mavmi.telegram_bot.water_stuff.service.water_stuff.serviceModule.edit.EditGroupWaterServiceModule;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class EditGroupServiceModule implements ServiceModule<WaterStuffServiceRs, WaterStuffServiceRq> {

    private final CommonServiceModule commonServiceModule;
    private final WaterStuffServiceMessageToServiceMethodContainer waterStuffServiceMessageToHandlerContainer;

    public EditGroupServiceModule(
            EditGroupNameServiceModule editGroupNameServiceModule,
            EditGroupDiffServiceModule editGroupDiffServiceModule,
            EditGroupWaterServiceModule editGroupWaterServiceModule,
            EditGroupFertilizeServiceModule editGroupFertilizeServiceModule,
            CommonServiceModule commonServiceModule
    ) {
        this.commonServiceModule = commonServiceModule;
        this.waterStuffServiceMessageToHandlerContainer = new WaterStuffServiceMessageToServiceMethodContainer(
                Map.of(
                    commonServiceModule.getConstants().getButtons().getEdit(), this::onEdit,
                    commonServiceModule.getConstants().getButtons().getChangeName(), editGroupNameServiceModule::handleRequest,
                    commonServiceModule.getConstants().getButtons().getChangeDiff(), editGroupDiffServiceModule::handleRequest,
                    commonServiceModule.getConstants().getButtons().getChangeWater(), editGroupWaterServiceModule::handleRequest,
                    commonServiceModule.getConstants().getButtons().getChangeFertilize(), editGroupFertilizeServiceModule::handleRequest,
                    commonServiceModule.getConstants().getButtons().getExit(), this::exit
                ),
                commonServiceModule::error
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

    private WaterStuffServiceRs onEdit(WaterStuffServiceRq request) {
        commonServiceModule.getCacheComponent().getCacheBucket().getDataCache(WaterStuffServiceDataCache.class).getMenuContainer().add(WaterStuffServiceMenu.EDIT);
        return commonServiceModule.createSendReplyKeyboardResponse(commonServiceModule.getConstants().getPhrases().getEditGroup(), commonServiceModule.getEditMenuButtons());
    }

    private WaterStuffServiceRs exit(WaterStuffServiceRq request) {
        commonServiceModule.dropMenu(WaterStuffServiceMenu.MANAGE_GROUP);
        commonServiceModule.getCacheComponent().getCacheBucket().getDataCache(WaterStuffServiceDataCache.class).getMessagesContainer().clearMessages();

        return commonServiceModule.createSendReplyKeyboardResponse(commonServiceModule.getConstants().getPhrases().getSuccess(), commonServiceModule.getManageMenuButtons());
    }
}
