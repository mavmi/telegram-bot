package mavmi.telegram_bot.water_stuff.service.water_stuff.serviceModule;

import mavmi.telegram_bot.common.service.dto.common.MessageJson;
import mavmi.telegram_bot.water_stuff.cache.WaterStuffServiceDataCache;
import mavmi.telegram_bot.water_stuff.service.water_stuff.container.WaterStuffServiceMessageToServiceMethodContainer;
import mavmi.telegram_bot.water_stuff.service.water_stuff.serviceModule.common.CommonServiceModule;
import mavmi.telegram_bot.water_stuff.service.water_stuff.serviceModule.edit.EditGroupDiffServiceModule;
import mavmi.telegram_bot.water_stuff.service.water_stuff.serviceModule.edit.EditGroupFertilizeServiceModule;
import mavmi.telegram_bot.water_stuff.service.water_stuff.serviceModule.edit.EditGroupNameServiceModule;
import mavmi.telegram_bot.water_stuff.service.water_stuff.serviceModule.edit.EditGroupWaterServiceModule;
import mavmi.telegram_bot.water_stuff.service.dto.waterStuffService.WaterStuffServiceRq;
import mavmi.telegram_bot.water_stuff.service.dto.waterStuffService.WaterStuffServiceRs;
import mavmi.telegram_bot.common.service.method.ServiceMethod;
import mavmi.telegram_bot.common.service.serviceModule.ServiceModule;
import mavmi.telegram_bot.water_stuff.constantsHandler.WaterStuffServiceConstantsHandler;
import mavmi.telegram_bot.water_stuff.constantsHandler.dto.WaterStuffServiceConstants;
import mavmi.telegram_bot.water_stuff.service.water_stuff.menu.WaterStuffServiceMenu;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class EditGroupServiceModule implements ServiceModule<WaterStuffServiceRs, WaterStuffServiceRq> {

    private final WaterStuffServiceConstants constants;
    private final CommonServiceModule commonServiceModule;
    private final WaterStuffServiceMessageToServiceMethodContainer waterStuffServiceMessageToHandlerContainer;

    public EditGroupServiceModule(
            EditGroupNameServiceModule editGroupNameServiceModule,
            EditGroupDiffServiceModule editGroupDiffServiceModule,
            EditGroupWaterServiceModule editGroupWaterServiceModule,
            EditGroupFertilizeServiceModule editGroupFertilizeServiceModule,
            CommonServiceModule commonServiceModule,
            WaterStuffServiceConstantsHandler constantsHandler
    ) {
        this.constants = constantsHandler.get();
        this.commonServiceModule = commonServiceModule;
        this.waterStuffServiceMessageToHandlerContainer = new WaterStuffServiceMessageToServiceMethodContainer(
                Map.of(
                    constants.getButtons().getEdit(), this::onEdit,
                    constants.getButtons().getChangeName(), editGroupNameServiceModule::handleRequest,
                    constants.getButtons().getChangeDiff(), editGroupDiffServiceModule::handleRequest,
                    constants.getButtons().getChangeWater(), editGroupWaterServiceModule::handleRequest,
                    constants.getButtons().getChangeFertilize(), editGroupFertilizeServiceModule::handleRequest,
                    constants.getButtons().getExit(), this::exit
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
        return commonServiceModule.createSendReplyKeyboardResponse(constants.getPhrases().getEditGroup(), commonServiceModule.getEditMenuButtons());
    }

    private WaterStuffServiceRs exit(WaterStuffServiceRq request) {
        commonServiceModule.dropMenu(WaterStuffServiceMenu.MANAGE_GROUP);
        commonServiceModule.getCacheComponent().getCacheBucket().getDataCache(WaterStuffServiceDataCache.class).getMessagesContainer().clearMessages();

        return commonServiceModule.createSendReplyKeyboardResponse(constants.getPhrases().getSuccess(), commonServiceModule.getManageMenuButtons());
    }
}
