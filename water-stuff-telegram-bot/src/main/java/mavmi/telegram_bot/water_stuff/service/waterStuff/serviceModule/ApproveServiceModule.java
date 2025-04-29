package mavmi.telegram_bot.water_stuff.service.waterStuff.serviceModule;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import mavmi.telegram_bot.lib.dto.service.common.MessageJson;
import mavmi.telegram_bot.lib.service_api.serviceComponents.container.ServiceComponentsContainer;
import mavmi.telegram_bot.lib.service_api.serviceComponents.method.ServiceMethod;
import mavmi.telegram_bot.lib.service_api.serviceComponents.serviceModule.ServiceModule;
import mavmi.telegram_bot.water_stuff.constantsHandler.dto.WaterConstants;
import mavmi.telegram_bot.water_stuff.service.dto.waterStuffService.WaterStuffServiceRq;
import mavmi.telegram_bot.water_stuff.service.waterStuff.serviceModule.common.CommonServiceModule;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ApproveServiceModule implements ServiceModule<WaterStuffServiceRq> {

    private final CommonServiceModule commonServiceModule;
    private final ServiceComponentsContainer<WaterStuffServiceRq> serviceComponentsContainer = new ServiceComponentsContainer<>();

    @PostConstruct
    public void setup() {
        this.serviceComponentsContainer.setDefaultServiceMethod(this::approve);
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

    private void approve(WaterStuffServiceRq request) {
        WaterConstants constants = commonServiceModule.getConstants();
        commonServiceModule.sendReplyKeyboard(
                request.getChatId(),
                constants.getPhrases().getCommon().getApprove(),
                new String[]{
                        constants.getButtons().getCommon().getYes(),
                        constants.getButtons().getCommon().getNo()
                }
        );
    }
}
