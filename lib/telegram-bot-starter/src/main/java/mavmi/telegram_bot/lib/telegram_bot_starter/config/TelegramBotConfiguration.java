package mavmi.telegram_bot.lib.telegram_bot_starter.config;

import com.pengrad.telegrambot.TelegramBot;
import okhttp3.OkHttpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.concurrent.TimeUnit;

@Configuration
public class TelegramBotConfiguration {

    @Bean("telegramBot")
    @ConditionalOnProperty(name = "telegram-bot.proxy.enabled", havingValue = "true")
    public TelegramBot getTelegramBotWithProxy(@Value("${telegram-bot.token}") String token,
                                      @Value("${telegram-bot.proxy.host}") String proxyHost,
                                      @Value("${telegram-bot.proxy.port}") int proxyPort) {
        Proxy socksProxy = new Proxy(Proxy.Type.SOCKS, new InetSocketAddress(proxyHost, proxyPort));

        OkHttpClient client = new OkHttpClient.Builder()
                .proxy(socksProxy)
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();

        return new TelegramBot.Builder(token)
                .okHttpClient(client)
                .build();
    }

    @Bean("telegramBot")
    @ConditionalOnProperty(name = "telegram-bot.proxy.enabled", havingValue = "false", matchIfMissing = true)
    public TelegramBot getTelegramBot(@Value("${telegram-bot.token}") String token) {
        return new TelegramBot(token);
    }
}
