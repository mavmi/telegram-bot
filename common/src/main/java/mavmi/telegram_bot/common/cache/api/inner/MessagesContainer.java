package mavmi.telegram_bot.common.cache.api.inner;

import org.springframework.lang.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Part of {@link mavmi.telegram_bot.common.cache.api.DataCache}
 * Stores user's requests history
 */
public class MessagesContainer {

    private final List<String> messagesHistory = new ArrayList<>();

    @Nullable
    public String getLastMessage() {
        int size = messagesHistory.size();
        if (size == 0) {
            return null;
        }

        return messagesHistory.get(size - 1);
    }

    public void removeLastMessage() {
        int size = messagesHistory.size();
        if (size != 0) {
            messagesHistory.remove(size - 1);
        }
    }

    public void addMessage(String msg) {
        messagesHistory.add(msg);
    }

    public void clearMessages() {
        messagesHistory.clear();
    }
}
