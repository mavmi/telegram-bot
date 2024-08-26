package mavmi.telegram_bot.water_stuff.service.water_stuff.serviceModule.common;

import lombok.Getter;
import mavmi.telegram_bot.common.cache.api.inner.MenuContainer;
import mavmi.telegram_bot.common.cache.impl.CacheComponent;
import mavmi.telegram_bot.common.service.dto.common.InlineKeyboardJson;
import mavmi.telegram_bot.common.service.dto.common.MessageJson;
import mavmi.telegram_bot.common.service.dto.common.ReplyKeyboardJson;
import mavmi.telegram_bot.common.service.dto.common.UpdateMessageJson;
import mavmi.telegram_bot.common.service.dto.common.tasks.WATER_STUFF_SERVICE_TASK;
import mavmi.telegram_bot.common.service.menu.Menu;
import mavmi.telegram_bot.water_stuff.cache.WaterStuffServiceDataCache;
import mavmi.telegram_bot.water_stuff.constantsHandler.WaterStuffServiceConstantsHandler;
import mavmi.telegram_bot.water_stuff.constantsHandler.dto.WaterStuffServiceConstants;
import mavmi.telegram_bot.water_stuff.data.water.UsersWaterData;
import mavmi.telegram_bot.water_stuff.data.water.WaterInfo;
import mavmi.telegram_bot.water_stuff.service.dto.waterStuffService.WaterStuffServiceRq;
import mavmi.telegram_bot.water_stuff.service.dto.waterStuffService.WaterStuffServiceRs;
import mavmi.telegram_bot.water_stuff.service.water_stuff.menu.WaterStuffServiceMenu;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Getter
@Component
public class CommonServiceModule {

    private final WaterStuffServiceConstants constants;
    private final UsersWaterData usersWaterData;
    private final String[] manageMenuButtons;
    private final String[] editMenuButtons;

    @Autowired
    private CacheComponent cacheComponent;

    public CommonServiceModule(
            UsersWaterData usersWaterData,
            WaterStuffServiceConstantsHandler constantsHandler
    ) {
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
        List<WaterInfo> waterInfoList = usersWaterData.getAll(cacheComponent.getCacheBucket().getDataCache(WaterStuffServiceDataCache.class).getUserId());
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

    public WaterStuffServiceRs cancel(WaterStuffServiceRq request) {
        WaterStuffServiceDataCache dataCache = cacheComponent.getCacheBucket().getDataCache(WaterStuffServiceDataCache.class);

        dataCache.getMessagesContainer().clearMessages();
        dropMenu();
        Menu menu = dataCache.getMenuContainer().getLast();

        if (menu.equals(WaterStuffServiceMenu.MANAGE_GROUP)) {
            return createSendReplyKeyboardResponse(
                    constants.getPhrases().getOperationCanceled(),
                    manageMenuButtons
            );
        } else if (menu.equals(WaterStuffServiceMenu.EDIT)) {
            return createSendReplyKeyboardResponse(
                    constants.getPhrases().getOperationCanceled(),
                    editMenuButtons
            );
        } else {
            return createSendTextResponse(constants.getPhrases().getOperationCanceled());
        }
    }

    public WaterStuffServiceRs error(WaterStuffServiceRq request) {
        return createSendTextResponse(constants.getPhrases().getError());
    }

    public WaterStuffServiceRs createSendTextResponse(String msg) {
        MessageJson messageJson = MessageJson
                .builder()
                .textMessage(msg)
                .build();

        return WaterStuffServiceRs
                .builder()
                .waterStuffServiceTask(WATER_STUFF_SERVICE_TASK.SEND_TEXT)
                .messageJson(messageJson)
                .build();
    }

    public WaterStuffServiceRs createSendTextDeleteKeyboardResponse(String msg) {
        MessageJson messageJson = MessageJson
                .builder()
                .textMessage(msg)
                .build();

        return WaterStuffServiceRs
                .builder()
                .waterStuffServiceTask(WATER_STUFF_SERVICE_TASK.SEND_TEXT_DELETE_KEYBOARD)
                .messageJson(messageJson)
                .build();
    }

    public WaterStuffServiceRs createEmptyResponse() {
        return WaterStuffServiceRs
                .builder()
                .waterStuffServiceTask(WATER_STUFF_SERVICE_TASK.NONE)
                .build();
    }

    public WaterStuffServiceRs createSendReplyKeyboardResponse(String msg, String[] keyboardButtons) {
        MessageJson messageJson = MessageJson
                .builder()
                .textMessage(msg)
                .build();

        ReplyKeyboardJson replyKeyboardJson = ReplyKeyboardJson
                .builder()
                .keyboardButtons(keyboardButtons)
                .build();

        return WaterStuffServiceRs
                .builder()
                .waterStuffServiceTask(WATER_STUFF_SERVICE_TASK.SEND_REPLY_KEYBOARD)
                .messageJson(messageJson)
                .replyKeyboardJson(replyKeyboardJson)
                .build();
    }

    public WaterStuffServiceRs createSendInlineKeyboardResponse(String message, InlineKeyboardJson inlineKeyboardJson, Integer msgId, boolean update) {
        MessageJson messageJson = MessageJson
                .builder()
                .textMessage(message)
                .build();

        UpdateMessageJson updateMessageJson = UpdateMessageJson
                .builder()
                .messageId(msgId)
                .update(update)
                .build();

        return WaterStuffServiceRs
                .builder()
                .waterStuffServiceTask(WATER_STUFF_SERVICE_TASK.SEND_INLINE_KEYBOARD)
                .messageJson(messageJson)
                .updateMessageJson(updateMessageJson)
                .inlineKeyboardJson(inlineKeyboardJson)
                .build();
    }

    public void dropMenu(Menu menuUntil) {
        MenuContainer menuContainer = cacheComponent.getCacheBucket().getDataCache(WaterStuffServiceDataCache.class).getMenuContainer();
        Menu menu = menuContainer.getLast();

        while (!menuUntil.equals(menu)) {
            menuContainer.removeLast();
            menu = menuContainer.getLast();
        }
    }

    public void dropMenu() {
        MenuContainer menuContainer = cacheComponent.getCacheBucket().getDataCache(WaterStuffServiceDataCache.class).getMenuContainer();
        WaterStuffServiceMenu menu = (WaterStuffServiceMenu) menuContainer.getLast();

        while (menu != null && !menu.isPremier()) {
            menuContainer.removeLast();
            menu = (WaterStuffServiceMenu) menuContainer.getLast();
        }
    }
}
