package mavmi.telegram_bot.shakal.service.menuHandlers.apolocheeseMenu;

import mavmi.telegram_bot.lib.menu_engine_starter.engine.MenuEngine;
import mavmi.telegram_bot.lib.menu_engine_starter.handler.api.MenuRequestHandler;
import mavmi.telegram_bot.shakal.cache.dto.ShakalDataCache;
import mavmi.telegram_bot.shakal.service.dto.ShakalServiceRq;
import mavmi.telegram_bot.shakal.service.menu.ShakalServiceMenu;
import mavmi.telegram_bot.shakal.service.menuHandlers.utils.CommonUtils;
import mavmi.telegram_bot.shakal.service.menuHandlers.utils.TelegramBotUtils;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;

@Component
public class ApolocheseServiceModule extends MenuRequestHandler<ShakalServiceRq> {

    private final CommonUtils commonUtils;
    private final TelegramBotUtils telegramBotUtils;

    public ApolocheseServiceModule(MenuEngine menuEngine,
                                   CommonUtils commonUtils,
                                   TelegramBotUtils telegramBotUtils) {
        super(menuEngine, ShakalServiceMenu.APOLOCHEESE);
        this.commonUtils = commonUtils;
        this.telegramBotUtils = telegramBotUtils;
    }

    @Override
    public void handleRequest(ShakalServiceRq request) {
        String msg = request.getMessageJson().getTextMessage();

        if (msg.equals(commonUtils.getConstants().getRequests().getApolocheese())) {
            askForName(request);
        } else {
            process(request);
        }
    }

    private void askForName(ShakalServiceRq request) {
        commonUtils.getUserCaches().getDataCache(ShakalDataCache.class).getMenuHistoryContainer().add(ShakalServiceMenu.APOLOCHEESE);
        telegramBotUtils.sendText(request.getChatId(),
                commonUtils.getConstants().getPhrases().getCommon().getApolocheese());
    }

    private void process(ShakalServiceRq request) {
        String msg = request.getMessageJson().getTextMessage();
        commonUtils.dropUserCaches();
        telegramBotUtils.sendText(
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
