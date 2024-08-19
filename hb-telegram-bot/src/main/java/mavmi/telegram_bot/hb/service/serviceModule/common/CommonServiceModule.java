package mavmi.telegram_bot.hb.service.serviceModule.common;

import lombok.Getter;
import mavmi.telegram_bot.common.cache.api.inner.MenuContainer;
import mavmi.telegram_bot.common.cache.impl.CacheComponent;
import mavmi.telegram_bot.common.database.model.HbAccessGrantedModel;
import mavmi.telegram_bot.common.database.repository.*;
import mavmi.telegram_bot.hb.cache.HbDataCache;
import mavmi.telegram_bot.hb.constantsHandler.HbConstantsHandler;
import mavmi.telegram_bot.hb.constantsHandler.dto.HbConstants;
import mavmi.telegram_bot.hb.edu.peerVerifier.PeerVerifier;
import mavmi.telegram_bot.hb.sheets.GoogleSheetsHandler;
import mavmi.telegram_bot.hb.sheets.dto.Event;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

import static mavmi.telegram_bot.hb.service.menu.HbServiceMenu.*;

@Getter
@Component
public class CommonServiceModule {

    private final PeerVerifier peerVerifier;
    private final GoogleSheetsHandler googleSheetsHandler;
    private final HbAccessGrantedRepository accessRepository;
    private final HbParticipantRepository participantRepository;
    private final HbNoteRepository noteRepository;
    private final HbPriseRepository priseRepository;
    private final HbScoreRepository scoreRepository;
    private final HbConstants constants;

    @Value("${service.password}")
    private String servicePassword;
    @Value("${service.fortune-cost}")
    private double fortuneCost;

    @Autowired
    private CacheComponent cacheComponent;

    public CommonServiceModule(
            PeerVerifier peerVerifier,
            GoogleSheetsHandler googleSheetsHandler,
            HbAccessGrantedRepository accessRepository,
            HbParticipantRepository participantRepository,
            HbNoteRepository noteRepository,
            HbPriseRepository priseRepository,
            HbScoreRepository scoreRepository,
            HbConstantsHandler constantsHandler
    ) {
        this.peerVerifier = peerVerifier;
        this.googleSheetsHandler = googleSheetsHandler;
        this.accessRepository = accessRepository;
        this.participantRepository = participantRepository;
        this.noteRepository = noteRepository;
        this.priseRepository = priseRepository;
        this.scoreRepository = scoreRepository;
        this.constants = constantsHandler.get();
    }

    public boolean checkAccess(long chatId) {
        Optional<HbAccessGrantedModel> accessGrantedModel = accessRepository.findByTelegramId(chatId);
        if (accessGrantedModel.isEmpty() || accessGrantedModel.get().getAccessGranted() == null || accessGrantedModel.get().getAccessGranted() == false) {
            return false;
        }

        return true;
    }

    public void dropMenu() {
        MenuContainer menuContainer = cacheComponent.getCacheBucket().getDataCache(HbDataCache.class).getMenuContainer();
        menuContainer.clear();
        menuContainer.add(MAIN_MENU);
    }

    public void previousMenu() {
        MenuContainer menuContainer = cacheComponent.getCacheBucket().getDataCache(HbDataCache.class).getMenuContainer();
        if (!menuContainer.getLast().equals(MAIN_MENU) && !menuContainer.getLast().equals(AUTH) && !menuContainer.getLast().equals(NOTE_USERNAME)) {
            menuContainer.removeLast();
        }
    }

    public String[] prepareGradesKeyboard() {
        String[] grades = constants.getButtons().getGrade();
        String[] buttons = new String[grades.length + 1];
        buttons[0] = constants.getButtons().getMenuBack();
        System.arraycopy(grades, 0, buttons, 1, grades.length);

        return buttons;
    }

    public String[] prepareEventsKeyboardButtons(List<Event> events) {
        String[] buttons = new String[events.size() + 1];
        buttons[0] = constants.getButtons().getMenuBack();
        for (int i = 0; i < events.size(); i++) {
            buttons[i + 1] = events.get(i).getName();
        }

        return buttons;
    }

    public String[] prepareEventGroupsKeyboardButtons() {
        List<String> eventGroupNames = googleSheetsHandler.getAllEvents().getGroupsNames();
        String[] buttons = new String[eventGroupNames.size() + 1];
        buttons[0] = constants.getButtons().getMenuBack();
        for (int i = 0; i < eventGroupNames.size(); i++) {
            buttons[i + 1] = eventGroupNames.get(i);
        }

        return buttons;
    }
}
