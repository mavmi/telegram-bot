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
@Entity(name = "\"user\"")
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
