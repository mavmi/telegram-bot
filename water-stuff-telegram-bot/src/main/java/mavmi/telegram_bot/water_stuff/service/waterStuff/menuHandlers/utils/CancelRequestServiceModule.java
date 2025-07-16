package mavmi.telegram_bot.water_stuff.service.waterStuff.menuHandlers.utils;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import mavmi.telegram_bot.lib.dto.service.common.MessageJson;
import mavmi.telegram_bot.lib.service_api.service.serviceComponents.container.ServiceComponentsContainer;
import mavmi.telegram_bot.lib.service_api.service.serviceComponents.method.ServiceMethod;
import mavmi.telegram_bot.lib.service_api.service.serviceComponents.serviceModule.ServiceModule;
import mavmi.telegram_bot.water_stuff.service.waterStuff.dto.WaterStuffServiceRq;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CancelRequestServiceModule implements ServiceModule<WaterStuffServiceRq> {

    private final CommonUtils commonUtils;
    private final ServiceComponentsContainer<WaterStuffServiceRq> serviceComponentsContainer = new ServiceComponentsContainer<>();

    @PostConstruct
    public void setup() {
        this.serviceComponentsContainer.add(commonUtils.getConstants().getRequests().getCancel(), this::processCancel);
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

    private void processCancel(WaterStuffServiceRq request) {
        commonUtils.cancel(request);
    }
}
