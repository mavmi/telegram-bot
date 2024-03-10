package mavmi.telegram_bot.common.database.auth;

import mavmi.telegram_bot.common.database.model.RuleModel;
import mavmi.telegram_bot.common.database.repository.RuleRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Lazy
@Component
@ConditionalOnBean(RuleRepository.class)
public class UserAuthentication {
    private final RuleRepository ruleRepository;

    public UserAuthentication(RuleRepository ruleRepository){
        this.ruleRepository = ruleRepository;
    }

    public boolean isPrivilegeGranted(Long userId, BOT_NAME botName){
        RuleModel ruleModel = ruleRepository.get(userId);
        if (ruleModel == null) {
            return false;
        }

        return getValue(ruleModel, botName);
    }

    public Map<Long, Boolean> isPrivilegeGranted(List<Long> userIdx, BOT_NAME botName){
        List<RuleModel> ruleModelList = ruleRepository.getAll();

        return ruleModelList
                .stream()
                .filter(ruleModel -> userIdx.contains(ruleModel.getUserid()))
                .collect(Collectors.toMap(RuleModel::getUserid, ruleModel -> getValue(ruleModel, botName)));
    }

    private boolean getValue(RuleModel ruleModel, BOT_NAME botName){
        Boolean value = false;

        switch (botName){
            case CHAT_GPT_BOT -> {
                value = ruleModel.getChatGpt();
                break;
            }
            case CRV_BOT -> {
                value = ruleModel.getCrv();
                break;
            }
            case WATER_STUFF_BOT -> {
                value = ruleModel.getWaterStuff();
                break;
            }
            case ROCKET_BOT -> {
                value = ruleModel.getRocket();
                break;
            }
            case MONITORING_BOT -> {
                value = ruleModel.getMonitoring();
                break;
            }
            case SHAKAL_BOT -> {
                value = true;
                break;
            }
        }

        if (value == null) {
            return false;
        }
        return value;
    }
}
