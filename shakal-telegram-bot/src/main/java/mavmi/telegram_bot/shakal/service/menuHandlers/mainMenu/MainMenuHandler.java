package mavmi.telegram_bot.shakal.service.menuHandlers.mainMenu;

import com.github.blad3mak3r.memes4j.Memes4J;
import com.github.blad3mak3r.memes4j.PendingRequest;
import lombok.extern.slf4j.Slf4j;
import mavmi.telegram_bot.lib.menu_engine_starter.engine.MenuEngine;
import mavmi.telegram_bot.lib.menu_engine_starter.handler.api.MenuRequestHandler;
import mavmi.telegram_bot.shakal.service.dto.ShakalServiceRq;
import mavmi.telegram_bot.shakal.service.menu.ShakalServiceMenu;
import mavmi.telegram_bot.shakal.service.menuHandlers.utils.CommonUtils;
import mavmi.telegram_bot.shakal.service.menuHandlers.utils.TelegramBotUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
public class MainMenuHandler extends MenuRequestHandler<ShakalServiceRq> {

    private final CommonUtils commonUtils;
    private final TelegramBotUtils telegramBotUtils;

    public MainMenuHandler(MenuEngine menuEngine,
                           CommonUtils commonUtils,
                           TelegramBotUtils telegramBotUtils) {
        super(menuEngine, ShakalServiceMenu.MAIN_MENU);
        this.commonUtils = commonUtils;
        this.telegramBotUtils = telegramBotUtils;
    }

    @Override
    public void handleRequest(ShakalServiceRq request) {
        String msg = request.getMessageJson().getTextMessage();

        if (msg.equals(commonUtils.getConstants().getRequests().getDice())) {
            menuEngine.proxyRequest(ShakalServiceMenu.DICE, request);
        } else if (msg.equals(commonUtils.getConstants().getRequests().getHoroscope())) {
            menuEngine.proxyRequest(ShakalServiceMenu.HOROSCOPE, request);
        } else if (msg.equals(commonUtils.getConstants().getRequests().getStart())) {
            greetings(request);
        } else if (msg.equals(commonUtils.getConstants().getRequests().getGoose())) {
            goose(request);
        } else if (msg.equals(commonUtils.getConstants().getRequests().getAnek())) {
            anek(request);
        } else if (msg.equals(commonUtils.getConstants().getRequests().getMeme())) {
            meme(request);
        } else {
            error(request);
        }
    }

    private void greetings(ShakalServiceRq request) {
        telegramBotUtils.sendText(request.getChatId(), commonUtils.getConstants().getPhrases().getCommon().getGreetings());
    }

    private void goose(ShakalServiceRq request) {
        telegramBotUtils.sendText(request.getChatId(), generateGoose());
    }

    private void anek(ShakalServiceRq request) {
        telegramBotUtils.sendText(request.getChatId(), generateAnek());
    }

    private void meme(ShakalServiceRq request) {
        PendingRequest memeRequest = Memes4J.getRandomMeme();

        try {
            telegramBotUtils.sendText(request.getChatId(), memeRequest.complete().getImage());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            telegramBotUtils.sendText(request.getChatId(), commonUtils.getConstants().getPhrases().getCommon().getError());
        }
    }

    private void error(ShakalServiceRq request) {
        telegramBotUtils.sendText(request.getChatId(), commonUtils.getConstants().getPhrases().getCommon().getInvalidInput());
    }

    private String generateGoose() {
        return commonUtils.getConstants().getGoose().getRandomGoose();
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
            return commonUtils.getConstants().getPhrases().getCommon().getInvalidInput();
        }
    }
}
