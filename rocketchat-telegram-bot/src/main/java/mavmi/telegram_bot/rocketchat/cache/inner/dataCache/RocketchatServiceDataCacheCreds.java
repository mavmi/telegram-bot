package mavmi.telegram_bot.rocketchat.cache.inner.dataCache;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RocketchatServiceDataCacheCreds {
    private String username;
    private String password;
}
