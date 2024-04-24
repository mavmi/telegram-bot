package mavmi.telegram_bot.water_stuff.service.service.water_stuff.serviceModule;

import mavmi.telegram_bot.common.dto.common.MessageJson;
import mavmi.telegram_bot.common.dto.dto.impl.water_stuff.water_stuff_service.WaterStuffServiceRq;
import mavmi.telegram_bot.common.dto.dto.impl.water_stuff.water_stuff_service.WaterStuffServiceRs;
import mavmi.telegram_bot.common.service.method.ServiceMethod;
import mavmi.telegram_bot.common.service.serviceModule.ServiceModule;
import mavmi.telegram_bot.water_stuff.service.constantsHandler.WaterStuffServiceConstantsHandler;
import mavmi.telegram_bot.water_stuff.service.constantsHandler.dto.WaterStuffServiceConstants;
import mavmi.telegram_bot.water_stuff.service.service.water_stuff.container.WaterStuffServiceMessageToHandlerContainer;
import mavmi.telegram_bot.water_stuff.service.service.water_stuff.menu.WaterStuffServiceMenu;
import mavmi.telegram_bot.water_stuff.service.service.water_stuff.serviceModule.common.CommonServiceModule;
import mavmi.telegram_bot.water_stuff.service.service.water_stuff.serviceModule.edit.EditGroupDiffServiceModule;
import mavmi.telegram_bot.water_stuff.service.service.water_stuff.serviceModule.edit.EditGroupFertilizeServiceModule;
import mavmi.telegram_bot.water_stuff.service.service.water_stuff.serviceModule.edit.EditGroupNameServiceModule;
import mavmi.telegram_bot.water_stuff.service.service.water_stuff.serviceModule.edit.EditGroupWaterServiceModule;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class EditGroupServiceModule implements ServiceModule<WaterStuffServiceRs, WaterStuffServiceRq> {

    private final WaterStuffServiceConstants constants;
    private final CommonServiceModule commonServiceModule;
    private final WaterStuffServiceMessageToHandlerContainer waterStuffServiceMessageToHandlerContainer;

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
        this.waterStuffServiceMessageToHandlerContainer = new WaterStuffServiceMessageToHandlerContainer(
                Map.of(
                    constants.getButtons().getEdit(), this::handleRequest,
                    constants.getButtons().getChangeName(), editGroupNameServiceModule::process,
                    constants.getButtons().getChangeDiff(), editGroupDiffServiceModule::process,
                    constants.getButtons().getChangeWater(), editGroupWaterServiceModule::process,
                    constants.getButtons().getChangeFertilize(), editGroupFertilizeServiceModule::process,
                    constants.getButtons().getExit(), this::exit
                ),
                commonServiceModule::error
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
        commonServiceModule.getUserSession().getCache().getMenuContainer().add(WaterStuffServiceMenu.EDIT);
        return commonServiceModule.createSendReplyKeyboardResponse(constants.getPhrases().getEditGroup(), commonServiceModule.getEditMenuButtons());
    }

    private WaterStuffServiceRs exit(WaterStuffServiceRq request) {
        commonServiceModule.dropMenu(WaterStuffServiceMenu.MANAGE_GROUP);
        commonServiceModule.getUserSession().getCache().getMessagesContainer().clearMessages();

        return commonServiceModule.createSendReplyKeyboardResponse(constants.getPhrases().getSuccess(), commonServiceModule.getManageMenuButtons());
    }
}
