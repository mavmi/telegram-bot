package mavmi.telegram_bot.shakal.constantsHandler.dto.inner.phrases;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Goose {
    private String[] goose;

    @JsonIgnore
    public String getRandomGoose() {
        return goose[(int)(Math.random() * goose.length)];
    }
}
