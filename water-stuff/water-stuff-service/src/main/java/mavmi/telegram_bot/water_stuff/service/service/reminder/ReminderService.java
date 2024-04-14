package mavmi.telegram_bot.water_stuff.service.service.reminder;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mavmi.telegram_bot.common.database.auth.BOT_NAME;
import mavmi.telegram_bot.common.database.auth.UserAuthentication;
import mavmi.telegram_bot.common.dto.common.MessageJson;
import mavmi.telegram_bot.common.dto.dto.impl.water_stuff.reminder_service.ReminderServiceRs;
import mavmi.telegram_bot.common.dto.dto.impl.water_stuff.reminder_service.inner.ReminderServiceRsElement;
import mavmi.telegram_bot.water_stuff.service.data.water.UsersWaterData;
import mavmi.telegram_bot.water_stuff.service.data.water.WaterInfo;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReminderService {

    private final UserAuthentication userAuthentication;
    private final UsersWaterData usersWaterData;

    public ReminderServiceRs handleRequest() {
        ReminderServiceRs reminderServiceRs = new ReminderServiceRs(new ArrayList<>());
        List<Long> userIdx = usersWaterData.getUsersIdx();
        Map<Long, Boolean> userIdToPrivilege = userAuthentication.isPrivilegeGranted(userIdx, BOT_NAME.WATER_STUFF_BOT);

        for (long chatId : userIdx) {
            Boolean privilege = userIdToPrivilege.get(chatId);
            if (privilege == null) {
                privilege = false;
            }

            if (!privilege) {
                log.info("User {} does not have permission to receive notifications", chatId);
                continue;
            }

            String msg = generateMessage(chatId);
            if (msg != null) {
                MessageJson messageJson = MessageJson
                        .builder()
                        .textMessage(msg)
                        .build();

                ReminderServiceRsElement element = ReminderServiceRsElement
                        .builder()
                        .chatId(chatId)
                        .messageJson(messageJson)
                        .build();

                reminderServiceRs
                        .getReminderServiceRsElements()
                        .add(element);
            }
        }

        return reminderServiceRs;
    }

    @Nullable
    private String generateMessage(Long userId) {
        List<WaterInfo> waterInfoList = usersWaterData.getAll(userId);
        if (waterInfoList == null || waterInfoList.isEmpty()) {
            return null;
        }

        StringBuilder builder = new StringBuilder();
        for (WaterInfo waterInfo : waterInfoList) {
            Long stopNotificationsUntil = waterInfo.getStopNotificationsUntil();
            if (stopNotificationsUntil != null && stopNotificationsUntil > System.currentTimeMillis()) {
                continue;
            }

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
