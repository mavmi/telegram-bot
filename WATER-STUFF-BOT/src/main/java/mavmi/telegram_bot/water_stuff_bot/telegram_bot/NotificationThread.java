package mavmi.telegram_bot.water_stuff_bot.telegram_bot;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import mavmi.telegram_bot.water_stuff_bot.data.pause.UsersPauseNotificationsData;
import mavmi.telegram_bot.water_stuff_bot.data.water.UsersWaterData;
import mavmi.telegram_bot.water_stuff_bot.data.water.WaterInfo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class NotificationThread extends Thread {
    private final long sleepTime;
    private final UsersWaterData usersWaterData;
    private final UsersPauseNotificationsData usersPauseNotificationsData;

    @Setter
    private Bot telegramBot;

    public NotificationThread(
            UsersWaterData usersWaterData,
            UsersPauseNotificationsData usersPauseNotificationsData,
            @Value("${bot.sleep-time}") Long sleepTime
    ) {
        this.usersWaterData = usersWaterData;
        this.usersPauseNotificationsData = usersPauseNotificationsData;
        this.sleepTime = sleepTime;
    }

    @Override
    public void run() {
        while (true) {
            try {
                for (Long id : usersWaterData.getUsersIdx()) {
                    String msg = generateMessage(id);
                    if (msg != null) {
                        Long userPauseValue = usersPauseNotificationsData.get(id);
                        if (userPauseValue != null && System.currentTimeMillis() < userPauseValue) {
                            log.info("User paused notifications; id {}", id);
                        } else {
                            telegramBot.sendMessage(id, msg);
                            log.info("Message sent to id: {}", id);
                        }
                    } else {
                        log.debug("Message is null; id {}", id);
                    }
                }
                sleep(sleepTime);
            } catch (InterruptedException e) {
                e.printStackTrace(System.err);
            }
        }
    }

    @Nullable
    private String generateMessage(Long userId) {
        List<WaterInfo> waterInfoList = usersWaterData.getAll(userId);
        if (waterInfoList == null || waterInfoList.isEmpty()) {
            return null;
        }

        StringBuilder builder = new StringBuilder();
        for (WaterInfo waterInfo : waterInfoList) {
            Date waterDate = waterInfo.getWaterAsDate();
            if (waterDate == null) {
                continue;
            }

            long daysDiff = TimeUnit.DAYS.convert(
                    System.currentTimeMillis() - waterDate.getTime(),
                    TimeUnit.MILLISECONDS
            );

            if (daysDiff >= waterInfo.getDiff()) {
                if (!builder.isEmpty()) {
                    builder.append("\n");
                }
                builder.append(waterInfo.getName())
                        .append(" (дней прошло: ")
                        .append(daysDiff)
                        .append(")");
            }
        }

        if (!builder.isEmpty()) {
            return builder.insert(0, "Нужно полить:\n").toString();
        } else {
            return null;
        }
    }
}
