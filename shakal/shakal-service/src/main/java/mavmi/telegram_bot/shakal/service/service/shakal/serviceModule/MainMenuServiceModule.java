package mavmi.telegram_bot.shakal.service.service.shakal.serviceModule;

import com.github.blad3mak3r.memes4j.Memes4J;
import com.github.blad3mak3r.memes4j.PendingRequest;
import mavmi.telegram_bot.common.dto.dto.impl.shakal.service.ShakalServiceRq;
import mavmi.telegram_bot.common.dto.dto.impl.shakal.service.ShakalServiceRs;
import mavmi.telegram_bot.common.service.method.ServiceMethod;
import mavmi.telegram_bot.common.service.serviceModule.ServiceModule;
import mavmi.telegram_bot.shakal.service.constantsHandler.ShakalServiceConstantsHandler;
import mavmi.telegram_bot.shakal.service.constantsHandler.dto.ShakalServiceConstants;
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

    private final ShakalServiceConstants constants;
    private final CommonServiceModule commonServiceModule;
    private final ShakalServiceMessageToHandlerContainer shakalServiceMessageToHandlerContainer;

    public MainMenuServiceModule(
            CommonServiceModule commonServiceModule,
            ApolocheseServiceModule apolocheseServiceModule,
            DiceServiceModule diceServiceModule,
            HoroscopeServiceModule horoscopeServiceModule,
            ShakalServiceConstantsHandler constantsHandler
    ) {
        this.constants = constantsHandler.get();
        this.commonServiceModule = commonServiceModule;
        this.shakalServiceMessageToHandlerContainer = new ShakalServiceMessageToHandlerContainer(
                Map.of(
                        constants.getRequests().getApolocheese(), apolocheseServiceModule::process,
                        constants.getRequests().getDice(), diceServiceModule::process,
                        constants.getRequests().getHoroscope(), horoscopeServiceModule::process,
                        constants.getRequests().getStart(), this::greetings,
                        constants.getRequests().getGoose(), this::goose,
                        constants.getRequests().getAnek(), this::anek,
                        constants.getRequests().getMeme(), this::meme
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
        return commonServiceModule.createSendTextResponse(constants.getPhrases().getCommon().getGreetings());
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
            return commonServiceModule.createSendTextResponse(constants.getPhrases().getCommon().getError());
        }
    }

    private ShakalServiceRs error(ShakalServiceRq request) {
        return commonServiceModule.createSendTextResponse(constants.getPhrases().getCommon().getInvalidInput());
    }

    private String generateGoose() {
        return constants.getGoose().getRandomGoose();
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
            return constants.getPhrases().getCommon().getInvalidInput();
        }
    }
}
