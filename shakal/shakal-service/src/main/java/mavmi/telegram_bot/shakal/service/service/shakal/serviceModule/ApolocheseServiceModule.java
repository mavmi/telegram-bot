package mavmi.telegram_bot.shakal.service.service.shakal.serviceModule;

import mavmi.telegram_bot.common.dto.dto.impl.shakal.service.ShakalServiceRq;
import mavmi.telegram_bot.common.dto.dto.impl.shakal.service.ShakalServiceRs;
import mavmi.telegram_bot.common.service.method.ServiceMethod;
import mavmi.telegram_bot.common.service.serviceModule.ServiceModule;
import mavmi.telegram_bot.shakal.service.constantsHandler.ShakalServiceConstantsHandler;
import mavmi.telegram_bot.shakal.service.constantsHandler.dto.ShakalServiceConstants;
import mavmi.telegram_bot.shakal.service.service.shakal.container.ShakalServiceMessageToHandlerContainer;
import mavmi.telegram_bot.shakal.service.service.shakal.menu.ShakalServiceMenu;
import mavmi.telegram_bot.shakal.service.service.shakal.serviceModule.common.CommonServiceModule;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;
import java.util.Map;

@Component
public class ApolocheseServiceModule implements ServiceModule<ShakalServiceRs, ShakalServiceRq> {

    private final ShakalServiceConstants constants;
    private final CommonServiceModule commonServiceModule;
    private final ShakalServiceMessageToHandlerContainer shakalServiceMessageToHandlerContainer;

    public ApolocheseServiceModule(
        CommonServiceModule commonServiceModule,
        ShakalServiceConstantsHandler constantsHandler
    ) {
        this.constants = constantsHandler.get();
        this.commonServiceModule = commonServiceModule;
        this.shakalServiceMessageToHandlerContainer = new ShakalServiceMessageToHandlerContainer(
                Map.of(constants.getRequests().getApolocheese(), this::askForName),
                this::handleRequest
        );
    }

    @Override
    public ShakalServiceRs process(ShakalServiceRq request) {
        ServiceMethod<ShakalServiceRs, ShakalServiceRq> method = shakalServiceMessageToHandlerContainer.getMethod(request.getMessageJson().getTextMessage());
        return method.process(request);
    }

    private ShakalServiceRs askForName(ShakalServiceRq request) {
        commonServiceModule.getUserSession().getCache().getMenuContainer().add(ShakalServiceMenu.APOLOCHEESE);
        return commonServiceModule.createSendTextResponse(constants.getPhrases().getCommon().getApolocheese());
    }

    private ShakalServiceRs handleRequest(ShakalServiceRq request) {
        String msg = request.getMessageJson().getTextMessage();

        commonServiceModule.getUserSession().getCache().getMenuContainer().removeLast();
        return commonServiceModule.createSendTextResponse(generateApolocheese(msg));
    }

    private String generateApolocheese(String username) {
        final StringBuilder builder = new StringBuilder();

        builder.append("```\n")
                .append("java -jar \"/home/mavmi/apolocheese/apolocheese.jar\"")
                .append("\n\n")
                .append(new SimpleDateFormat("dd.MM.yyyy HH:mm:").format(GregorianCalendar.getInstance().getTime()))
                .append("```")
                .append("\n")
                .append("\"Я прошу прощения, ")
                .append(username)
                .append("! Солнышко! Я дико извиняюсь! Сможешь ли ты меня простить?.....\"")
                .append("\n")
                .append("\n")
                .append("```\n")
                .append("@https://github.com/mavmi\n")
                .append("@All rights reserved!\n")
                .append("@Do not distribute!\n")
                .append("```");

        return builder.toString();
    }
}
