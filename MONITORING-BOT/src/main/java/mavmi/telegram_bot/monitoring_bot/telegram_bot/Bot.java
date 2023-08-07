package mavmi.telegram_bot.monitoring_bot.telegram_bot;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.request.SendMessage;
import mavmi.telegram_bot.common.bot.AbsTelegramBot;
import mavmi.telegram_bot.common.database.model.RuleModel;
import mavmi.telegram_bot.common.database.repository.RuleRepository;
import mavmi.telegram_bot.common.logger.Logger;

import java.util.ArrayList;
import java.util.List;

public class Bot extends AbsTelegramBot {
    private final TelegramBot telegramBot;
    private final RuleRepository ruleRepository;

    public Bot(String telegramBotToken, Logger logger, RuleRepository ruleRepository){
        super(logger);
        this.telegramBot = new TelegramBot(telegramBotToken);
        this.ruleRepository = ruleRepository;
    }

    @Override
    public void run() {
        logger.log("MONITORING-BOT IS RUNNING");
        telegramBot.setUpdatesListener(
                updates -> {
                    return UpdatesListener.CONFIRMED_UPDATES_ALL;
                }, e -> {
                    logger.err(e.getMessage());
                }
        );
    }

    public synchronized void sendMsg(String msg){
        for (Long id : getAvailableUsers()){
            telegramBot.execute(new SendMessage(id, msg));
        }
    }

    private List<Long> getAvailableUsers(){
        List<Long> idx = new ArrayList<>();

        for (RuleModel ruleModel : ruleRepository.getAll()){
            if (ruleModel.getMonitoring() != null && ruleModel.getMonitoring()){
                idx.add(ruleModel.getUserid());
            }
        }

        return idx;
    }
}
