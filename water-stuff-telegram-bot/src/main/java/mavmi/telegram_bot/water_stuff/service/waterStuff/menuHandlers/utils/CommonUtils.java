package mavmi.telegram_bot.water_stuff.service.waterStuff.menuHandlers.utils;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import mavmi.telegram_bot.lib.database_starter.model.WaterModel;
import mavmi.telegram_bot.lib.dto.service.menu.Menu;
import mavmi.telegram_bot.lib.menu_engine_starter.engine.MenuEngine;
import mavmi.telegram_bot.lib.user_cache_starter.cache.api.UserCaches;
import mavmi.telegram_bot.lib.user_cache_starter.menu.container.MenuHistoryContainer;
import mavmi.telegram_bot.lib.user_cache_starter.provider.UserCachesProvider;
import mavmi.telegram_bot.water_stuff.cache.dto.WaterDataCache;
import mavmi.telegram_bot.water_stuff.constantsHandler.WaterConstantsHandler;
import mavmi.telegram_bot.water_stuff.constantsHandler.dto.WaterConstants;
import mavmi.telegram_bot.water_stuff.data.water.service.WaterDataService;
import mavmi.telegram_bot.water_stuff.service.waterStuff.dto.WaterStuffServiceRq;
import mavmi.telegram_bot.water_stuff.service.waterStuff.menu.WaterStuffServiceMenu;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.Date;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Getter
@Component
@RequiredArgsConstructor
public class CommonUtils {

    private final MenuEngine menuEngine;
    private final TelegramBotUtils telegramBotUtils;
    private final UserCachesProvider userCachesProvider;
    private final WaterDataService waterDataService;

    private WaterConstants constants;

    @Autowired
    public void setup(WaterConstantsHandler constantsHandler) {
        this.constants = constantsHandler.get();
    }

    public UserCaches getUserCaches() {
        return userCachesProvider.get();
    }

    public String getReadableWaterInfo(WaterModel waterModel) {
        StringBuilder builder = new StringBuilder();

        int i = 0;
        long EMPTY = -1;
        long[] daysDiff = new long[2];

        for (Date date : new Date[]{ waterModel.getWaterDate(), waterModel.getFertilizeDate() }) {
            daysDiff[i++] = (date == null) ?
                    EMPTY :
                    TimeUnit.DAYS.convert(
                            System.currentTimeMillis() - date.getTime(),
                            TimeUnit.MILLISECONDS
                    );
        }

        builder.append("***")
                .append("> ")
                .append(waterModel.getName())
                .append("***")
                .append("\n")
                .append("Разница по дням: ")
                .append(waterModel.getDaysDiff())
                .append("\n")
                .append("Полив: ")
                .append(waterModel.getWaterAsString());

        if (daysDiff[0] != EMPTY) {
            builder.append(" (дней прошло: ")
                    .append(daysDiff[0])
                    .append(")");
        }

        builder.append("\n")
                .append("Удобрение: ")
                .append(waterModel.getFertilizeAsString());

        if (daysDiff[1] != EMPTY) {
            builder.append(" (дней прошло: ")
                    .append(daysDiff[1])
                    .append(")");
        }

        return builder.toString();
    }

    public String[] getGroupsNames() {
        List<WaterModel> waterModelList = waterDataService.getAll(getUserCaches().getDataCache(WaterDataCache.class).getUserId());
        if (waterModelList == null) {
            return new String[]{};
        }

        int size = waterModelList.size();
        String[] arr = new String[size];

        int i = 0;
        for (WaterModel waterModel : waterModelList) {
            arr[i++] = waterModel.getName();
        }

        Arrays.sort(arr);

        return arr;
    }

    public List<String> getMenuButtons(Menu menu, long chatId) {
        if (menu == WaterStuffServiceMenu.MANAGE_GROUP) {
            String selectedGroup = getUserCaches()
                    .getDataCache(WaterDataCache.class)
                    .getSelectedGroup();
            WaterModel model = waterDataService.get(chatId, selectedGroup);

            List<String> buttons = menuEngine.getMenuButtonsAsString(menu);
            Long pauseUntil = model.getStopNotificationsUntil();

            if (pauseUntil == null || pauseUntil < System.currentTimeMillis()) {
                buttons.removeIf(str -> str.equals(menuEngine.getMenuButtonByName(WaterStuffServiceMenu.MANAGE_GROUP, "continue").getValue()));
            } else {
                buttons.removeIf(str -> str.equals(menuEngine.getMenuButtonByName(WaterStuffServiceMenu.MANAGE_GROUP, "pause").getValue()));
            }

            return buttons;
        } else {
            return menuEngine.getMenuButtonsAsString(menu);
        }
    }

    public void cancel(WaterStuffServiceRq request) {
        WaterDataCache dataCache = getUserCaches().getDataCache(WaterDataCache.class);

        dataCache.getMessagesContainer().clear();
        dropUserMenu();
        Menu menu = dataCache.getMenuHistoryContainer().getLast();

        if (menu.equals(WaterStuffServiceMenu.MANAGE_GROUP)) {
            telegramBotUtils.sendReplyKeyboard(request.getChatId(),
                    constants.getPhrases().getCommon().getOperationCanceled(),
                    getMenuButtons(WaterStuffServiceMenu.MANAGE_GROUP, request.getChatId()));
        } else if (menu.equals(WaterStuffServiceMenu.EDIT)) {
            telegramBotUtils.sendReplyKeyboard(request.getChatId(),
                    constants.getPhrases().getCommon().getOperationCanceled(),
                    getMenuButtons(WaterStuffServiceMenu.EDIT, request.getChatId()));
        } else {
            telegramBotUtils.sendText(request.getChatId(),
                    constants.getPhrases().getCommon().getOperationCanceled());
        }
    }

    public void error(WaterStuffServiceRq request) {
        telegramBotUtils.sendText(request.getChatId(), constants.getPhrases().getCommon().getError());
    }

    public void dropUserMenu(Menu menuUntil) {
        getUserCaches().getDataCache(WaterDataCache.class)
                .getMenuHistoryContainer()
                .deleteUntil(Menu.class, menuUntil);
    }

    public void dropUserMenu() {
        MenuHistoryContainer menuContainer = getUserCaches().getDataCache(WaterDataCache.class).getMenuHistoryContainer();
        WaterStuffServiceMenu menu = (WaterStuffServiceMenu) menuContainer.getLast();

        while (menu != null && !menu.isPremier()) {
            menuContainer.getLastAndRemove();
            menu = (WaterStuffServiceMenu) menuContainer.getLast();
        }
    }
}
