package mavmi.telegram_bot.common.database.auth;

import mavmi.telegram_bot.common.database.model.RuleModel;
import mavmi.telegram_bot.common.database.repository.RuleRepository;
import org.springframework.stereotype.Component;

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

        Boolean value = getValue(ruleModel, botName);
        if (value == null) {
            return false;
        }

        return value;
    }

    private Boolean getValue(RuleModel ruleModel, BotNames botName){
        switch (botName){
            case CHAT_GPT_BOT -> {
                return ruleModel.getChatGpt();
            }
            case CRV_BOT -> {
                return ruleModel.getCrv();
            }
            case WATER_STUFF_BOT -> {
                return ruleModel.getWaterStuff();
            }
            case ROCKET_BOT -> {
                return ruleModel.getRocket();
            }
            default -> {
                return false;
            }
        }
    }
}
