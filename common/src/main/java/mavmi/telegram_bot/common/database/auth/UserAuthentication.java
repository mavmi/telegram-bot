package mavmi.telegram_bot.common.database.auth;

import lombok.RequiredArgsConstructor;
import mavmi.telegram_bot.common.database.model.RuleModel;
import mavmi.telegram_bot.common.database.repository.RuleRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Is used to retrieve user's authentication status
 */
@Component
@RequiredArgsConstructor
public class UserAuthentication {

    private final RuleRepository ruleRepository;

    public boolean isPrivilegeGranted(Long userId, BOT_NAME botName){
        return ruleRepository
                .findById(userId)
                .filter(ruleModel -> getValue(ruleModel, botName))
                .isPresent();
    }

    public Map<Long, Boolean> isPrivilegeGranted(List<Long> userIdx, BOT_NAME botName){
        return ruleRepository
                .findAll()
                .stream()
                .filter(ruleModel -> userIdx.contains(ruleModel.getUserid()))
                .collect(Collectors.toMap(RuleModel::getUserid, ruleModel -> getValue(ruleModel, botName)));
    }

    private boolean getValue(RuleModel ruleModel, BOT_NAME botName){
        return switch (botName){
            case WATER_STUFF_BOT -> ruleModel.getWaterStuff();
            case MONITORING_BOT -> ruleModel.getMonitoring();
            case SHAKAL_BOT -> true;
        };
    }
}
