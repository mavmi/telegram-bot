package mavmi.telegram_bot.rocketchat.cache.dto.inner.dataCache;

import java.util.ArrayList;
import java.util.List;

public class MessagesToDelete {

    private final List<Integer> messagesIds = new ArrayList<>();

    public void add(int msgId) {
        messagesIds.add(msgId);
    }

    public int remove() {
        return messagesIds.removeFirst();
    }

    public int size() {
        return messagesIds.size();
    }
}
