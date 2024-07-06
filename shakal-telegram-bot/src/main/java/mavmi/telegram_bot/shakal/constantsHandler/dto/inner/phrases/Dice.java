package mavmi.telegram_bot.shakal.constantsHandler.dto.inner.phrases;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Dice {
    @JsonProperty("throw")
    private String doThrow;
    private String quit;
    private String start;
    private String ok;
    private String error;
    private String[] win;
    private String[] draw;
    private String[] lose;

    @JsonIgnore
    public String getRandomWinPhrase() {
        return win[(int)(Math.random() * win.length)];
    }

    @JsonIgnore
    public String getRandomDrawPhrase() {
        return draw[(int)(Math.random() * draw.length)];
    }

    @JsonIgnore
    public String getRandomLosePhrase() {
        return lose[(int)(Math.random() * lose.length)];
    }
}
