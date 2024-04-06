package mavmi.telegram_bot.shakal.service.service.shakal.serviceModule;

import com.github.blad3mak3r.memes4j.Memes4J;
import com.github.blad3mak3r.memes4j.PendingRequest;
import mavmi.telegram_bot.common.dto.dto.impl.shakal.service.ShakalServiceRq;
import mavmi.telegram_bot.common.dto.dto.impl.shakal.service.ShakalServiceRs;
import mavmi.telegram_bot.common.service.method.ServiceMethod;
import mavmi.telegram_bot.common.service.serviceModule.ServiceModule;
import mavmi.telegram_bot.shakal.service.constants.Goose;
import mavmi.telegram_bot.shakal.service.constants.Phrases;
import mavmi.telegram_bot.shakal.service.constants.Requests;
import mavmi.telegram_bot.shakal.service.service.shakal.container.ShakalServiceMessageToHandlerContainer;
import mavmi.telegram_bot.shakal.service.service.shakal.serviceModule.common.CommonServiceModule;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

@Component
public class MainMenuServiceModule implements ServiceModule<ShakalServiceRs, ShakalServiceRq> {

    private final CommonServiceModule commonServiceModule;
    private final ShakalServiceMessageToHandlerContainer shakalServiceMessageToHandlerContainer;

    public MainMenuServiceModule(
            CommonServiceModule commonServiceModule,
            ApolocheseServiceModule apolocheseServiceModule,
            DiceServiceModule diceServiceModule,
            HoroscopeServiceModule horoscopeServiceModule
    ) {
        this.commonServiceModule = commonServiceModule;
        this.shakalServiceMessageToHandlerContainer = new ShakalServiceMessageToHandlerContainer(
                Map.of(
                        Requests.APOLOCHEESE_REQ, apolocheseServiceModule::process,
                        Requests.DICE_REQ, diceServiceModule::process,
                        Requests.HOROSCOPE_REQ, horoscopeServiceModule::process,
                        Requests.START_REQ, this::greetings,
                        Requests.GOOSE_REQ, this::goose,
                        Requests.ANEK_REQ, this::anek,
                        Requests.MEME_REQ, this::meme
                ),
                this::error
        );
    }

    @Override
    public ShakalServiceRs process(ShakalServiceRq request) {
        String msg = request.getMessageJson().getTextMessage();
        ServiceMethod<ShakalServiceRs, ShakalServiceRq> method = shakalServiceMessageToHandlerContainer.getMethod(msg);
        return method.process(request);
    }

    private ShakalServiceRs greetings(ShakalServiceRq request) {
        return commonServiceModule.createSendTextResponse(Phrases.GREETINGS_MSG);
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
            e.printStackTrace(System.out);
            return commonServiceModule.createSendTextResponse(Phrases.EXCEPTION_MSG);
        }
    }

    private ShakalServiceRs error(ShakalServiceRq request) {
        return commonServiceModule.createSendTextResponse(Phrases.INVALID_COMMAND_MSG);
    }

    private String generateGoose() {
        return Goose.getRandomGoose();
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
            e.printStackTrace(System.out);
            return Phrases.EXCEPTION_MSG;
        }
    }
}
