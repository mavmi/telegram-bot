package mavmi.telegram_bot.water_stuff_bot.telegram_bot;

import com.pengrad.telegrambot.request.SendMessage;
import mavmi.telegram_bot.common.logger.Logger;
import mavmi.telegram_bot.water_stuff_bot.water.WaterInfo;

public class NotificationThread extends Thread{
    private final static long sleepDuration = 3 * 3600000L;
    private final Bot bot;
    private final long chatId;
    private final Logger logger;

    public NotificationThread(Bot bot, long chatId){
        this.bot = bot;
        this.chatId = chatId;
        this.logger = Logger.getInstance();
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

        for (int i = 0; i < bot.getWaterContainer().size(); i++){
            WaterInfo waterInfo = bot.getWaterContainer().get(i);
            if (waterInfo.getWater().getDate() == null) continue;
            long daysDiff = waterInfo.getWater().daysDiff();
            if (daysDiff >= waterInfo.getDiff()){
                if (builder.length() != 0) builder.append("\n");
                builder.append(waterInfo.getName())
                        .append(" (дней прошло: ")
                        .append(daysDiff)
                        .append(")");
            }
        }

        if (builder.length() != 0) return builder.insert(0, "Нужно полить:\n").toString();
        return null;
    }

}
