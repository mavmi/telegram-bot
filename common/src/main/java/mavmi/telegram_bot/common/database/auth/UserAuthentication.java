package mavmi.telegram_bot.common.database.auth;

import lombok.RequiredArgsConstructor;
import mavmi.telegram_bot.common.database.model.RuleModel;
import mavmi.telegram_bot.common.database.repository.RuleRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "database", name = "enabled", havingValue = "true")
public class UserAuthentication {

    private final RuleRepository ruleRepository;

    public boolean isPrivilegeGranted(Long userId, BOT_NAME botName){
        Optional<RuleModel> ruleModelOpt = ruleRepository.findById(userId);

        return ruleModelOpt
                .filter(ruleModel -> getValue(ruleModel, botName))
                .isPresent();
    }

    public Map<Long, Boolean> isPrivilegeGranted(List<Long> userIdx, BOT_NAME botName){
        List<RuleModel> ruleModelList = ruleRepository.findAll();

        return ruleModelList
                .stream()
                .filter(ruleModel -> userIdx.contains(ruleModel.getUserid()))
                .collect(Collectors.toMap(RuleModel::getUserid, ruleModel -> getValue(ruleModel, botName)));
    }

    private boolean getValue(RuleModel ruleModel, BOT_NAME botName){
        return switch (botName){
            case CHAT_GPT_BOT -> ruleModel.getChatGpt();
            case CRV_BOT -> ruleModel.getCrv();
            case WATER_STUFF_BOT -> ruleModel.getWaterStuff();
            case ROCKET_BOT -> ruleModel.getRocket();
            case MONITORING_BOT -> ruleModel.getMonitoring();
            case SHAKAL_BOT -> true;
        };
    }
}
