package mavmi.telegram_bot.shakal.service.constantsHandler.dto.inner;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Goose {
    private String[] gooses;

    @JsonIgnore
    public String getRandomGoose() {
        return gooses[(int)(Math.random() * gooses.length)];
    }
}
