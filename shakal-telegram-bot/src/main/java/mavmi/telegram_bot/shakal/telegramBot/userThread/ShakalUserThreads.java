package mavmi.telegram_bot.shakal.telegramBot.userThread;

import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import lombok.RequiredArgsConstructor;
import mavmi.telegram_bot.lib.telegram_bot_starter.userThread.UserThreads;
import mavmi.telegram_bot.lib.user_cache_starter.provider.UserCachesProvider;
import mavmi.telegram_bot.shakal.mapper.RequestsMapper;
import mavmi.telegram_bot.shakal.service.ShakalService;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ShakalUserThreads extends UserThreads<ShakalUserThread> {

    private final UserCachesProvider userCachesProvider;
    private final RequestsMapper requestsMapper;
    private final ShakalService shakalService;

    @Override
    public void add(Update update) {
        Message message = update.message();
        if (message == null) {
            return;
        }

        long chatId = message.chat().id();
        ShakalUserThread userThread = (ShakalUserThread) tgIdToUserThread.get(chatId);

        if (userThread == null) {
            userThread = new ShakalUserThread(this, userCachesProvider, requestsMapper, shakalService, chatId);
            tgIdToUserThread.put(chatId, userThread);
            userThread.add(update);
            Thread.ofVirtual().start(userThread);
        } else {
            userThread.add(update);
        }
    }
}
