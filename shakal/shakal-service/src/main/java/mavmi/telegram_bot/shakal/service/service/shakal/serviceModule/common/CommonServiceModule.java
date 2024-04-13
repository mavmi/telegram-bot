package mavmi.telegram_bot.shakal.service.service.shakal.serviceModule.common;

import lombok.Getter;
import mavmi.telegram_bot.common.dto.common.KeyboardJson;
import mavmi.telegram_bot.common.dto.common.MessageJson;
import mavmi.telegram_bot.common.dto.common.tasks.SHAKAL_SERVICE_TASK;
import mavmi.telegram_bot.common.dto.dto.impl.shakal.service.ShakalServiceRs;
import mavmi.telegram_bot.common.httpFilter.userSession.session.UserSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Getter
@Component
public class CommonServiceModule {

    @Autowired
    private UserSession userSession;

    public ShakalServiceRs createSendTextResponse(String msg) {
        MessageJson messageJson = MessageJson
                .builder()
                .textMessage(msg)
                .build();

        return ShakalServiceRs
                .builder()
                .shakalServiceTask(SHAKAL_SERVICE_TASK.SEND_TEXT)
                .messageJson(messageJson)
                .build();
    }

    public ShakalServiceRs createSendKeyboardResponse(String msg, String[] keyboardButtons) {
        MessageJson messageJson = MessageJson
                .builder()
                .textMessage(msg)
                .build();

        KeyboardJson keyboardJson = KeyboardJson
                .builder()
                .keyboardButtons(keyboardButtons)
                .build();

        return ShakalServiceRs
                .builder()
                .shakalServiceTask(SHAKAL_SERVICE_TASK.SEND_KEYBOARD)
                .messageJson(messageJson)
                .keyboardJson(keyboardJson)
                .build();
    }

    public ShakalServiceRs createSendDiceResponse(String msg, String[] keyboardButtons) {
        MessageJson messageJson = MessageJson
                .builder()
                .textMessage(msg)
                .build();

        KeyboardJson keyboardJson = KeyboardJson
                .builder()
                .keyboardButtons(keyboardButtons)
                .build();

        return ShakalServiceRs
                .builder()
                .shakalServiceTask(SHAKAL_SERVICE_TASK.SEND_DICE)
                .messageJson(messageJson)
                .keyboardJson(keyboardJson)
                .build();
    }
}
