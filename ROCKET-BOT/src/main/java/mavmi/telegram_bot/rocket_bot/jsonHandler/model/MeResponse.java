package mavmi.telegram_bot.rocket_bot.jsonHandler.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class MeResponse {
    private String id;
    private String username;
    private String email;
    private String name;
    private String statusText;
    private String statusConnection;

    @Override
    public String toString() {
        return
                "Username: " + username + "\n" +
                "Email: " + email + "\n" +
                "Имя: " + name + "\n" +
                "Статус профиля: " + statusText + "\n" +
                "Статус подключения: " + statusConnection;
    }
}
