package mavmi.telegram_bot.water_stuff.telegramBot.reminder;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mavmi.parameters_management_system.client.plugin.impl.remote.RemoteParameterPlugin;
import mavmi.telegram_bot.water_stuff.service.reminder.dto.ReminderServiceRs;
import mavmi.telegram_bot.water_stuff.service.reminder.dto.inner.ReminderServiceRsElement;
import mavmi.telegram_bot.water_stuff.service.reminder.ReminderService;
import mavmi.telegram_bot.water_stuff.telegramBot.client.WaterTelegramBotSender;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class Reminder extends Thread {

    private final RemoteParameterPlugin parameterPlugin;
    private final ReminderService reminderService;
    private final WaterTelegramBotSender sender;

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
                    sender.sendText(
                            element.getChatId(),
                            element.getMessageJson().getTextMessage()
                    );
                }

                sleep(parameterPlugin.getParameter("water.reminder.sleep-time").getLong());
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }
    }
}
