package mavmi.telegram_bot.shakal.service.serviceModule;

import com.github.blad3mak3r.memes4j.Memes4J;
import com.github.blad3mak3r.memes4j.PendingRequest;
import lombok.extern.slf4j.Slf4j;
import mavmi.telegram_bot.common.service.method.direct.ServiceMethod;
import mavmi.telegram_bot.common.service.serviceModule.direct.ServiceModule;
import mavmi.telegram_bot.shakal.constantsHandler.ShakalServiceConstantsHandler;
import mavmi.telegram_bot.shakal.service.container.ShakalServiceMessageToServiceMethodContainer;
import mavmi.telegram_bot.shakal.service.dto.ShakalServiceRq;
import mavmi.telegram_bot.shakal.service.dto.ShakalServiceRs;
import mavmi.telegram_bot.shakal.service.serviceModule.common.CommonServiceModule;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

@Slf4j
@Component
public class MainMenuServiceModule implements ServiceModule<ShakalServiceRs, ShakalServiceRq> {

    private final CommonServiceModule commonServiceModule;
    private final ShakalServiceMessageToServiceMethodContainer shakalServiceMessageToHandlerContainer;

    public MainMenuServiceModule(
            CommonServiceModule commonServiceModule,
            ApolocheseServiceModule apolocheseServiceModule,
            DiceServiceModule diceServiceModule,
            HoroscopeServiceModule horoscopeServiceModule,
            ShakalServiceConstantsHandler constantsHandler
    ) {
        this.commonServiceModule = commonServiceModule;
        this.shakalServiceMessageToHandlerContainer = new ShakalServiceMessageToServiceMethodContainer(
                Map.of(
                        commonServiceModule.getConstants().getRequests().getApolocheese(), apolocheseServiceModule::handleRequest,
                        commonServiceModule.getConstants().getRequests().getDice(), diceServiceModule::handleRequest,
                        commonServiceModule.getConstants().getRequests().getHoroscope(), horoscopeServiceModule::handleRequest,
                        commonServiceModule.getConstants().getRequests().getStart(), this::greetings,
                        commonServiceModule.getConstants().getRequests().getGoose(), this::goose,
                        commonServiceModule.getConstants().getRequests().getAnek(), this::anek,
                        commonServiceModule.getConstants().getRequests().getMeme(), this::meme
                ),
                this::error
        );
    }

    @Override
    public ShakalServiceRs handleRequest(ShakalServiceRq request) {
        String msg = request.getMessageJson().getTextMessage();
        ServiceMethod<ShakalServiceRs, ShakalServiceRq> method = shakalServiceMessageToHandlerContainer.getMethod(msg);
        return method.process(request);
    }

    private ShakalServiceRs greetings(ShakalServiceRq request) {
        return commonServiceModule.createSendTextResponse(commonServiceModule.getConstants().getPhrases().getCommon().getGreetings());
    }

    private ShakalServiceRs goose(ShakalServiceRq request) {
        return commonServiceModule.createSendTextResponse(generateGoose());
    }

    private ShakalServiceRs anek(ShakalServiceRq request) {
        return commonServiceModule.createSendTextResponse(generateAnek());
    }

    private ShakalServiceRs meme(ShakalServiceRq request) {
        PendingRequest memeRequest = Memes4J.getRandomMeme();

        try {
            return commonServiceModule.createSendTextResponse(memeRequest.complete().getImage());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return commonServiceModule.createSendTextResponse(commonServiceModule.getConstants().getPhrases().getCommon().getError());
        }
    }

    private ShakalServiceRs error(ShakalServiceRq request) {
        return commonServiceModule.createSendTextResponse(commonServiceModule.getConstants().getPhrases().getCommon().getInvalidInput());
    }

    private String generateGoose() {
        return commonServiceModule.getConstants().getGoose().getRandomGoose();
    }

    private String generateAnek() {
        try {
            Document document = Jsoup.connect("https://www.anekdot.ru/random/anekdot/").get();
            for (Element element : document.getElementsByTag("div")) {
                if (element.className().equals("text")) {
                    return element.text();
                }
            }
            throw new IOException();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            return commonServiceModule.getConstants().getPhrases().getCommon().getInvalidInput();
        }
    }
}
