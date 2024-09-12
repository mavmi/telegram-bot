package mavmi.telegram_bot.shakal.service.serviceComponents.serviceModule;

import mavmi.telegram_bot.common.service.serviceComponents.container.ServiceComponentsContainer;
import mavmi.telegram_bot.common.service.serviceComponents.method.ServiceMethod;
import mavmi.telegram_bot.common.service.serviceComponents.serviceModule.ServiceModule;
import mavmi.telegram_bot.shakal.cache.ShakalDataCache;
import mavmi.telegram_bot.shakal.service.dto.ShakalServiceRq;
import mavmi.telegram_bot.shakal.service.menu.ShakalServiceMenu;
import mavmi.telegram_bot.shakal.service.serviceComponents.serviceModule.common.CommonServiceModule;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;

@Component
public class ApolocheseServiceModule implements ServiceModule<ShakalServiceRq> {

    private final CommonServiceModule commonServiceModule;
    private final ServiceComponentsContainer<ShakalServiceRq> serviceRqServiceComponentsContainer = new ServiceComponentsContainer<>();

    public ApolocheseServiceModule(CommonServiceModule commonServiceModule) {
        this.commonServiceModule = commonServiceModule;
        this.serviceRqServiceComponentsContainer.add(commonServiceModule.getConstants().getRequests().getApolocheese(), this::askForName)
                .setDefaultServiceMethod(this::process);
    }

    @Override
    public void handleRequest(ShakalServiceRq request) {
        ServiceMethod<ShakalServiceRq> method = serviceRqServiceComponentsContainer.getMethod(request.getMessageJson().getTextMessage());
        method.process(request);
    }

    private void askForName(ShakalServiceRq request) {
        commonServiceModule.getCacheComponent().getCacheBucket().getDataCache(ShakalDataCache.class).getMenuContainer().add(ShakalServiceMenu.APOLOCHEESE);
        commonServiceModule.sendText(
                request.getChatId(),
                commonServiceModule.getConstants().getPhrases().getCommon().getApolocheese()
        );
    }

    private void process(ShakalServiceRq request) {
        String msg = request.getMessageJson().getTextMessage();
        commonServiceModule.getCacheComponent().getCacheBucket().getDataCache(ShakalDataCache.class).getMenuContainer().removeLast();
        commonServiceModule.sendText(
                request.getChatId(),
                generateApolocheese(msg)
        );
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
