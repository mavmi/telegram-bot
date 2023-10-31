package mavmi.telegram_bot.monitoring_bot.telegram_bot;

import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.request.SendDocument;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import mavmi.telegram_bot.common.bot.AbsTelegramBot;
import mavmi.telegram_bot.common.database.model.RuleModel;
import mavmi.telegram_bot.common.database.repository.RuleRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class Bot extends AbsTelegramBot {
    private final RuleRepository ruleRepository;

    public Bot(
            RuleRepository ruleRepository,
            @Value("${bot.token}") String telegramBotToken
    ){
        super(null, telegramBotToken);
        this.ruleRepository = ruleRepository;
    }

    @Override
    @PostConstruct
    public void run() {
        log.info("MONITORING-BOT IS RUNNING");
        telegramBot.setUpdatesListener(
                updates -> {
                    return UpdatesListener.CONFIRMED_UPDATES_ALL;
                }, e -> {
                    e.printStackTrace(System.err);
                }
        );
    }

    synchronized public void sendMessage(String msg){
        for (Long id : getAvailableUsers()){
            try {
                sendMessage(id, msg);
                log.info("Message sent to id: {}", id);
            } catch (RuntimeException e){
                e.printStackTrace(System.err);
            }
        }
    }

    synchronized public void sendFile(File file){
        for (Long id : getAvailableUsers()){
            try {
                sendRequest(new SendDocument(id, file));
                log.info("File sent to id: {}", id);
            } catch (RuntimeException e){
                e.printStackTrace(System.err);
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
