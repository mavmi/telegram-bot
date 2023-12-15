package mavmi.telegram_bot.common.database.model;

import lombok.*;

import java.sql.Date;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WaterStuffModel {
    private Long id;
    private String name;
    private Date water;
    private Date fertilize;
    private Integer diff;
}
