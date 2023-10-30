package mavmi.telegram_bot.monitoring_bot.telegram_bot;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.request.BaseRequest;
import com.pengrad.telegrambot.request.SendDocument;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.BaseResponse;
import jakarta.annotation.PostConstruct;
import mavmi.telegram_bot.common.bot.AbsTelegramBot;
import mavmi.telegram_bot.common.database.model.RuleModel;
import mavmi.telegram_bot.common.database.repository.RuleRepository;
import mavmi.telegram_bot.common.logger.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Component
public class Bot extends AbsTelegramBot {
    private final RuleRepository ruleRepository;

    public Bot(
            @Value("${bot.token}") String telegramBotToken,
            Logger logger,
            RuleRepository ruleRepository
    ){
        super(logger, telegramBotToken);
        this.ruleRepository = ruleRepository;
    }

    @Override
    @PostConstruct
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

    synchronized public void sendMessage(String msg){
        for (Long id : getAvailableUsers()){
            try {
                sendMessage(id, msg);
                logger.log("Message sent to " + id);
            } catch (RuntimeException e){
                logger.err(e.getMessage());
            }
        }
    }

    synchronized public void sendFile(File file){
        for (Long id : getAvailableUsers()){
            try {
                sendRequest(new SendDocument(id, file));
                logger.log("File sent to " + id);
            } catch (RuntimeException e){
                logger.err(e.getMessage());
            }
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
