package mavmi.telegram_bot.shakal.service.serviceComponents.serviceModule;

import com.github.blad3mak3r.memes4j.Memes4J;
import com.github.blad3mak3r.memes4j.PendingRequest;
import lombok.extern.slf4j.Slf4j;
import mavmi.telegram_bot.common.service.serviceComponents.container.ServiceComponentsContainer;
import mavmi.telegram_bot.common.service.serviceComponents.method.ServiceMethod;
import mavmi.telegram_bot.common.service.serviceComponents.serviceModule.ServiceModule;
import mavmi.telegram_bot.shakal.constantsHandler.ShakalConstantsHandler;
import mavmi.telegram_bot.shakal.service.dto.ShakalServiceRq;
import mavmi.telegram_bot.shakal.service.serviceComponents.serviceModule.common.CommonServiceModule;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
public class MainMenuServiceModule implements ServiceModule<ShakalServiceRq> {

    private final CommonServiceModule commonServiceModule;
    private final ServiceComponentsContainer<ShakalServiceRq> serviceComponentsContainer = new ServiceComponentsContainer<>();

    public MainMenuServiceModule(
            CommonServiceModule commonServiceModule,
            ApolocheseServiceModule apolocheseServiceModule,
            DiceServiceModule diceServiceModule,
            HoroscopeServiceModule horoscopeServiceModule,
            ShakalConstantsHandler constantsHandler
    ) {
        this.commonServiceModule = commonServiceModule;
        this.serviceComponentsContainer.add(commonServiceModule.getConstants().getRequests().getApolocheese(), apolocheseServiceModule::handleRequest)
                .add(commonServiceModule.getConstants().getRequests().getDice(), diceServiceModule::handleRequest)
                .add(commonServiceModule.getConstants().getRequests().getHoroscope(), horoscopeServiceModule::handleRequest)
                .add(commonServiceModule.getConstants().getRequests().getStart(), this::greetings)
                .add(commonServiceModule.getConstants().getRequests().getGoose(), this::goose)
                .add(commonServiceModule.getConstants().getRequests().getAnek(), this::anek)
                .add(commonServiceModule.getConstants().getRequests().getMeme(), this::meme)
                .setDefaultServiceMethod(this::error);
    }

    @Override
    public void handleRequest(ShakalServiceRq request) {
        String msg = request.getMessageJson().getTextMessage();
        ServiceMethod<ShakalServiceRq> method = serviceComponentsContainer.getMethod(msg);
        method.process(request);
    }

    private void greetings(ShakalServiceRq request) {
        commonServiceModule.sendText(request.getChatId(), commonServiceModule.getConstants().getPhrases().getCommon().getGreetings());
    }

    private void goose(ShakalServiceRq request) {
        commonServiceModule.sendText(request.getChatId(), generateGoose());
    }

    private void anek(ShakalServiceRq request) {
        commonServiceModule.sendText(request.getChatId(), generateAnek());
    }

    private void meme(ShakalServiceRq request) {
        PendingRequest memeRequest = Memes4J.getRandomMeme();

        try {
            commonServiceModule.sendText(request.getChatId(), memeRequest.complete().getImage());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            commonServiceModule.sendText(request.getChatId(), commonServiceModule.getConstants().getPhrases().getCommon().getError());
        }
    }

    private void error(ShakalServiceRq request) {
        commonServiceModule.sendText(request.getChatId(), commonServiceModule.getConstants().getPhrases().getCommon().getInvalidInput());
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
