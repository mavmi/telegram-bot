package mavmi.telegram_bot.common.database.model;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "hb_note")
public class HbNoteModel {
    @Id
    @Column(name = "id", columnDefinition = "serial")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "edu_username")
    private String eduUsername;
    @Column(name = "event_name")
    private String eventName;
    @Column(name = "grade")
    private Long grade;
    @Column(name = "grade_with_factor")
    private Double gradeWithFactor;
    @Column(name = "cell_position")
    private String cellPosition;
}
