package mavmi.telegram_bot.rocket_bot.telegram_bot;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LocalUser {
    private long chatId;
    private int menuLevel;
}
