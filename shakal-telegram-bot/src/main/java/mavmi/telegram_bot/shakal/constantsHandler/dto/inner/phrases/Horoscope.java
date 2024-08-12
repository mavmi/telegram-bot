package mavmi.telegram_bot.shakal.constantsHandler.dto.inner.phrases;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class Horoscope {
    private String question;
    private String error;
    private Map<String, String> signs;
}
