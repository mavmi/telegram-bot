package mavmi.telegram_bot.water_stuff.service.waterStuff.serviceModule.common;

import lombok.Getter;
import mavmi.telegram_bot.lib.dto.service.common.InlineKeyboardJson;
import mavmi.telegram_bot.lib.dto.service.menu.Menu;
import mavmi.telegram_bot.lib.user_cache_starter.cache.api.UserCaches;
import mavmi.telegram_bot.lib.user_cache_starter.cache.api.inner.MenuContainer;
import mavmi.telegram_bot.water_stuff.cache.WaterDataCache;
import mavmi.telegram_bot.water_stuff.constantsHandler.WaterConstantsHandler;
import mavmi.telegram_bot.water_stuff.constantsHandler.dto.WaterConstants;
import mavmi.telegram_bot.water_stuff.data.water.UsersWaterData;
import mavmi.telegram_bot.water_stuff.data.water.inner.WaterInfo;
import mavmi.telegram_bot.water_stuff.service.dto.waterStuffService.WaterStuffServiceRq;
import mavmi.telegram_bot.water_stuff.service.waterStuff.menu.WaterStuffServiceMenu;
import mavmi.telegram_bot.water_stuff.telegramBot.client.WaterTelegramBotSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Getter
@Component
public class CommonServiceModule {

    private final WaterTelegramBotSender sender;
    private final WaterConstants constants;
    private final UsersWaterData usersWaterData;
    private final String[] manageMenuButtons;
    private final String[] editMenuButtons;

    @Autowired
    private UserCaches userCaches;

    public CommonServiceModule(
            WaterTelegramBotSender sender,
            UsersWaterData usersWaterData,
            WaterConstantsHandler constantsHandler
    ) {
        this.sender = sender;
        this.constants = constantsHandler.get();
        this.usersWaterData = usersWaterData;
        this.manageMenuButtons = new String[] {
                constants.getButtons().getManageGroup().getInfo(),
                constants.getButtons().getManageGroup().getPause(),
                constants.getButtons().getManageGroup().getDoContinue(),
                constants.getButtons().getManageGroup().getWater(),
                constants.getButtons().getManageGroup().getFertilize(),
                constants.getButtons().getManageGroup().getEdit(),
                constants.getButtons().getManageGroup().getRm(),
                constants.getButtons().getCommon().getExit()
        };
        this.editMenuButtons = new String[] {
                constants.getButtons().getManageGroup().getEditGroup().getChangeName(),
                constants.getButtons().getManageGroup().getEditGroup().getChangeDiff(),
                constants.getButtons().getManageGroup().getEditGroup().getChangeWater(),
                constants.getButtons().getManageGroup().getEditGroup().getChangeFertilize(),
                constants.getButtons().getCommon().getExit()
        };
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
        List<WaterInfo> waterInfoList = usersWaterData.getAll(userCaches.getDataCache(WaterDataCache.class).getUserId());
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
        WaterDataCache dataCache = userCaches.getDataCache(WaterDataCache.class);

        dataCache.getMessagesContainer().clearMessages();
        dropUserMenu();
        Menu menu = dataCache.getMenuContainer().getLast();

        if (menu.equals(WaterStuffServiceMenu.MANAGE_GROUP)) {
            sender.sendReplyKeyboard(request.getChatId(), constants.getPhrases().getCommon().getOperationCanceled(), manageMenuButtons);
        } else if (menu.equals(WaterStuffServiceMenu.EDIT)) {
            sender.sendReplyKeyboard(request.getChatId(), constants.getPhrases().getCommon().getOperationCanceled(), editMenuButtons);
        } else {
            sender.sendText(request.getChatId(), constants.getPhrases().getCommon().getOperationCanceled());
        }
    }

    public void error(WaterStuffServiceRq request) {
        sender.sendText(request.getChatId(), constants.getPhrases().getCommon().getError());
    }

    public void sendText(long chatId, String msg) {
        sender.sendText(chatId, msg);
    }

    public void sendTextDeleteKeyboard(long chatId, String msg) {
        sender.sendTextDeleteKeyboard(chatId, msg);
    }

    public void sendReplyKeyboard(long chatId, String msg, String[] keyboardButtons) {
        sender.sendReplyKeyboard(chatId, msg, keyboardButtons);
    }

    public void sendInlineKeyboard(long chatId, String msg, InlineKeyboardJson inlineKeyboardJson, Integer msgId, boolean update) {
        sender.sendInlineKeyboard(chatId, msg, msgId, update, inlineKeyboardJson);
    }

    public void dropUserMenu(Menu menuUntil) {
        MenuContainer menuContainer = userCaches.getDataCache(WaterDataCache.class).getMenuContainer();
        Menu menu = menuContainer.getLast();

        while (!menuUntil.equals(menu)) {
            menuContainer.removeLast();
            menu = menuContainer.getLast();
        }
    }

    public void dropUserMenu() {
        MenuContainer menuContainer = userCaches.getDataCache(WaterDataCache.class).getMenuContainer();
        WaterStuffServiceMenu menu = (WaterStuffServiceMenu) menuContainer.getLast();

        while (menu != null && !menu.isPremier()) {
            menuContainer.removeLast();
            menu = (WaterStuffServiceMenu) menuContainer.getLast();
        }
    }
}
