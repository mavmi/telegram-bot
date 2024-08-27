package mavmi.telegram_bot.rocketchat.cache.inner.dataCache;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class RocketchatServiceDataCacheMessagesIdsHistory {

    private final List<Integer> messagesIds = new ArrayList<>();

    synchronized public void add(int id) {
        messagesIds.add(id);
    }

    synchronized public int removeLast() {
        int lastId = messagesIds.size() - 1;
        int value = messagesIds.get(lastId);
        messagesIds.remove(lastId);
        return value;
    }

    synchronized public int size() {
        return messagesIds.size();
    }
}
