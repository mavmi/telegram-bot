package mavmi.telegram_bot.lib.database_starter.model;

import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "websocket", schema = "logs")
public class LogsWebsocketModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "chatid")
    private Long chatid;
    @Column(name = "timestamp")
    private Timestamp timestamp;
    @Column(name = "message")
    private String message;
}
