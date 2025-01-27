package mavmi.telegram_bot.common.crypto;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.encrypt.Encryptors;
import org.springframework.security.crypto.encrypt.TextEncryptor;

@Configuration
@ConditionalOnProperty(prefix = "crypto", name = {"password", "salt"})
public class CryptoConfiguration {

    @Bean("rocketChatTextEncryptor")
    public TextEncryptor getTextEncryptor(
            @Value("${crypto.password}") String password,
            @Value("${crypto.salt}") String salt
    ) {
        return Encryptors.delux(password, salt);
    }
}
