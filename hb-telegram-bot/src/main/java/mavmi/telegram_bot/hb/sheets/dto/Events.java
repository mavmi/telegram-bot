package mavmi.telegram_bot.hb.sheets.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class Events {

    private List<Event> eventsList = new ArrayList<>();
    private Map<String, Event> eventsMap = new HashMap<>();
    private Map<String, List<Event>> groupsMap = new HashMap<>();

    public void addEvent(Event event) {
        eventsList.add(event);
        eventsMap.put(event.getName(), event);

        String groupName = event.getGroupName();
        List<Event> group = groupsMap.get(groupName);
        if (group == null) {
            group = new ArrayList<>();
            groupsMap.put(groupName, group);
        }
        group.add(event);
    }

    public List<Event> getEvents() {
        return eventsList;
    }

    public Event getEventByName(String name) {
        return eventsMap.get(name);
    }

    public List<String> getGroupsNames() {
        return groupsMap.keySet().stream().toList();
    }

    public List<Event> getEventsByGroupName(String groupName) {
        return groupsMap.get(groupName);
    }
}
