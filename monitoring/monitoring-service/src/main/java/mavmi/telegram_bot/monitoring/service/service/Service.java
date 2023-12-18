package mavmi.telegram_bot.monitoring.service.service;

import lombok.extern.slf4j.Slf4j;
import mavmi.telegram_bot.common.database.model.RuleModel;
import mavmi.telegram_bot.common.database.repository.RuleRepository;
import mavmi.telegram_bot.common.dto.json.bot.BotRequestJson;
import mavmi.telegram_bot.common.dto.json.bot.inner.BotTaskManagerJson;
import mavmi.telegram_bot.common.service.AbsService;
import mavmi.telegram_bot.common.service.cache.ServiceCache;
import mavmi.telegram_bot.monitoring.service.http.HttpClient;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class Service extends AbsService<UserCache> {

    private final HttpClient httpClient;
    private final RuleRepository ruleRepository;

    public Service(
            ServiceCache<UserCache> serviceCache,
            HttpClient httpClient,
            RuleRepository ruleRepository
    ) {
        super(serviceCache);
        this.httpClient = httpClient;
        this.ruleRepository = ruleRepository;
    }

    public int putTask(BotRequestJson botRequestJson) {
        if (botRequestJson == null) {
            log.info("Bad request");
            return HttpStatus.BAD_REQUEST.value();
        }

        BotTaskManagerJson botTaskManagerJson = botRequestJson.getBotTaskManagerJson();
        long id = botRequestJson.getChatId();

        return httpClient.sendPutTask(
                id,
                botTaskManagerJson.getTarget(),
                botTaskManagerJson.getMessage()
        );
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
