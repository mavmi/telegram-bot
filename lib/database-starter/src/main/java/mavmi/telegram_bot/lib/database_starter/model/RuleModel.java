package mavmi.telegram_bot.lib.database_starter.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "rule", schema = "common")
public class RuleModel {
    @Id
    @Column(name = "userid")
    private Long userid;
    @Column(name = "water_stuff")
    private Boolean waterStuff;
    @Column(name = "monitoring")
    private Boolean monitoring;
}
