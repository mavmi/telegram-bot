package mavmi.telegram_bot.shakal.constantsHandler.dto;

import lombok.Getter;
import lombok.Setter;
import mavmi.telegram_bot.lib.constants_handler.api.Constants;
import mavmi.telegram_bot.shakal.constantsHandler.dto.inner.Phrases;
import mavmi.telegram_bot.shakal.constantsHandler.dto.inner.Goose;
import mavmi.telegram_bot.shakal.constantsHandler.dto.inner.Requests;

/**
 * Container of bot's constants
 */
@Getter
@Setter
public class ShakalConstants implements Constants {
    private Goose goose;
    private Phrases phrases;
    private Requests requests;
}
