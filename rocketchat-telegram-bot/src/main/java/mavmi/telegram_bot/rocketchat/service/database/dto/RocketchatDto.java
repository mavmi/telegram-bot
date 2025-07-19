package mavmi.telegram_bot.rocketchat.service.database.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Builder
@Accessors(chain = true)
public class RocketchatDto {
    private long telegramId;
    private String telegramUsername;
    private String telegramFirstname;
    private String telegramLastname;
    private String rocketchatUsername;
    private String rocketchatPasswordHash;
    private String rocketchatToken;
    private Integer lastQrMsgId;
}
