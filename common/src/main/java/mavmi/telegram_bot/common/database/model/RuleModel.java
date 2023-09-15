package mavmi.telegram_bot.common.database.model;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RuleModel {
    private Long userid;
    private Boolean chatGpt;
    private Boolean waterStuff;
    private Boolean crv;
    private Boolean monitoring;
    private Boolean rocket;
}
