package mavmi.telegram_bot.rocketchat.crypto;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.encrypt.Encryptors;
import org.springframework.security.crypto.encrypt.TextEncryptor;

@Configuration
public class Config {

    @Value("${crypto.password}")
    private String password;
    @Value("${crypto.salt}")
    private String salt;

    @Bean("rocketChatTextEncryptor")
    public TextEncryptor getTextEncryptor() {
        return Encryptors.delux(password, salt);
    }
}
