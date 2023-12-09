package mavmi.telegram_bot.common.database.auth;

import mavmi.telegram_bot.common.database.model.RuleModel;
import mavmi.telegram_bot.common.database.repository.RuleRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class UserAuthentication {
    private final RuleRepository ruleRepository;

    public UserAuthentication(RuleRepository ruleRepository){
        this.ruleRepository = ruleRepository;
    }

    public boolean isPrivilegeGranted(Long userId, BotNames botName){
        RuleModel ruleModel = ruleRepository.get(userId);
        if (ruleModel == null) {
            return false;
        }

        return getValue(ruleModel, botName);
    }

    public Map<Long, Boolean> isPrivilegeGranted(List<Long> userIdx, BotNames botName){
        List<RuleModel> ruleModelList = ruleRepository.getAll();

        return ruleModelList
                .stream()
                .filter(ruleModel -> userIdx.contains(ruleModel.getUserid()))
                .collect(Collectors.toMap(RuleModel::getUserid, ruleModel -> getValue(ruleModel, botName)));
    }

    private boolean getValue(RuleModel ruleModel, BotNames botName){
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
        }

        if (value == null) {
            return false;
        }
        return value;
    }
}
