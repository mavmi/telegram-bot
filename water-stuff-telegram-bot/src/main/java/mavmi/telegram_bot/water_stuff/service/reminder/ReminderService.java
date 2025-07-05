package mavmi.telegram_bot.water_stuff.service.reminder;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mavmi.telegram_bot.lib.database_starter.api.BOT_NAME;
import mavmi.telegram_bot.lib.database_starter.auth.UserAuthentication;
import mavmi.telegram_bot.lib.database_starter.model.WaterModel;
import mavmi.telegram_bot.lib.dto.service.common.MessageJson;
import mavmi.telegram_bot.water_stuff.data.water.service.WaterDataService;
import mavmi.telegram_bot.water_stuff.service.reminder.dto.ReminderServiceRs;
import mavmi.telegram_bot.water_stuff.service.reminder.dto.inner.ReminderServiceRsElement;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReminderService {

    private final UserAuthentication userAuthentication;
    private final WaterDataService waterDataService;

    public ReminderServiceRs handleRequest() {
        ReminderServiceRs reminderServiceRs = new ReminderServiceRs(new ArrayList<>());
        List<Long> userIdx = waterDataService.getUsersIdx();
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
        List<WaterModel> waterModelList = waterDataService.getAll(userId);
        if (waterModelList == null || waterModelList.isEmpty()) {
            return null;
        }

        StringBuilder builder = new StringBuilder();
        for (WaterModel waterModel : waterModelList) {
            Long stopNotificationsUntil = waterModel.getStopNotificationsUntil();
            if (stopNotificationsUntil != null && stopNotificationsUntil > System.currentTimeMillis()) {
                continue;
            }

            Date waterDate = waterModel.getWaterDate();
            if (waterDate == null) {
                continue;
            }

            long daysDiff = TimeUnit.DAYS.convert(
                    System.currentTimeMillis() - waterDate.getTime(),
                    TimeUnit.MILLISECONDS
            );

            if (daysDiff >= waterModel.getDaysDiff()) {
                if (!builder.isEmpty()) {
                    builder.append("\n");
                }
                builder.append(waterModel.getName())
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
