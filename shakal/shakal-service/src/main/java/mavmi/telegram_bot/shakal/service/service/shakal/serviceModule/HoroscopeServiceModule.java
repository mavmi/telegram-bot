package mavmi.telegram_bot.shakal.service.service.shakal.serviceModule;

import lombok.RequiredArgsConstructor;
import mavmi.telegram_bot.common.dto.dto.impl.shakal.service.ShakalServiceRq;
import mavmi.telegram_bot.common.dto.dto.impl.shakal.service.ShakalServiceRs;
import mavmi.telegram_bot.common.service.method.ServiceMethod;
import mavmi.telegram_bot.common.service.serviceModule.ServiceModule;
import mavmi.telegram_bot.shakal.service.constants.Phrases;
import mavmi.telegram_bot.shakal.service.constants.Requests;
import mavmi.telegram_bot.shakal.service.service.shakal.container.ShakalServiceMessageToHandlerContainer;
import mavmi.telegram_bot.shakal.service.service.shakal.menu.ShakalServiceMenu;
import mavmi.telegram_bot.shakal.service.service.shakal.serviceModule.common.CommonServiceModule;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class HoroscopeServiceModule implements ServiceModule<ShakalServiceRs, ShakalServiceRq> {

    private final CommonServiceModule commonServiceModule;
    private final ShakalServiceMessageToHandlerContainer shakalServiceMessageToHandlerContainer = new ShakalServiceMessageToHandlerContainer(
            Map.of(Requests.HOROSCOPE_REQ, this::askForSign),
            this::handleRequest
    );

    @Override
    public ShakalServiceRs process(ShakalServiceRq request) {
        String msg = request.getMessageJson().getTextMessage();
        ServiceMethod<ShakalServiceRs, ShakalServiceRq> method = shakalServiceMessageToHandlerContainer.getMethod(msg);
        return method.process(request);
    }

    private ShakalServiceRs askForSign(ShakalServiceRq request) {
        commonServiceModule.getUserSession().getCache().getMenuContainer().add(ShakalServiceMenu.HOROSCOPE);
        return commonServiceModule.createSendKeyboardResponse(Phrases.HOROSCOPE_QUES_MSG, generateHoroscopeArray());
    }

    private ShakalServiceRs handleRequest(ShakalServiceRq request) {
        String msg = request.getMessageJson().getTextMessage();
        String sign = Phrases.HOROSCOPE_SIGNS.get(msg);
        if (sign == null) {
            return commonServiceModule.createSendKeyboardResponse(Phrases.HOROSCOPE_ERROR_MSG, generateHoroscopeArray());
        } else {
            commonServiceModule.getUserSession().getCache().getMenuContainer().removeLast();
            return commonServiceModule.createSendTextResponse(generateHoroscope(sign));
        }
    }

    private String[] generateHoroscopeArray() {
        int i = 0;
        String[] arr = new String[12];

        for (Map.Entry<String, String> entry : Phrases.HOROSCOPE_SIGNS.entrySet()) {
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
            e.printStackTrace(System.out);
            return Phrases.EXCEPTION_MSG;
        }
    }
}
