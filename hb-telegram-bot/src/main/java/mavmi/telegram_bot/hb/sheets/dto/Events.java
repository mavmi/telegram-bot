package mavmi.telegram_bot.hb.sheets.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class Events {
    private List<Event> events;
}
