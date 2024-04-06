package mavmi.telegram_bot.water_stuff.telegram_bot.telegramBot.reminder;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import mavmi.telegram_bot.common.dto.dto.impl.water_stuff.reminder_service.ReminderServiceRs;
import mavmi.telegram_bot.common.dto.dto.impl.water_stuff.reminder_service.inner.ReminderServiceRsElement;
import mavmi.telegram_bot.water_stuff.telegram_bot.telegramBot.WaterStuffTelegramBot;
import mavmi.telegram_bot.water_stuff.telegram_bot.httpClient.WaterStuffTelegramBotHttpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class Reminder extends Thread {

    private final WaterStuffTelegramBot telegramWaterStuffTelegramBot;
    private final WaterStuffTelegramBotHttpClient waterStuffTelegramBotHttpClient;
    private final long sleepTime;

    public Reminder(
            WaterStuffTelegramBot telegramWaterStuffTelegramBot,
            WaterStuffTelegramBotHttpClient waterStuffTelegramBotHttpClient,
            @Value("${reminder.sleep-time}") long sleepTime
    ) {
        this.telegramWaterStuffTelegramBot = telegramWaterStuffTelegramBot;
        this.waterStuffTelegramBotHttpClient = waterStuffTelegramBotHttpClient;
        this.sleepTime = sleepTime;
    }

    @Override
    @PostConstruct
    public void run() {
        while (true) {
            try {
                ResponseEntity<ReminderServiceRs> response = waterStuffTelegramBotHttpClient.reminderServiceRequest();

                if (response != null && response.getStatusCode().equals(HttpStatusCode.valueOf(HttpStatus.OK.value()))) {
                    ReminderServiceRs reminderServiceRs = response.getBody();
                    for (ReminderServiceRsElement element : reminderServiceRs.getReminderServiceRsElements()) {
                        log.info("Send remind to id {}", element.getChatId());
                        telegramWaterStuffTelegramBot.sendText(
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
