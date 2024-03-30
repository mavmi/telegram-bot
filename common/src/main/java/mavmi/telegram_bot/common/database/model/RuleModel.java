package mavmi.telegram_bot.common.database.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "rule")
public class RuleModel {
    @Id
    @Column(name = "userid")
    private Long userid;
    @Column(name = "chat_gpt")
    private Boolean chatGpt;
    @Column(name = "water_stuff")
    private Boolean waterStuff;
    @Column(name = "crv")
    private Boolean crv;
    @Column(name = "monitoring")
    private Boolean monitoring;
    @Column(name = "rocket")
    private Boolean rocket;
}
