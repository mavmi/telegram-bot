package mavmi.telegram_bot.hb.sheets.dto;

import lombok.*;

@Getter
@Setter
@Builder
@ToString
@AllArgsConstructor
public class Event {
    private double factor;
    private String name;
    private String groupName;
    private CellPosition cellPosition;
}
