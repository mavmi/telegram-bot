package mavmi.telegram_bot.water_stuff_bot.telegram_bot;

import com.pengrad.telegrambot.request.SendMessage;
import mavmi.telegram_bot.common.database.model.WaterStuffModel;
import mavmi.telegram_bot.common.database.repository.WaterStuffRepository;
import mavmi.telegram_bot.common.logger.Logger;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class NotificationThread extends Thread{
    private final static long sleepDuration = 3 * 3600000L;
    private final Bot bot;
    private final WaterStuffRepository waterStuffRepository;
    private final long chatId;
    private final Logger logger;

    public NotificationThread(Bot bot, WaterStuffRepository waterStuffRepository, Logger logger, long chatId){
        this.bot = bot;
        this.waterStuffRepository = waterStuffRepository;
        this.chatId = chatId;
        this.logger = logger;
    }

    @Override
    public void run() {
        while (true) {
            try {
                String msg = generateMessage();
                if (msg != null){
                    bot.sendMsg(new SendMessage(chatId, msg));
                    logger.log("Message sent");
                } else {
                    logger.log("Message is null");
                }
                sleep(sleepDuration);
            } catch (InterruptedException e) {
                logger.err(e.getMessage());
            }
        }
    }

    private String generateMessage(){
        StringBuilder builder = new StringBuilder();

        for (WaterStuffModel waterStuffModel : waterStuffRepository.getAll()){
            if (waterStuffModel.getWater() == null) continue;
            long daysDiff = ChronoUnit.DAYS.between(waterStuffModel.getWater().toLocalDate(), LocalDate.now());
            if (daysDiff >= waterStuffModel.getDiff()){
                if (builder.length() != 0) builder.append("\n");
                builder.append(waterStuffModel.getName())
                        .append(" (дней прошло: ")
                        .append(daysDiff)
                        .append(")");
            }
        }

        if (builder.length() != 0) return builder.insert(0, "Нужно полить:\n").toString();
        return null;
    }

}
