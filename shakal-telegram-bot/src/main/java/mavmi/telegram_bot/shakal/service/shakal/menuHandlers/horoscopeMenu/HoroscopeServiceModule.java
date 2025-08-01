package mavmi.telegram_bot.shakal.service.shakal.menuHandlers.horoscopeMenu;

import lombok.extern.slf4j.Slf4j;
import mavmi.telegram_bot.lib.menu_engine_starter.engine.MenuEngine;
import mavmi.telegram_bot.lib.menu_engine_starter.handler.api.MenuRequestHandler;
import mavmi.telegram_bot.shakal.cache.dto.ShakalDataCache;
import mavmi.telegram_bot.shakal.constantsHandler.dto.ShakalConstants;
import mavmi.telegram_bot.shakal.service.shakal.dto.ShakalServiceRq;
import mavmi.telegram_bot.shakal.service.shakal.menu.ShakalServiceMenu;
import mavmi.telegram_bot.shakal.service.shakal.menuHandlers.utils.CommonUtils;
import mavmi.telegram_bot.shakal.service.shakal.menuHandlers.utils.HoroscopeUtils;
import mavmi.telegram_bot.shakal.service.shakal.menuHandlers.utils.TelegramBotUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
public class HoroscopeServiceModule extends MenuRequestHandler<ShakalServiceRq> {

    private final CommonUtils commonUtils;
    private final TelegramBotUtils telegramBotUtils;

    public HoroscopeServiceModule(MenuEngine menuEngine,
                                  CommonUtils commonUtils,
                                  TelegramBotUtils telegramBotUtils) {
        super(menuEngine, ShakalServiceMenu.HOROSCOPE);
        this.commonUtils = commonUtils;
        this.telegramBotUtils = telegramBotUtils;
    }

    @Override
    public void handleRequest(ShakalServiceRq request) {
        String msg = request.getMessageJson().getTextMessage();

        if (msg.equals(commonUtils.getConstants().getRequests().getHoroscope())) {
            askForSign(request);
        } else {
            process(request);
        }
    }

    private void askForSign(ShakalServiceRq request) {
        commonUtils.getUserCaches().getDataCache(ShakalDataCache.class).getMenuHistoryContainer().add(ShakalServiceMenu.HOROSCOPE);
        telegramBotUtils.sendReplyKeyboard(request.getChatId(),
                commonUtils.getConstants().getPhrases().getHoroscope().getQuestion(),
                menuEngine.getMenuButtonsAsString(ShakalServiceMenu.HOROSCOPE));
    }

    private void process(ShakalServiceRq request) {
        ShakalConstants constants = commonUtils.getConstants();
        String msg = request.getMessageJson().getTextMessage();
        String sign = HoroscopeUtils.getSign(msg);
        if (sign == null) {
            telegramBotUtils.sendReplyKeyboard(request.getChatId(),
                    constants.getPhrases().getHoroscope().getError(),
                    menuEngine.getMenuButtonsAsString(ShakalServiceMenu.HOROSCOPE));
        } else {
            commonUtils.dropUserCaches();
            telegramBotUtils.sendTextDeleteKeyboard(request.getChatId(), generateHoroscope(sign));
        }
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
            return commonUtils.getConstants().getPhrases().getCommon().getError();
        }
    }
}
