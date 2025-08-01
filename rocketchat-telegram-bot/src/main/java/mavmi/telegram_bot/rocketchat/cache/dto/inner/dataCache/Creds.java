package mavmi.telegram_bot.rocketchat.cache.dto.inner.dataCache;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Creds {

    private String rocketchatUsername;
    private String rocketchatPasswordHash;
    private String rocketchatToken;

    public void reset() {
        rocketchatUsername = null;
        rocketchatPasswordHash = null;
        rocketchatToken = null;
    }
}
