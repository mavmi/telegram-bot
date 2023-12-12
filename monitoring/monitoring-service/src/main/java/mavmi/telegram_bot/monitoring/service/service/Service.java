package mavmi.telegram_bot.monitoring.service.service;

import lombok.extern.slf4j.Slf4j;
import mavmi.telegram_bot.common.database.auth.BotNames;
import mavmi.telegram_bot.common.database.auth.UserAuthentication;
import mavmi.telegram_bot.common.database.model.RuleModel;
import mavmi.telegram_bot.common.database.repository.RuleRepository;
import mavmi.telegram_bot.common.utils.dto.json.bot.BotRequestJson;
import mavmi.telegram_bot.common.utils.dto.json.bot.inner.BotTaskManagerJson;
import mavmi.telegram_bot.common.utils.service.AbsService;
import mavmi.telegram_bot.monitoring.service.http.HttpClient;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class Service extends AbsService {

    private final HttpClient httpClient;
    private final RuleRepository ruleRepository;
    private final UserAuthentication userAuthentication;

    public Service(
            HttpClient httpClient,
            RuleRepository ruleRepository,
            UserAuthentication userAuthentication) {
        super(null);
        this.httpClient = httpClient;
        this.ruleRepository = ruleRepository;
        this.userAuthentication = userAuthentication;
    }

    public int putTask(BotRequestJson botRequestJson) {
        long id = botRequestJson.getChatId();
        BotTaskManagerJson botTaskManagerJson = botRequestJson.getBotTaskManagerJson();

        if (!userAuthentication.isPrivilegeGranted(id, BotNames.MONITORING_BOT)) {
            log.info("User unauthorized: id {}", id);
            return HttpStatus.UNAUTHORIZED.value();
        }
        if (botRequestJson == null) {
            log.info("Bad request: id {}", id);
            return HttpStatus.BAD_REQUEST.value();
        }

        return httpClient.sendPutTask(botTaskManagerJson.getTarget(), botTaskManagerJson.getMessage());
    }

    public List<Long> getAvailableIdx() {
        List<Long> idx = new ArrayList<>();
        List<RuleModel> ruleModelList = ruleRepository.getAll();

        for (RuleModel ruleModel : ruleModelList) {
            Long userId = ruleModel.getUserid();
            Boolean value = ruleModel.getMonitoring();

            if (value != null && value) {
                idx.add(userId);
            }
        }

        return idx;
    }
}
