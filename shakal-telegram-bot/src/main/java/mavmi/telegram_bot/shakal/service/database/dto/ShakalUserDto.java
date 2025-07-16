package mavmi.telegram_bot.shakal.service.database.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ShakalUserDto {
    private Long id;
    private Long chatId;
    private String username;
    private String firstName;
    private String lastName;
}
