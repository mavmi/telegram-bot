package mavmi.telegram_bot.lib.user_cache_starter.message.container;

import mavmi.telegram_bot.lib.user_cache_starter.message.container.exception.MessageHistoryContainerException;

import java.util.ArrayList;
import java.util.List;

public class MessageHistoryContainer {

    private final List<String> messagesHistory = new ArrayList<>();

    public void add(String msg) {
        messagesHistory.add(msg);
    }

    public String getLast() {
        if (messagesHistory.isEmpty()) {
            throw new MessageHistoryContainerException("Message history is empty");
        }

        return messagesHistory.getLast();
    }

    public String getLastAndRemove() {
        if (messagesHistory.isEmpty()) {
            throw new MessageHistoryContainerException("Message history is empty");
        }

        return messagesHistory.remove(messagesHistory.size() - 1);
    }

    public int size() {
        return messagesHistory.size();
    }

    public void clear() {
        messagesHistory.clear();
    }
}
