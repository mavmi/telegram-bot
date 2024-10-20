package mavmi.telegram_bot.shakal.service.serviceModule;

import lombok.extern.slf4j.Slf4j;
import mavmi.telegram_bot.common.service.dto.common.DiceJson;
import mavmi.telegram_bot.common.service.dto.common.MessageJson;
import mavmi.telegram_bot.common.service.method.direct.ServiceMethod;
import mavmi.telegram_bot.common.service.serviceModule.direct.ServiceModule;
import mavmi.telegram_bot.shakal.cache.ShakalServiceDataCache;
import mavmi.telegram_bot.shakal.constantsHandler.ShakalServiceConstantsHandler;
import mavmi.telegram_bot.shakal.constantsHandler.dto.ShakalServiceConstants;
import mavmi.telegram_bot.shakal.service.container.ShakalServiceMessageToServiceMethodContainer;
import mavmi.telegram_bot.shakal.service.dto.ShakalServiceRq;
import mavmi.telegram_bot.shakal.service.dto.ShakalServiceRs;
import mavmi.telegram_bot.shakal.service.menu.ShakalServiceMenu;
import mavmi.telegram_bot.shakal.service.serviceModule.common.CommonServiceModule;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
public class DiceServiceModule implements ServiceModule<ShakalServiceRs, ShakalServiceRq> {

    private final CommonServiceModule commonServiceModule;
    private final ShakalServiceMessageToServiceMethodContainer shakalServiceMessageToHandlerContainer;

    public DiceServiceModule(
        CommonServiceModule commonServiceModule,
        ShakalServiceConstantsHandler constantsHandler
    ) {
        this.commonServiceModule = commonServiceModule;
        this.shakalServiceMessageToHandlerContainer = new ShakalServiceMessageToServiceMethodContainer(
                Map.of(commonServiceModule.getConstants().getRequests().getDice(), this::diceInit),
                this::play
        );
    }

    @Override
    public ShakalServiceRs handleRequest(ShakalServiceRq request) {
        String msg = (request.getMessageJson() != null) ? request.getMessageJson().getTextMessage() : null;
        ServiceMethod<ShakalServiceRs, ShakalServiceRq> method = shakalServiceMessageToHandlerContainer.getMethod(msg);
        return method.process(request);
    }

    private ShakalServiceRs diceInit(ShakalServiceRq request) {
        commonServiceModule.getCacheComponent().getCacheBucket().getDataCache(ShakalServiceDataCache.class).getMenuContainer().add(ShakalServiceMenu.DICE);
        return commonServiceModule.createSendDiceResponse(commonServiceModule.getConstants().getPhrases().getDice().getStart(), generateDiceArray());
    }

    private ShakalServiceRs play(ShakalServiceRq request) {
        ShakalServiceConstants constants = commonServiceModule.getConstants();
        MessageJson messageJson = request.getMessageJson();
        DiceJson diceJson = request.getDiceJson();
        ShakalServiceDataCache dataCache = commonServiceModule.getCacheComponent().getCacheBucket().getDataCache(ShakalServiceDataCache.class);

        if (diceJson != null) {
            if (diceJson.getBotDiceValue() != null) {
                dataCache.setBotDice(diceJson.getBotDiceValue());
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

                return commonServiceModule.createSendDiceResponse(responseString, generateDiceArray());
            }
        } else if (messageJson != null && messageJson.getTextMessage().equals(constants.getPhrases().getDice().getQuit())) {
            dataCache.getMenuContainer().removeLast();
            return commonServiceModule.createSendTextDeleteKeyboardResponse(constants.getPhrases().getDice().getOk());
        }
        return commonServiceModule.createSendDiceResponse(constants.getPhrases().getDice().getError(), generateDiceArray());
    }

    private String[] generateDiceArray() {
        return new String[]{
                commonServiceModule.getConstants().getPhrases().getDice().getDoThrow(),
                commonServiceModule.getConstants().getPhrases().getDice().getQuit()
        };
    }
}
