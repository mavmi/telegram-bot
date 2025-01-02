package mavmi.telegram_bot.water_stuff.service.waterStuff.serviceModule;

import mavmi.telegram_bot.common.service.serviceComponents.container.ServiceComponentsContainer;
import mavmi.telegram_bot.common.service.dto.common.MessageJson;
import mavmi.telegram_bot.common.service.serviceComponents.method.ServiceMethod;
import mavmi.telegram_bot.common.service.serviceComponents.serviceModule.ServiceModule;
import mavmi.telegram_bot.water_stuff.cache.WaterDataCache;
import mavmi.telegram_bot.water_stuff.service.dto.waterStuffService.WaterStuffServiceRq;
import mavmi.telegram_bot.water_stuff.service.waterStuff.menu.WaterStuffServiceMenu;
import mavmi.telegram_bot.water_stuff.service.waterStuff.serviceModule.common.CommonServiceModule;
import mavmi.telegram_bot.water_stuff.service.waterStuff.serviceModule.edit.EditGroupDiffServiceModule;
import mavmi.telegram_bot.water_stuff.service.waterStuff.serviceModule.edit.EditGroupFertilizeServiceModule;
import mavmi.telegram_bot.water_stuff.service.waterStuff.serviceModule.edit.EditGroupNameServiceModule;
import mavmi.telegram_bot.water_stuff.service.waterStuff.serviceModule.edit.EditGroupWaterServiceModule;
import org.springframework.stereotype.Component;

@Component
public class EditGroupServiceModule implements ServiceModule<WaterStuffServiceRq> {

    private final CommonServiceModule commonServiceModule;
    private final ServiceComponentsContainer<WaterStuffServiceRq> serviceComponentsContainer = new ServiceComponentsContainer<>();

    public EditGroupServiceModule(
            EditGroupNameServiceModule editGroupNameServiceModule,
            EditGroupDiffServiceModule editGroupDiffServiceModule,
            EditGroupWaterServiceModule editGroupWaterServiceModule,
            EditGroupFertilizeServiceModule editGroupFertilizeServiceModule,
            CommonServiceModule commonServiceModule
    ) {
        this.commonServiceModule = commonServiceModule;
        serviceComponentsContainer.add(commonServiceModule.getConstants().getButtons().getEdit(), this::onEdit)
                .add(commonServiceModule.getConstants().getButtons().getChangeName(), editGroupNameServiceModule::handleRequest)
                .add(commonServiceModule.getConstants().getButtons().getChangeDiff(), editGroupDiffServiceModule::handleRequest)
                .add(commonServiceModule.getConstants().getButtons().getChangeWater(), editGroupWaterServiceModule::handleRequest)
                .add(commonServiceModule.getConstants().getButtons().getChangeFertilize(), editGroupFertilizeServiceModule::handleRequest)
                .add(commonServiceModule.getConstants().getButtons().getExit(), this::exit)
                .setDefaultServiceMethod(commonServiceModule::error);
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

    private void onEdit(WaterStuffServiceRq request) {
        commonServiceModule.getCacheComponent().getCacheBucket().getDataCache(WaterDataCache.class).getMenuContainer().add(WaterStuffServiceMenu.EDIT);
        commonServiceModule.sendReplyKeyboard(request.getChatId(), commonServiceModule.getConstants().getPhrases().getEditGroup(), commonServiceModule.getEditMenuButtons());
    }

    private void exit(WaterStuffServiceRq request) {
        commonServiceModule.dropUserMenu(WaterStuffServiceMenu.MANAGE_GROUP);
        commonServiceModule.getCacheComponent().getCacheBucket().getDataCache(WaterDataCache.class).getMessagesContainer().clearMessages();
        commonServiceModule.sendReplyKeyboard(request.getChatId(), commonServiceModule.getConstants().getPhrases().getSuccess(), commonServiceModule.getManageMenuButtons());
    }
}
