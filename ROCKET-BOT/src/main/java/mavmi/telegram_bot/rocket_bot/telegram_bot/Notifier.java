package mavmi.telegram_bot.rocket_bot.telegram_bot;

import com.pengrad.telegrambot.request.SendMessage;
import lombok.AllArgsConstructor;

import java.util.Set;

import static mavmi.telegram_bot.rocket_bot.constants.Phrases.EXECUTION_FAIL_MSG;

@AllArgsConstructor
public class Notifier extends Thread{
    private final Bot bot;
    private final Long sleepTime;
    private final Set<Long> userIdx;

    @Override
    public void run() {
        while (true) {
            int usersCount = userIdx.size();
            bot.getLogger().log("Количество активных пользователей: " + usersCount);
            if (usersCount != 0) {
                bot.getLogger().log("Запуск проверки новых сообщений");
                for (Long id : userIdx) {
                    bot.updateLastMessages(id, true);
                }
            }
            try {
                sleep(sleepTime);
            } catch (InterruptedException e) {
                bot.getLogger().err(e.getMessage());
            }
        }
    }

    void addUser(Long id) {
        userIdx.add(id);
    }

    void deleteUser(Long id) {
        userIdx.remove(id);
    }
}
