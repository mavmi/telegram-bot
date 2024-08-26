package mavmi.telegram_bot.water_stuff.telegramBot.reminder;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import mavmi.telegram_bot.water_stuff.service.dto.reminderService.ReminderServiceRs;
import mavmi.telegram_bot.water_stuff.service.dto.reminderService.inner.ReminderServiceRsElement;
import mavmi.telegram_bot.water_stuff.service.reminder.ReminderService;
import mavmi.telegram_bot.water_stuff.telegramBot.WaterStuffTelegramBot;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class Reminder extends Thread {

    private final ReminderService reminderService;
    private final WaterStuffTelegramBot telegramWaterStuffTelegramBot;
    private final long sleepTime;

    public Reminder(
            WaterStuffTelegramBot telegramWaterStuffTelegramBot,
            ReminderService reminderService,
            @Value("${reminder.sleep-time}") long sleepTime
    ) {
        this.telegramWaterStuffTelegramBot = telegramWaterStuffTelegramBot;
        this.reminderService = reminderService;
        this.sleepTime = sleepTime;
    }

    @PostConstruct
    public void postConstruct() {
        this.start();
    }

    @Override
    public void run() {
        while (true) {
            try {
                ReminderServiceRs reminderServiceRs = reminderService.handleRequest();
                for (ReminderServiceRsElement element : reminderServiceRs.getReminderServiceRsElements()) {
                    log.info("Send remind to id {}", element.getChatId());
                    telegramWaterStuffTelegramBot.sendText(
                            element.getChatId(),
                            element.getMessageJson().getTextMessage()
                    );
                }

                sleep(sleepTime);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }
    }
}
