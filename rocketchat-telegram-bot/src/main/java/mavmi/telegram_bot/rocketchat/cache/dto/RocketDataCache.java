package mavmi.telegram_bot.rocketchat.cache.dto;

import lombok.Getter;
import lombok.Setter;
import mavmi.telegram_bot.lib.user_cache_starter.cache.api.DataCache;
import mavmi.telegram_bot.rocketchat.cache.dto.inner.dataCache.Commands;
import mavmi.telegram_bot.rocketchat.cache.dto.inner.dataCache.Creds;
import mavmi.telegram_bot.rocketchat.cache.dto.inner.dataCache.MessagesToDelete;
import mavmi.telegram_bot.rocketchat.cache.dto.inner.dataCache.command.Command;
import mavmi.telegram_bot.rocketchat.service.rocketchat.menu.RocketMenu;

@Getter
@Setter
public class RocketDataCache extends DataCache {

    private final Creds creds = new Creds();
    private final Commands commands = new Commands();
    private final MessagesToDelete messagesToDelete = new MessagesToDelete();

    private Long activeCommandHash;
    private Integer lastQrMsgId;

    public RocketDataCache(Long userId) {
        super(userId, RocketMenu.MAIN_MENU);
    }

    public void setRocketchatUsername(String username) {
        creds.setRocketchatUsername(username);
    }

    public String getRocketchatUsername() {
        return creds.getRocketchatUsername();
    }

    public void setRocketchatPasswordHash(String passwordHash) {
        creds.setRocketchatPasswordHash(passwordHash);
    }

    public String getRocketchatPasswordHash() {
        return creds.getRocketchatPasswordHash();
    }

    public void setRocketchatToken(String token) {
        creds.setRocketchatToken(token);
    }

    public String getRocketchatToken() {
        return creds.getRocketchatToken();
    }

    public void setRocketchatTokenExpiryDate(Long date) {
        creds.setRocketchatTokenExpiryDate(date);
    }

    public Long getRocketchatTokenExpiryDate() {
        return creds.getRocketchatTokenExpiryDate();
    }

    public void putCommand(String commandName, long timestampMillis) {
        commands.putCommand(commandName, timestampMillis);
    }

    public Command getCommandByName(String commandName) {
        return commands.getCommandByName(commandName);
    }

    public void addMessageToDelete(int msgId) {
        messagesToDelete.add(msgId);
    }

    public int removeCommandToDelete() {
        return messagesToDelete.remove();
    }

    public int messagesToDeleteSize() {
        return messagesToDelete.size();
    }
}
