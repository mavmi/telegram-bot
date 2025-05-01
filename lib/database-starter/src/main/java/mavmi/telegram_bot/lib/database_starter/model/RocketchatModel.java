package mavmi.telegram_bot.lib.database_starter.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.Accessors;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@Entity
@Table(name = "rocketchat", schema = "rocketchat_telegram_bot")
public class RocketchatModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "telegram_id")
    private long telegramId;
    @Column(name = "telegram_username")
    private String telegramUsername;
    @Column(name = "telegram_firstname")
    private String telegramFirstname;
    @Column(name = "telegram_lastname")
    private String telegramLastname;
    @Column(name = "rocketchat_username")
    private String rocketchatUsername;
    @Column(name = "rocketchat_password_hash")
    private String rocketchatPasswordHash;
    @Column(name = "rocketchat_token")
    private String rocketchatToken;
    @Column(name = "rocketchat_token_expiry_date")
    private Long rocketchatTokenExpiryDate;
}
