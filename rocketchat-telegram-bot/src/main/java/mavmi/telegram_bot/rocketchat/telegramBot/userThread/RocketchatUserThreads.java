package mavmi.telegram_bot.rocketchat.telegramBot.userThread;

import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import lombok.RequiredArgsConstructor;
import mavmi.telegram_bot.common.telegramBot.userThread.UserThreads;
import mavmi.telegram_bot.rocketchat.mapper.RequestsMapper;
import mavmi.telegram_bot.rocketchat.service.RocketchatService;
import mavmi.telegram_bot.rocketchat.telegramBot.RocketchatTelegramBotSender;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RocketchatUserThreads extends UserThreads<RocketchatUserThread> {

    private final RocketchatTelegramBotSender sender;
    private final RocketchatService rocketchatService;
    private final RequestsMapper requestsMapper;

    @Override
    public void add(Update update) {
        Message message = update.message();
        if (message == null) {
            return;
        }

        long chatId = message.chat().id();
        RocketchatUserThread userThread = (RocketchatUserThread) tgIdToUserThread.get(chatId);

        if (userThread == null) {
            userThread = new RocketchatUserThread(this, sender, rocketchatService, requestsMapper, chatId);
            tgIdToUserThread.put(chatId, userThread);
            userThread.add(update);
            Thread.ofVirtual().start(userThread);
        } else {
            userThread.add(update);
        }
    }
}
