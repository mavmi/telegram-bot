package mavmi.telegram_bot.shakal.service.service.shakal.serviceModule;

import mavmi.telegram_bot.common.dto.common.DiceJson;
import mavmi.telegram_bot.common.dto.common.MessageJson;
import mavmi.telegram_bot.common.dto.dto.impl.shakal.service.ShakalServiceRq;
import mavmi.telegram_bot.common.dto.dto.impl.shakal.service.ShakalServiceRs;
import mavmi.telegram_bot.common.service.method.ServiceMethod;
import mavmi.telegram_bot.common.service.serviceModule.ServiceModule;
import mavmi.telegram_bot.shakal.service.cache.ShakalServiceUserDataCache;
import mavmi.telegram_bot.shakal.service.constantsHandler.ShakalServiceConstantsHandler;
import mavmi.telegram_bot.shakal.service.constantsHandler.dto.ShakalServiceConstants;
import mavmi.telegram_bot.shakal.service.service.shakal.container.ShakalServiceMessageToHandlerContainer;
import mavmi.telegram_bot.shakal.service.service.shakal.menu.ShakalServiceMenu;
import mavmi.telegram_bot.shakal.service.service.shakal.serviceModule.common.CommonServiceModule;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class DiceServiceModule implements ServiceModule<ShakalServiceRs, ShakalServiceRq> {

    private final ShakalServiceConstants constants;
    private final CommonServiceModule commonServiceModule;
    private final ShakalServiceMessageToHandlerContainer shakalServiceMessageToHandlerContainer;

    public DiceServiceModule(
        CommonServiceModule commonServiceModule,
        ShakalServiceConstantsHandler constantsHandler
    ) {
        this.constants = constantsHandler.get();
        this.commonServiceModule = commonServiceModule;
        this.shakalServiceMessageToHandlerContainer = new ShakalServiceMessageToHandlerContainer(
                Map.of(constants.getRequests().getDice(), this::diceInit),
                this::play
        );
    }

    @Override
    public ShakalServiceRs process(ShakalServiceRq request) {
        String msg = (request.getMessageJson() != null) ? request.getMessageJson().getTextMessage() : null;
        ServiceMethod<ShakalServiceRs, ShakalServiceRq> method = shakalServiceMessageToHandlerContainer.getMethod(msg);
        return method.process(request);
    }

    private ShakalServiceRs diceInit(ShakalServiceRq request) {
        commonServiceModule.getUserSession().getCache().getMenuContainer().add(ShakalServiceMenu.DICE);
        return commonServiceModule.createSendDiceResponse(constants.getPhrases().getDice().getStart(), generateDiceArray());
    }

    private ShakalServiceRs play(ShakalServiceRq request) {
        MessageJson messageJson = request.getMessageJson();
        DiceJson diceJson = request.getDiceJson();
        ShakalServiceUserDataCache user = commonServiceModule.getUserSession().getCache();

        if (diceJson != null) {
            if (diceJson.getBotDiceValue() != null) {
                user.setBotDice(diceJson.getBotDiceValue());
            } else {
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace(System.out);
                }

                user.setUserDice(diceJson.getUserDiceValue());
                String responseString;
                if (user.getUserDice() > user.getBotDice()) {
                    responseString = constants.getPhrases().getDice().getRandomWinPhrase();
                } else if (user.getUserDice() < user.getBotDice()) {
                    responseString = constants.getPhrases().getDice().getRandomLosePhrase();
                } else {
                    responseString = constants.getPhrases().getDice().getRandomDrawPhrase();
                }

                return commonServiceModule.createSendDiceResponse(responseString, generateDiceArray());
            }
        } else if (messageJson != null && messageJson.getTextMessage().equals(constants.getPhrases().getDice().getQuit())) {
            user.getMenuContainer().removeLast();
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
