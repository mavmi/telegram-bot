package mavmi.telegram_bot.water_stuff.service.service.notificator;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import mavmi.telegram_bot.common.database.auth.BotNames;
import mavmi.telegram_bot.common.database.auth.UserAuthentication;
import mavmi.telegram_bot.water_stuff.service.data.pause.UsersPauseNotificationsData;
import mavmi.telegram_bot.water_stuff.service.data.water.UsersWaterData;
import mavmi.telegram_bot.water_stuff.service.data.water.WaterInfo;
import mavmi.telegram_bot.water_stuff.service.http.HttpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class NotificationThread extends Thread {

    private final UserAuthentication userAuthentication;
    private final UsersWaterData usersWaterData;
    private final UsersPauseNotificationsData usersPauseNotificationsData;
    private final HttpClient httpClient;

    private final long sleepTime;


    public NotificationThread(
            UserAuthentication userAuthentication,
            UsersWaterData usersWaterData,
            UsersPauseNotificationsData usersPauseNotificationsData,
            HttpClient httpClient,
            @Value("${service.sleep-time}") Long sleepTime
    ) {
        this.userAuthentication = userAuthentication;
        this.usersWaterData = usersWaterData;
        this.usersPauseNotificationsData = usersPauseNotificationsData;
        this.httpClient = httpClient;
        this.sleepTime = sleepTime;
    }

    @PostConstruct
    public void init() {
        this.start();
    }

    @Override
    public void run() {
        while (true) {
            try {
                List<Long> userIdx = usersWaterData.getUsersIdx();
                Map<Long, Boolean> userIdToPrivilege = userAuthentication.isPrivilegeGranted(userIdx, BotNames.WATER_STUFF_BOT);

                for (long id : userIdx) {
                    Boolean privilege = userIdToPrivilege.get(id);
                    if (privilege == null) {
                        privilege = false;
                    }

                    if (!privilege) {
                        log.info("User {} does not have permission to receive notifications", id);
                        continue;
                    }

                    String msg = generateMessage(id);
                    if (msg != null) {
                        Long userPauseValue = usersPauseNotificationsData.get(id);
                        if (userPauseValue != null && System.currentTimeMillis() < userPauseValue) {
                            log.info("User paused notifications; id {}", id);
                        } else {
                            httpClient.sendText(id, msg);
                            log.info("Message sent to id: {}", id);
                        }
                    } else {
                        log.debug("Message is null; id {}", id);
                    }
                }
                sleep(sleepTime);
            } catch (InterruptedException e) {
                e.printStackTrace(System.out);
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
            Date waterDate = waterInfo.getWater();
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