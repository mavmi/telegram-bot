package mavmi.telegram_bot.shakal.service.menuHandlers.diceMenu;

import lombok.extern.slf4j.Slf4j;
import mavmi.telegram_bot.lib.dto.service.common.DiceJson;
import mavmi.telegram_bot.lib.dto.service.common.MessageJson;
import mavmi.telegram_bot.lib.menu_engine_starter.engine.MenuEngine;
import mavmi.telegram_bot.lib.menu_engine_starter.handler.api.MenuRequestHandler;
import mavmi.telegram_bot.shakal.cache.dto.ShakalDataCache;
import mavmi.telegram_bot.shakal.constantsHandler.dto.ShakalConstants;
import mavmi.telegram_bot.shakal.service.dto.ShakalServiceRq;
import mavmi.telegram_bot.shakal.service.menu.ShakalServiceMenu;
import mavmi.telegram_bot.shakal.service.menuHandlers.utils.CommonUtils;
import mavmi.telegram_bot.shakal.service.menuHandlers.utils.TelegramBotUtils;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class DiceServiceModule extends MenuRequestHandler<ShakalServiceRq> {

    private final CommonUtils commonUtils;
    private final TelegramBotUtils telegramBotUtils;

    public DiceServiceModule(MenuEngine menuEngine,
                             CommonUtils commonUtils,
                             TelegramBotUtils telegramBotUtils) {
        super(menuEngine, ShakalServiceMenu.DICE);
        this.commonUtils = commonUtils;
        this.telegramBotUtils = telegramBotUtils;
    }

    @Override
    public void handleRequest(ShakalServiceRq request) {
        String msg = (request.getMessageJson() != null) ? request.getMessageJson().getTextMessage() : null;

        if (msg != null && msg.equals(commonUtils.getConstants().getRequests().getDice())) {
            diceInit(request);
        } else {
            play(request);
        }
    }

    private void diceInit(ShakalServiceRq request) {
        commonUtils.getUserCaches().getDataCache(ShakalDataCache.class).getMenuHistoryContainer().add(ShakalServiceMenu.DICE);
        telegramBotUtils.sendDice(request.getChatId(),
                commonUtils.getConstants().getPhrases().getDice().getStart(),
                menuEngine.getMenuButtons(ShakalServiceMenu.DICE).toArray(new String[0]));
    }

    private void play(ShakalServiceRq request) {
        ShakalConstants constants = commonUtils.getConstants();
        MessageJson messageJson = request.getMessageJson();
        DiceJson diceJson = request.getDiceJson();
        ShakalDataCache dataCache = commonUtils.getUserCaches().getDataCache(ShakalDataCache.class);

        if (diceJson != null) {
            if (diceJson.getBotDiceValue() != null) {
                dataCache.setBotDice(diceJson.getBotDiceValue());
                telegramBotUtils.sendDice(request.getChatId(), constants.getPhrases().getDice().getError(),
                        menuEngine.getMenuButtons(ShakalServiceMenu.DICE).toArray(new String[0]));
            } else {
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    log.error(e.getMessage(), e);
                }

                dataCache.setUserDice(diceJson.getUserDiceValue());
                String responseString;
                if (dataCache.getUserDice() > dataCache.getBotDice()) {
                    responseString = constants.getPhrases().getDice().getRandomWinPhrase();
                } else if (dataCache.getUserDice() < dataCache.getBotDice()) {
                    responseString = constants.getPhrases().getDice().getRandomLosePhrase();
                } else {
                    responseString = constants.getPhrases().getDice().getRandomDrawPhrase();
                }

                telegramBotUtils.sendDice(request.getChatId(),
                        responseString,
                        menuEngine.getMenuButtons(ShakalServiceMenu.DICE).toArray(new String[0]));
            }
        } else if (messageJson != null && messageJson.getTextMessage().equals(constants.getPhrases().getDice().getQuit())) {
            commonUtils.dropUserCaches();
            telegramBotUtils.sendTextDeleteKeyboard(request.getChatId(), constants.getPhrases().getDice().getOk());
        }
    }
}
