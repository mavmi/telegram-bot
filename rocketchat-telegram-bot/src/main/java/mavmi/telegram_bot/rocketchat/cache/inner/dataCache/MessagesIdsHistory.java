package mavmi.telegram_bot.rocketchat.cache.inner.dataCache;

import lombok.Getter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
public class MessagesIdsHistory {

    private final Map<Long, List<Integer>> messagesIds = new HashMap<>();

    synchronized public void add(long commandHash, int id) {
        List<Integer> messagesHistory = messagesIds.get(commandHash);
        if (messagesHistory == null) {
            messagesHistory = new ArrayList<>();
            messagesIds.put(commandHash, messagesHistory);
        }

        messagesHistory.add(id);
    }

    synchronized public int removeLast(long commandHash) {
        List<Integer> messagesHistory = messagesIds.get(commandHash);
        if (messagesHistory == null) {
            return 0;
        }

        int lastId = messagesHistory.size() - 1;
        int value = messagesHistory.get(lastId);
        messagesHistory.remove(lastId);

        if (messagesHistory.isEmpty()) {
            messagesIds.remove(commandHash);
        }

        return value;
    }

    synchronized public int size(long commandHash) {
        List<Integer> messagesHistory = messagesIds.get(commandHash);
        if (messagesHistory == null) {
            return 0;
        }

        return messagesHistory.size();
    }
}
