package mavmi.telegram_bot.shakal.service.serviceComponents.serviceModule;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mavmi.telegram_bot.lib.service_api.serviceComponents.container.ServiceComponentsContainer;
import mavmi.telegram_bot.lib.service_api.serviceComponents.method.ServiceMethod;
import mavmi.telegram_bot.lib.service_api.serviceComponents.serviceModule.ServiceModule;
import mavmi.telegram_bot.shakal.cache.ShakalDataCache;
import mavmi.telegram_bot.shakal.constantsHandler.dto.ShakalConstants;
import mavmi.telegram_bot.shakal.service.dto.ShakalServiceRq;
import mavmi.telegram_bot.shakal.service.menu.ShakalServiceMenu;
import mavmi.telegram_bot.shakal.service.serviceComponents.serviceModule.common.CommonServiceModule;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class HoroscopeServiceModule implements ServiceModule<ShakalServiceRq> {

    private final CommonServiceModule commonServiceModule;
    private final ServiceComponentsContainer<ShakalServiceRq> serviceComponentsContainer = new ServiceComponentsContainer<>();

    @PostConstruct
    public void setup() {
        this.serviceComponentsContainer.add(commonServiceModule.getConstants().getRequests().getHoroscope(), this::askForSign)
                .setDefaultServiceMethod(this::process);
    }

    @Override
    public void handleRequest(ShakalServiceRq request) {
        String msg = request.getMessageJson().getTextMessage();
        ServiceMethod<ShakalServiceRq> method = serviceComponentsContainer.getMethod(msg);
        method.process(request);
    }

    private void askForSign(ShakalServiceRq request) {
        commonServiceModule.getUserCaches().getDataCache(ShakalDataCache.class).setMenu(ShakalServiceMenu.HOROSCOPE);
        commonServiceModule.sendReplyKeyboard(request.getChatId(), commonServiceModule.getConstants().getPhrases().getHoroscope().getQuestion(), generateHoroscopeArray());
    }

    private void process(ShakalServiceRq request) {
        ShakalConstants constants = commonServiceModule.getConstants();
        String msg = request.getMessageJson().getTextMessage();
        String sign = constants.getPhrases().getHoroscope().getSigns().get(msg);
        if (sign == null) {
            commonServiceModule.sendReplyKeyboard(request.getChatId(), constants.getPhrases().getHoroscope().getError(), generateHoroscopeArray());
        } else {
            commonServiceModule.dropUserCaches();
            commonServiceModule.sendTextDeleteKeyboard(request.getChatId(), generateHoroscope(sign));
        }
    }

    private String[] generateHoroscopeArray() {
        int i = 0;
        String[] arr = new String[12];

        for (Map.Entry<String, String> entry : commonServiceModule.getConstants().getPhrases().getHoroscope().getSigns().entrySet()) {
            arr[i++] = entry.getKey();
        }

        return arr;
    }

    private String generateHoroscope(String sign) {
        try {
            Document document = Jsoup
                    .connect("https://horo.mail.ru/prediction/" + sign + "/today/")
                    .get();
            StringBuilder builder = new StringBuilder();

            int i = 0;
            for (Element element : document.getElementsByTag("p")) {
                if (i++ == 2) {
                    break;
                }
                if (!builder.isEmpty()) {
                    builder.append("\n").append("\n");
                }
                builder.append(element.text());
            }

            return builder.toString();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            return commonServiceModule.getConstants().getPhrases().getCommon().getError();
        }
    }
}
