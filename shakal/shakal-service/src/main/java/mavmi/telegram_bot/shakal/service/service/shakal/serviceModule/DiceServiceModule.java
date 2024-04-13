package mavmi.telegram_bot.shakal.service.service.shakal.serviceModule;

import lombok.RequiredArgsConstructor;
import mavmi.telegram_bot.common.dto.common.DiceJson;
import mavmi.telegram_bot.common.dto.common.MessageJson;
import mavmi.telegram_bot.common.dto.dto.impl.shakal.service.ShakalServiceRq;
import mavmi.telegram_bot.common.dto.dto.impl.shakal.service.ShakalServiceRs;
import mavmi.telegram_bot.common.service.method.ServiceMethod;
import mavmi.telegram_bot.common.service.serviceModule.ServiceModule;
import mavmi.telegram_bot.shakal.service.cache.ShakalServiceUserDataCache;
import mavmi.telegram_bot.shakal.service.constants.DicePhrases;
import mavmi.telegram_bot.shakal.service.constants.Phrases;
import mavmi.telegram_bot.shakal.service.constants.Requests;
import mavmi.telegram_bot.shakal.service.service.shakal.container.ShakalServiceMessageToHandlerContainer;
import mavmi.telegram_bot.shakal.service.service.shakal.menu.ShakalServiceMenu;
import mavmi.telegram_bot.shakal.service.service.shakal.serviceModule.common.CommonServiceModule;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class DiceServiceModule implements ServiceModule<ShakalServiceRs, ShakalServiceRq> {

    private final CommonServiceModule commonServiceModule;
    private final ShakalServiceMessageToHandlerContainer shakalServiceMessageToHandlerContainer = new ShakalServiceMessageToHandlerContainer(
            Map.of(Requests.DICE_REQ, this::diceInit),
            this::play
    );

    @Override
    public ShakalServiceRs process(ShakalServiceRq request) {
        String msg = (request.getMessageJson() != null) ? request.getMessageJson().getTextMessage() : null;
        ServiceMethod<ShakalServiceRs, ShakalServiceRq> method = shakalServiceMessageToHandlerContainer.getMethod(msg);
        return method.process(request);
    }

    private ShakalServiceRs diceInit(ShakalServiceRq request) {
        commonServiceModule.getUserSession().getCache().getMenuContainer().add(ShakalServiceMenu.DICE);
        return commonServiceModule.createSendDiceResponse(Phrases.DICE_START, generateDiceArray());
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
                    responseString = DicePhrases.getRandomWinPhrase();
                } else if (user.getUserDice() < user.getBotDice()) {
                    responseString = DicePhrases.getRandomLosePhrase();
                } else {
                    responseString = DicePhrases.getRandomDrawPhrase();
                }

                return commonServiceModule.createSendDiceResponse(responseString, generateDiceArray());
            }
        } else if (messageJson != null && messageJson.getTextMessage().equals(Phrases.DICE_QUIT_MSG)) {
            user.getMenuContainer().removeLast();
            return commonServiceModule.createSendTextResponse(Phrases.DICE_OK_MSG);
        }
        return commonServiceModule.createSendDiceResponse(Phrases.DICE_ERROR_MSG, generateDiceArray());
    }

    private String[] generateDiceArray() {
        return new String[]{
                Phrases.DICE_THROW_MSG,
                Phrases.DICE_QUIT_MSG
        };
    }
}
