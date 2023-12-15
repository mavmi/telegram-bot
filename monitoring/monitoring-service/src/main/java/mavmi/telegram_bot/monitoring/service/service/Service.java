package mavmi.telegram_bot.monitoring.service.service;

import lombok.extern.slf4j.Slf4j;
import mavmi.telegram_bot.common.database.auth.BotNames;
import mavmi.telegram_bot.common.database.auth.UserAuthentication;
import mavmi.telegram_bot.common.database.model.RuleModel;
import mavmi.telegram_bot.common.database.repository.RuleRepository;
import mavmi.telegram_bot.common.service.cache.ServiceCache;
import mavmi.telegram_bot.common.dto.json.bot.BotRequestJson;
import mavmi.telegram_bot.common.dto.json.bot.inner.BotTaskManagerJson;
import mavmi.telegram_bot.common.dto.json.bot.inner.UserJson;
import mavmi.telegram_bot.common.service.AbsService;
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
    private final UserAuthentication userAuthentication;

    public Service(
            ServiceCache<UserCache> serviceCache,
            HttpClient httpClient,
            RuleRepository ruleRepository,
            UserAuthentication userAuthentication) {
        super(serviceCache);
        this.httpClient = httpClient;
        this.ruleRepository = ruleRepository;
        this.userAuthentication = userAuthentication;
    }

    public int putTask(BotRequestJson botRequestJson) {
        if (botRequestJson == null) {
            log.info("Bad request");
            return HttpStatus.BAD_REQUEST.value();
        }

        BotTaskManagerJson botTaskManagerJson = botRequestJson.getBotTaskManagerJson();
        UserJson userJson = botRequestJson.getUserJson();

        long id = botRequestJson.getChatId();
        String username = userJson.getUsername();
        String firstName = userJson.getFirstName();
        String lastName = userJson.getLastName();

        UserCache userCache = getUserCache(id, username, firstName, lastName);
        if (!userCache.getIsPrivilegeGranted()) {
            log.info("User unauthorized: id {}", id);
            return HttpStatus.UNAUTHORIZED.value();
        }

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

    private UserCache getUserCache(Long chatId, String username, String firstName, String lastName) {
        UserCache userCache = serviceCache.getUser(chatId);

        if (userCache == null) {
            Boolean isPrivilegeGranted = userAuthentication.isPrivilegeGranted(chatId, BotNames.MONITORING_BOT);
            userCache = new UserCache(chatId, username, firstName, lastName, isPrivilegeGranted);
            serviceCache.putUser(userCache);
        }

        return userCache;
    }
}
