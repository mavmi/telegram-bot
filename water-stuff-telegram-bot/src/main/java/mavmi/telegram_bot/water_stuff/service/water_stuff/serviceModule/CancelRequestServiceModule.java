package mavmi.telegram_bot.water_stuff.service.water_stuff.serviceModule;

import mavmi.telegram_bot.common.service.dto.common.MessageJson;
import mavmi.telegram_bot.common.service.method.direct.ServiceMethod;
import mavmi.telegram_bot.common.service.serviceModule.direct.ServiceModule;
import mavmi.telegram_bot.water_stuff.service.dto.waterStuffService.WaterStuffServiceRq;
import mavmi.telegram_bot.water_stuff.service.dto.waterStuffService.WaterStuffServiceRs;
import mavmi.telegram_bot.water_stuff.service.water_stuff.container.WaterStuffServiceMessageToServiceMethodContainer;
import mavmi.telegram_bot.water_stuff.service.water_stuff.serviceModule.common.CommonServiceModule;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class CancelRequestServiceModule implements ServiceModule<WaterStuffServiceRs, WaterStuffServiceRq> {

    private final CommonServiceModule commonServiceModule;
    private final WaterStuffServiceMessageToServiceMethodContainer waterStuffServiceMessageToHandlerContainer;

    public CancelRequestServiceModule(
            CommonServiceModule commonServiceModule
    ) {
        this.commonServiceModule = commonServiceModule;
        this.waterStuffServiceMessageToHandlerContainer = new WaterStuffServiceMessageToServiceMethodContainer(
                Map.of(commonServiceModule.getConstants().getRequests().getCancel(), this::processCancel)
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

    private WaterStuffServiceRs processCancel(WaterStuffServiceRq request) {
        return commonServiceModule.cancel(request);
    }
}
