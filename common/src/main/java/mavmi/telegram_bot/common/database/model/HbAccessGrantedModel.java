package mavmi.telegram_bot.common.database.model;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "hb_access_granted")
public class HbAccessGrantedModel {
    @Id
    @Column(name = "id", columnDefinition = "serial")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "telegram_id")
    private Long telegramId;
    @Column(name = "access_granted")
    private Boolean accessGranted;
}
