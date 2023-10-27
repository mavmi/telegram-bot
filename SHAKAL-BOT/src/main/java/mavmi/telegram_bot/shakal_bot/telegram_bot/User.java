package mavmi.telegram_bot.shakal_bot.telegram_bot;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import mavmi.telegram_bot.shakal_bot.constants.Levels;

@Setter
@Getter
@Accessors(chain = true)
public class User {
    private int state;
    private int spamCount;
    private int botDice;
    private int userDice;
    private long id;
    private long chatId;
    private String username;
    private String firstName;
    private String lastName;

    public User() {
        this.state = Levels.MAIN_LEVEL;
    }

}
