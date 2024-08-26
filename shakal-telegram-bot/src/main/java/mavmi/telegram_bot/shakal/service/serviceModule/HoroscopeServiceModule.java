package mavmi.telegram_bot.shakal.service.serviceModule;

import lombok.extern.slf4j.Slf4j;
import mavmi.telegram_bot.common.service.method.direct.ServiceMethod;
import mavmi.telegram_bot.common.service.serviceModule.direct.ServiceModule;
import mavmi.telegram_bot.shakal.cache.ShakalServiceDataCache;
import mavmi.telegram_bot.shakal.constantsHandler.dto.ShakalServiceConstants;
import mavmi.telegram_bot.shakal.service.container.ShakalServiceMessageToServiceMethodContainer;
import mavmi.telegram_bot.shakal.service.dto.ShakalServiceRq;
import mavmi.telegram_bot.shakal.service.dto.ShakalServiceRs;
import mavmi.telegram_bot.shakal.service.menu.ShakalServiceMenu;
import mavmi.telegram_bot.shakal.service.serviceModule.common.CommonServiceModule;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

@Slf4j
@Component
public class HoroscopeServiceModule implements ServiceModule<ShakalServiceRs, ShakalServiceRq> {

    private final CommonServiceModule commonServiceModule;
    private final ShakalServiceMessageToServiceMethodContainer shakalServiceMessageToHandlerContainer;

    public HoroscopeServiceModule(
        CommonServiceModule commonServiceModule
    ) {
        this.commonServiceModule = commonServiceModule;
        this.shakalServiceMessageToHandlerContainer = new ShakalServiceMessageToServiceMethodContainer(
                Map.of(commonServiceModule.getConstants().getRequests().getHoroscope(), this::askForSign),
                this::process
        );
    }

    @Override
    public ShakalServiceRs handleRequest(ShakalServiceRq request) {
        String msg = request.getMessageJson().getTextMessage();
        ServiceMethod<ShakalServiceRs, ShakalServiceRq> method = shakalServiceMessageToHandlerContainer.getMethod(msg);
        return method.process(request);
    }

    private ShakalServiceRs askForSign(ShakalServiceRq request) {
        commonServiceModule.getCacheComponent().getCacheBucket().getDataCache(ShakalServiceDataCache.class).getMenuContainer().add(ShakalServiceMenu.HOROSCOPE);
        return commonServiceModule.createSendReplyKeyboardResponse(commonServiceModule.getConstants().getPhrases().getHoroscope().getQuestion(), generateHoroscopeArray());
    }

    private ShakalServiceRs process(ShakalServiceRq request) {
        ShakalServiceConstants constants = commonServiceModule.getConstants();
        String msg = request.getMessageJson().getTextMessage();
        String sign = constants.getPhrases().getHoroscope().getSigns().get(msg);
        if (sign == null) {
            return commonServiceModule.createSendReplyKeyboardResponse(constants.getPhrases().getHoroscope().getError(), generateHoroscopeArray());
        } else {
            commonServiceModule.getCacheComponent().getCacheBucket().getDataCache(ShakalServiceDataCache.class).getMenuContainer().removeLast();
            return commonServiceModule.createSendTextResponse(generateHoroscope(sign));
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
