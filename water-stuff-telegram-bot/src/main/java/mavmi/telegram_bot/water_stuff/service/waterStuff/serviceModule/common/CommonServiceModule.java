package mavmi.telegram_bot.water_stuff.service.waterStuff.serviceModule.common;

import lombok.Getter;
import mavmi.telegram_bot.common.cache.api.inner.MenuContainer;
import mavmi.telegram_bot.common.cache.impl.CacheComponent;
import mavmi.telegram_bot.common.service.dto.common.InlineKeyboardJson;
import mavmi.telegram_bot.common.service.menu.Menu;
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
    private CacheComponent cacheComponent;

    public CommonServiceModule(
            WaterTelegramBotSender sender,
            UsersWaterData usersWaterData,
            WaterConstantsHandler constantsHandler
    ) {
        this.sender = sender;
        this.constants = constantsHandler.get();
        this.usersWaterData = usersWaterData;
        this.manageMenuButtons = new String[] {
                constants.getButtons().getInfo(),
                constants.getButtons().getPause(),
                constants.getButtons().getDoContinue(),
                constants.getButtons().getWater(),
                constants.getButtons().getFertilize(),
                constants.getButtons().getEdit(),
                constants.getButtons().getRm(),
                constants.getButtons().getExit()
        };
        this.editMenuButtons = new String[] {
                constants.getButtons().getChangeName(),
                constants.getButtons().getChangeDiff(),
                constants.getButtons().getChangeWater(),
                constants.getButtons().getChangeFertilize(),
                constants.getButtons().getExit()
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
        List<WaterInfo> waterInfoList = usersWaterData.getAll(cacheComponent.getCacheBucket().getDataCache(WaterDataCache.class).getUserId());
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
        WaterDataCache dataCache = cacheComponent.getCacheBucket().getDataCache(WaterDataCache.class);

        dataCache.getMessagesContainer().clearMessages();
        dropMenu();
        Menu menu = dataCache.getMenuContainer().getLast();

        if (menu.equals(WaterStuffServiceMenu.MANAGE_GROUP)) {
            sender.sendReplyKeyboard(request.getChatId(), constants.getPhrases().getOperationCanceled(), manageMenuButtons);
        } else if (menu.equals(WaterStuffServiceMenu.EDIT)) {
            sender.sendReplyKeyboard(request.getChatId(), constants.getPhrases().getOperationCanceled(), editMenuButtons);
        } else {
            sender.sendText(request.getChatId(), constants.getPhrases().getOperationCanceled());
        }
    }

    public void error(WaterStuffServiceRq request) {
        sender.sendText(request.getChatId(), constants.getPhrases().getError());
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

    public void dropMenu(Menu menuUntil) {
        MenuContainer menuContainer = cacheComponent.getCacheBucket().getDataCache(WaterDataCache.class).getMenuContainer();
        Menu menu = menuContainer.getLast();

        while (!menuUntil.equals(menu)) {
            menuContainer.removeLast();
            menu = menuContainer.getLast();
        }
    }

    public void dropMenu() {
        MenuContainer menuContainer = cacheComponent.getCacheBucket().getDataCache(WaterDataCache.class).getMenuContainer();
        WaterStuffServiceMenu menu = (WaterStuffServiceMenu) menuContainer.getLast();

        while (menu != null && !menu.isPremier()) {
            menuContainer.removeLast();
            menu = (WaterStuffServiceMenu) menuContainer.getLast();
        }
    }
}
