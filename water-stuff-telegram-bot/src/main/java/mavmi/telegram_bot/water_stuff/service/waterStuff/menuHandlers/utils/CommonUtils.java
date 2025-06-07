package mavmi.telegram_bot.water_stuff.service.waterStuff.menuHandlers.utils;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import mavmi.telegram_bot.lib.dto.service.menu.Menu;
import mavmi.telegram_bot.lib.menu_engine_starter.engine.MenuEngine;
import mavmi.telegram_bot.lib.user_cache_starter.menu.container.MenuHistoryContainer;
import mavmi.telegram_bot.lib.user_cache_starter.cache.api.UserCaches;
import mavmi.telegram_bot.lib.user_cache_starter.provider.UserCachesProvider;
import mavmi.telegram_bot.water_stuff.cache.dto.WaterDataCache;
import mavmi.telegram_bot.water_stuff.constantsHandler.WaterConstantsHandler;
import mavmi.telegram_bot.water_stuff.constantsHandler.dto.WaterConstants;
import mavmi.telegram_bot.water_stuff.data.water.UsersWaterData;
import mavmi.telegram_bot.water_stuff.data.water.inner.WaterInfo;
import mavmi.telegram_bot.water_stuff.service.waterStuff.dto.WaterStuffServiceRq;
import mavmi.telegram_bot.water_stuff.service.waterStuff.menu.WaterStuffServiceMenu;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Getter
@Component
@RequiredArgsConstructor
public class CommonUtils {

    private final MenuEngine menuEngine;
    private final TelegramBotUtils telegramBotUtils;
    private final UserCachesProvider userCachesProvider;
    private final UsersWaterData usersWaterData;

    private WaterConstants constants;

    @Autowired
    public void setup(WaterConstantsHandler constantsHandler) {
        this.constants = constantsHandler.get();
    }

    public UserCaches getUserCaches() {
        return userCachesProvider.get();
    }

    public String getReadableWaterInfo(WaterInfo waterInfo) {
        StringBuilder builder = new StringBuilder();

        int i = 0;
        long EMPTY = -1;
        long[] daysDiff = new long[2];

        for (java.util.Date date : new java.util.Date[]{ waterInfo.getWater(), waterInfo.getFertilize() }) {
            daysDiff[i++] = (date == null) ?
                    EMPTY :
                    TimeUnit.DAYS.convert(
                            System.currentTimeMillis() - date.getTime(),
                            TimeUnit.MILLISECONDS
                    );
        }

        builder.append("***")
                .append("> ")
                .append(waterInfo.getName())
                .append("***")
                .append("\n")
                .append("Разница по дням: ")
                .append(waterInfo.getDiff())
                .append("\n")
                .append("Полив: ")
                .append(waterInfo.getWaterAsString());

        if (daysDiff[0] != EMPTY) {
            builder.append(" (дней прошло: ")
                    .append(daysDiff[0])
                    .append(")");
        }

        builder.append("\n")
                .append("Удобрение: ")
                .append(waterInfo.getFertilizeAsString());

        if (daysDiff[1] != EMPTY) {
            builder.append(" (дней прошло: ")
                    .append(daysDiff[1])
                    .append(")");
        }

        return builder.toString();
    }

    public String[] getGroupsNames() {
        List<WaterInfo> waterInfoList = usersWaterData.getAll(getUserCaches().getDataCache(WaterDataCache.class).getUserId());
        if (waterInfoList == null) {
            return new String[]{};
        }

        int size = waterInfoList.size();
        String[] arr = new String[size];

        int i = 0;
        for (WaterInfo waterInfo : waterInfoList) {
            arr[i++] = waterInfo.getName();
        }

        return arr;
    }

    public void cancel(WaterStuffServiceRq request) {
        WaterDataCache dataCache = getUserCaches().getDataCache(WaterDataCache.class);

        dataCache.getMessagesContainer().clear();
        dropUserMenu();
        Menu menu = dataCache.getMenuHistoryContainer().getLast();

        if (menu.equals(WaterStuffServiceMenu.MANAGE_GROUP)) {
            telegramBotUtils.sendReplyKeyboard(request.getChatId(),
                    constants.getPhrases().getCommon().getOperationCanceled(),
                    menuEngine.getMenuButtonsAsString(WaterStuffServiceMenu.MANAGE_GROUP));
        } else if (menu.equals(WaterStuffServiceMenu.EDIT)) {
            telegramBotUtils.sendReplyKeyboard(request.getChatId(),
                    constants.getPhrases().getCommon().getOperationCanceled(),
                    menuEngine.getMenuButtonsAsString(WaterStuffServiceMenu.EDIT));
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
