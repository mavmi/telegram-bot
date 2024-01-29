package mavmi.telegram_bot.monitoring.service.service;

import lombok.extern.slf4j.Slf4j;
import mavmi.telegram_bot.common.database.model.RuleModel;
import mavmi.telegram_bot.common.database.repository.RuleRepository;
import mavmi.telegram_bot.common.dto.json.bot.BotRequestJson;
import mavmi.telegram_bot.common.dto.json.bot.inner.BotTaskManagerJson;
import mavmi.telegram_bot.common.service.AbsService;
import mavmi.telegram_bot.common.service.IMenu;
import mavmi.telegram_bot.common.service.cache.ServiceCache;
import mavmi.telegram_bot.monitoring.service.constants.Buttons;
import mavmi.telegram_bot.monitoring.service.constants.Phrases;
import mavmi.telegram_bot.monitoring.service.constants.Requests;
import mavmi.telegram_bot.monitoring.service.http.HttpClient;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class Service extends AbsService<UserCache> {

    private static final String[] HOST_BUTTONS = new String[] {
            Buttons.MEM_BTN,
            Buttons.RAM_BTN,
            Buttons.USERS_BTN,
            Buttons.BACKUP_BTN,
            Buttons.EXIT_BTN
    };

    private static final String[] APPS_BUTTONS = new String[] {
            Buttons.PK_BTN,
            Buttons.FP_BTN,
            Buttons.GC_BTN,
            Buttons.EXIT_BTN
    };

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

    public int handleRequest(BotRequestJson jsonDto) {
        long chatId = jsonDto.getChatId();
        String username = jsonDto.getUserJson().getUsername();
        String firstName = jsonDto.getUserJson().getFirstName();
        String lastName = jsonDto.getUserJson().getLastName();
        String msg = jsonDto.getUserMessageJson().getTextMessage();

        UserCache userCache = getUserCache(chatId, username, firstName, lastName);
        if (msg == null) {
            log.error("Message is NULL! id: {}", chatId);
            return HttpStatus.BAD_REQUEST.value();
        }

        log.info("Got request. id: {}; username: {}; first name: {}; last name: {}; message: {}",
                userCache.getUserId(),
                userCache.getUsername(),
                userCache.getFirstName(),
                userCache.getLastName(),
                msg
        );

        IMenu userMenu = userCache.getMenu();
        if (userMenu == Menu.MAIN_MENU) {
            switch (msg) {
                case Requests.HOST_REQ -> handleHostReq(userCache);
                case Requests.APPS_REQ -> handleAppsReq(userCache);
                default -> error(userCache);
            }
        } else if (msg.equals(Buttons.EXIT_BTN)) {
            exit(userCache);
        } else {
            BotTaskManagerJson botTaskManagerJson = jsonDto.getBotTaskManagerJson();
            httpClient.sendKeyboard(
                    chatId,
                    Phrases.OK_MSG,
                    (userCache.getMenu() == Menu.HOST) ? HOST_BUTTONS : APPS_BUTTONS
            );
            return httpClient.sendPutTask(
                    chatId,
                    botTaskManagerJson.getTarget(),
                    botTaskManagerJson.getMessage()
            );
        }

        return HttpStatus.OK.value();
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

    private void handleHostReq(UserCache user) {
        user.setMenu(Menu.HOST);
        httpClient.sendKeyboard(
                user.getUserId(),
                Phrases.AVAILABLE_OPTIONS_MSG,
                HOST_BUTTONS
        );
    }

    private void handleAppsReq(UserCache user) {
        user.setMenu(Menu.APPS);
        httpClient.sendKeyboard(
                user.getUserId(),
                Phrases.AVAILABLE_OPTIONS_MSG,
                APPS_BUTTONS
        );
    }

    private void error(UserCache user) {
        httpClient.sendText(
                List.of(user.getUserId()),
                Phrases.ERR_MSG
        );
    }

    private void exit(UserCache user) {
        dropUserInfo(user);
        httpClient.sendText(
                List.of(user.getUserId()),
                Phrases.OK_MSG
        );
    }

    private UserCache getUserCache(Long chatId, String username, String firstName, String lastName) {
        UserCache user = serviceCache.getUser(chatId);

        if (user == null) {
            user = new UserCache(chatId, Menu.MAIN_MENU, username, firstName, lastName);
            serviceCache.putUser(user);
        }

        return user;
    }

    private void dropUserInfo(UserCache user) {
        user.setMenu(Menu.MAIN_MENU);
        user.getLastMessages().clear();
    }
}
