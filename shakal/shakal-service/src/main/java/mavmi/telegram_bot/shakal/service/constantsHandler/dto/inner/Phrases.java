package mavmi.telegram_bot.shakal.service.constantsHandler.dto.inner;

import lombok.Getter;
import lombok.Setter;
import mavmi.telegram_bot.shakal.service.constantsHandler.dto.inner.phrases.Common;
import mavmi.telegram_bot.shakal.service.constantsHandler.dto.inner.phrases.Dice;
import mavmi.telegram_bot.shakal.service.constantsHandler.dto.inner.phrases.Horoscope;

@Getter
@Setter
public class Phrases {
    private Common common;
    private Dice dice;
    private Horoscope horoscope;
}
