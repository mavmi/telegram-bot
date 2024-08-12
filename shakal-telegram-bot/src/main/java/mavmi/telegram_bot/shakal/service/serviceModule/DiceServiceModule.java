package mavmi.telegram_bot.shakal.service.serviceModule;

import mavmi.telegram_bot.common.service.dto.common.DiceJson;
import mavmi.telegram_bot.common.service.dto.common.MessageJson;
import mavmi.telegram_bot.common.service.method.ServiceMethod;
import mavmi.telegram_bot.common.service.serviceModule.ServiceModule;
import mavmi.telegram_bot.shakal.cache.ShakalServiceDataCache;
import mavmi.telegram_bot.shakal.constantsHandler.ShakalServiceConstantsHandler;
import mavmi.telegram_bot.shakal.constantsHandler.dto.ShakalServiceConstants;
import mavmi.telegram_bot.shakal.service.serviceModule.common.CommonServiceModule;
import mavmi.telegram_bot.shakal.service.container.ShakalServiceMessageToServiceMethodContainer;
import mavmi.telegram_bot.shakal.service.dto.ShakalServiceRq;
import mavmi.telegram_bot.shakal.service.dto.ShakalServiceRs;
import mavmi.telegram_bot.shakal.service.menu.ShakalServiceMenu;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class DiceServiceModule implements ServiceModule<ShakalServiceRs, ShakalServiceRq> {

    private final ShakalServiceConstants constants;
    private final CommonServiceModule commonServiceModule;
    private final ShakalServiceMessageToServiceMethodContainer shakalServiceMessageToHandlerContainer;

    public DiceServiceModule(
        CommonServiceModule commonServiceModule,
        ShakalServiceConstantsHandler constantsHandler
    ) {
        this.constants = constantsHandler.get();
        this.commonServiceModule = commonServiceModule;
        this.shakalServiceMessageToHandlerContainer = new ShakalServiceMessageToServiceMethodContainer(
                Map.of(constants.getRequests().getDice(), this::diceInit),
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
        return commonServiceModule.createSendDiceResponse(constants.getPhrases().getDice().getStart(), generateDiceArray());
    }

    private ShakalServiceRs play(ShakalServiceRq request) {
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
                    e.printStackTrace(System.out);
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
            return commonServiceModule.createSendTextResponse(constants.getPhrases().getDice().getOk());
        }
        return commonServiceModule.createSendDiceResponse(constants.getPhrases().getDice().getError(), generateDiceArray());
    }

    private String[] generateDiceArray() {
        return new String[]{
                constants.getPhrases().getDice().getDoThrow(),
                constants.getPhrases().getDice().getQuit()
        };
    }
}
