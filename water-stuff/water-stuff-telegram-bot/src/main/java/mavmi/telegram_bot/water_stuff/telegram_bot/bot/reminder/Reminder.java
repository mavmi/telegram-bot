package mavmi.telegram_bot.water_stuff.telegram_bot.bot.reminder;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import mavmi.telegram_bot.common.dto.impl.water_stuff.reminder_service.ReminderServiceRs;
import mavmi.telegram_bot.common.dto.impl.water_stuff.reminder_service.inner.ReminderServiceRsElement;
import mavmi.telegram_bot.water_stuff.telegram_bot.bot.Bot;
import mavmi.telegram_bot.water_stuff.telegram_bot.httpClient.HttpClient;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class Reminder extends Thread {

    private final Bot telegramBot;
    private final HttpClient httpClient;
    private final long sleepTime;

    public Reminder(
            Bot telegramBot,
            HttpClient httpClient,
            @Value("${reminder.sleep-time}") long sleepTime
    ) {
        this.telegramBot = telegramBot;
        this.httpClient = httpClient;
        this.sleepTime = sleepTime;
    }

    @Override
    @PostConstruct
    public void run() {
        while (true) {
            try {
                Response response = httpClient.reminderServiceRequest();

                if (response.code() == HttpStatus.OK.value()) {
                    ObjectMapper objectMapper = new ObjectMapper();
                    ReminderServiceRs reminderServiceRs = objectMapper.readValue(response.body().string(), ReminderServiceRs.class);
                    for (ReminderServiceRsElement element : reminderServiceRs.getReminderServiceRsElements()) {
                        log.info("Send remind to id {}", element.getChatId());
                        telegramBot.sendText(
                                element.getChatId(),
                                element.getMessageJson().getTextMessage()
                        );
                    }
                } else {
                    log.error("Got error from reminder service");
                }

                Thread.sleep(sleepTime);
            } catch (Exception e) {
                e.printStackTrace(System.out);
            }
        }
    }
}
