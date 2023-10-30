package mavmi.telegram_bot.water_stuff_bot.telegram_bot;

import lombok.Setter;
import mavmi.telegram_bot.common.database.model.RuleModel;
import mavmi.telegram_bot.common.database.repository.RuleRepository;
import mavmi.telegram_bot.common.logger.Logger;
import mavmi.telegram_bot.water_stuff_bot.data.WaterContainer;
import mavmi.telegram_bot.water_stuff_bot.data.WaterInfo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Component
public class NotificationThread extends Thread {
    private final long sleepTime;
    private final Logger logger;
    private final WaterContainer waterContainer;
    private final RuleRepository ruleRepository;
    private final List<Long> idx;

    @Setter
    private Bot telegramBot;

    public NotificationThread(
            Logger logger,
            WaterContainer waterContainer,
            RuleRepository ruleRepository,
            @Value("${bot.sleep-time}") Long sleepTime
    ) {
        this.logger = logger;
        this.waterContainer = waterContainer;
        this.ruleRepository = ruleRepository;
        this.idx = getIdx();
        this.sleepTime = sleepTime;
    }

    @Override
    public void run() {
        while (true) {
            try {
                String msg = generateMessage();
                if (msg != null) {
                    for (long id : idx) {
                        telegramBot.sendMessage(id, msg);
                    }
                    logger.log("Message sent");
                } else {
                    logger.log("Message is null");
                }
                sleep(sleepTime);
            } catch (InterruptedException e) {
                logger.err(e.getMessage());
            }
        }
    }

    private String generateMessage() {
        StringBuilder builder = new StringBuilder();

        for (Map.Entry<String, WaterInfo> entry : waterContainer.entrySet()) {
            WaterInfo waterInfo = entry.getValue();
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
        }
        return null;
    }

    private List<Long> getIdx() {
        List<Long> idx = new ArrayList<>();

        for (RuleModel ruleModel : ruleRepository.getAll()) {
            if (ruleModel.getWaterStuff() != null && ruleModel.getWaterStuff()) {
                idx.add(ruleModel.getUserid());
            }
        }

        return idx;
    }
}
