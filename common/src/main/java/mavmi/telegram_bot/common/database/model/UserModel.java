package mavmi.telegram_bot.common.database.model;

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
@Table(name = "user", schema = "shakal_telegram_bot")
public class UserModel {
    @Id
    @Column(name = "id")
    private Long id;
    @Column(name = "chatid")
    private Long chatId;
    @Column(name = "username")
    private String username;
    @Column(name = "firstname")
    private String firstName;
    @Column(name = "lastname")
    private String lastName;
}
