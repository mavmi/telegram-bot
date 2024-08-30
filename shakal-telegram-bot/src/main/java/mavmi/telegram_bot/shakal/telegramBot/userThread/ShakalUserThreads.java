package mavmi.telegram_bot.shakal.telegramBot.userThread;

import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import lombok.RequiredArgsConstructor;
import mavmi.telegram_bot.common.telegramBot.userThread.UserThreads;
import mavmi.telegram_bot.shakal.mapper.RequestsMapper;
import mavmi.telegram_bot.shakal.service.ShakalDirectService;
import mavmi.telegram_bot.shakal.telegramBot.ShakalTelegramBotSender;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ShakalUserThreads extends UserThreads<ShakalUserThread> {

    private final RequestsMapper requestsMapper;
    private final ShakalDirectService shakalService;
    private final ShakalTelegramBotSender sender;

    @Override
    public void add(Update update) {
        Message message = update.message();
        if (message == null) {
            return;
        }

        long chatId = message.chat().id();
        ShakalUserThread userThread = (ShakalUserThread) tgIdToUserThread.get(chatId);

        if (userThread == null) {
            userThread = new ShakalUserThread(this, requestsMapper, shakalService, sender, chatId);
            tgIdToUserThread.put(chatId, userThread);
            userThread.add(update);
            new Thread(userThread).start();
        } else {
            userThread.add(update);
        }
    }
}
