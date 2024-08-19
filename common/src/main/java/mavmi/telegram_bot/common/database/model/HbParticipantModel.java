package mavmi.telegram_bot.common.database.model;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "hb_participant")
public class HbParticipantModel {
    @Id
    @Column(name = "id", columnDefinition = "serial")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "edu_username")
    private String eduUsername;
    @Column(name = "row")
    private Long row;
}
