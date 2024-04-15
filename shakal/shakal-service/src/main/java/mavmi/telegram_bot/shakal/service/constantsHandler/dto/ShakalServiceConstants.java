package mavmi.telegram_bot.shakal.service.constantsHandler.dto;

import lombok.Getter;
import lombok.Setter;
import mavmi.telegram_bot.common.constantsHandler.dto.Constants;
import mavmi.telegram_bot.shakal.service.constantsHandler.dto.inner.Goose;
import mavmi.telegram_bot.shakal.service.constantsHandler.dto.inner.Phrases;
import mavmi.telegram_bot.shakal.service.constantsHandler.dto.inner.Requests;

@Getter
@Setter
public class ShakalServiceConstants implements Constants {
    private Goose goose;
    private Phrases phrases;
    private Requests requests;
}
