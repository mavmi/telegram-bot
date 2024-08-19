package mavmi.telegram_bot.hb.edu.peerVerifier;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mavmi.telegram_bot.hb.edu.httpClient.EduHttpClient;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class PeerVerifier {

    private final Set<String> peerUsernames = new HashSet<>();
    private final EduHttpClient eduHttpClient;

    private String accessToken;

    synchronized public boolean verifyPeerName(String name) {
        name = name.toLowerCase();
        if (peerUsernames.contains(name)) {
            return true;
        }

        if (accessToken == null) {
            accessToken = eduHttpClient.auth();
        }

        for (int i = 0; i < 2; i++) {
            int statusCode = eduHttpClient.verifyPeerName(name, accessToken);
            if (statusCode == 200) {
                peerUsernames.add(name);
                return true;
            } else if (statusCode == 401) {
                accessToken = eduHttpClient.auth();
            } else {
                return false;
            }
        }

        log.error("Не смог залогиниться на платформе");
        return false;
    }
}
