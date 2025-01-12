package mavmi.telegram_bot.shakal.service.serviceComponents.serviceModule;

import lombok.extern.slf4j.Slf4j;
import mavmi.telegram_bot.common.service.serviceComponents.container.ServiceComponentsContainer;
import mavmi.telegram_bot.common.service.dto.common.DiceJson;
import mavmi.telegram_bot.common.service.dto.common.MessageJson;
import mavmi.telegram_bot.common.service.serviceComponents.method.ServiceMethod;
import mavmi.telegram_bot.common.service.serviceComponents.serviceModule.ServiceModule;
import mavmi.telegram_bot.shakal.cache.ShakalDataCache;
import mavmi.telegram_bot.shakal.constantsHandler.dto.ShakalConstants;
import mavmi.telegram_bot.shakal.service.dto.ShakalServiceRq;
import mavmi.telegram_bot.shakal.service.menu.ShakalServiceMenu;
import mavmi.telegram_bot.shakal.service.serviceComponents.serviceModule.common.CommonServiceModule;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class DiceServiceModule implements ServiceModule<ShakalServiceRq> {

    private final CommonServiceModule commonServiceModule;
    private final ServiceComponentsContainer<ShakalServiceRq> serviceComponentsContainer = new ServiceComponentsContainer<>();

    public DiceServiceModule(CommonServiceModule commonServiceModule) {
        this.commonServiceModule = commonServiceModule;
        this.serviceComponentsContainer.add(commonServiceModule.getConstants().getRequests().getDice(), this::diceInit)
                .setDefaultServiceMethod(this::play);
    }

    @Override
    public void handleRequest(ShakalServiceRq request) {
        String msg = (request.getMessageJson() != null) ? request.getMessageJson().getTextMessage() : null;
        ServiceMethod<ShakalServiceRq> method = serviceComponentsContainer.getMethod(msg);
        method.process(request);
    }

    private void diceInit(ShakalServiceRq request) {
        commonServiceModule.getCacheComponent().getCacheBucket().getDataCache(ShakalDataCache.class).setMenu(ShakalServiceMenu.DICE);
        commonServiceModule.sendDice(request.getChatId(), commonServiceModule.getConstants().getPhrases().getDice().getStart(), generateDiceArray());
    }

    private void play(ShakalServiceRq request) {
        ShakalConstants constants = commonServiceModule.getConstants();
        MessageJson messageJson = request.getMessageJson();
        DiceJson diceJson = request.getDiceJson();
        ShakalDataCache dataCache = commonServiceModule.getCacheComponent().getCacheBucket().getDataCache(ShakalDataCache.class);

        if (diceJson != null) {
            if (diceJson.getBotDiceValue() != null) {
                dataCache.setBotDice(diceJson.getBotDiceValue());
                commonServiceModule.sendDice(request.getChatId(), constants.getPhrases().getDice().getError(), generateDiceArray());
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

                commonServiceModule.sendDice(request.getChatId(), responseString, generateDiceArray());
            }
        } else if (messageJson != null && messageJson.getTextMessage().equals(constants.getPhrases().getDice().getQuit())) {
            commonServiceModule.dropUserCaches();
            commonServiceModule.sendTextDeleteKeyboard(request.getChatId(), constants.getPhrases().getDice().getOk());
        }
    }

    private String[] generateDiceArray() {
        return new String[]{
                commonServiceModule.getConstants().getPhrases().getDice().getDoThrow(),
                commonServiceModule.getConstants().getPhrases().getDice().getQuit()
        };
    }
}
