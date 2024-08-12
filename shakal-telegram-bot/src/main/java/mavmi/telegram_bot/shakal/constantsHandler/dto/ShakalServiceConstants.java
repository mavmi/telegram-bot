package mavmi.telegram_bot.shakal.constantsHandler.dto;

import lombok.Getter;
import lombok.Setter;
import mavmi.telegram_bot.common.constantsHandler.api.Constants;
import mavmi.telegram_bot.shakal.constantsHandler.dto.inner.Phrases;
import mavmi.telegram_bot.shakal.constantsHandler.dto.inner.Goose;
import mavmi.telegram_bot.shakal.constantsHandler.dto.inner.Requests;

@Getter
@Setter
public class ShakalServiceConstants implements Constants {
    private Goose goose;
    private Phrases phrases;
    private Requests requests;
}
