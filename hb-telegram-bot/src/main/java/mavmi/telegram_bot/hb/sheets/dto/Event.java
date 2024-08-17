package mavmi.telegram_bot.hb.sheets.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class Event {
    private String name;
    private double factor;
    private CellPosition cellPosition;
}
