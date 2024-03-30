package mavmi.telegram_bot.common.httpClient;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;

import java.io.IOException;
import java.net.URI;

@Slf4j
public class HttpClientErrorHandler implements ResponseErrorHandler {

    @Override
    public boolean hasError(@NotNull ClientHttpResponse response) throws IOException {
        return !response.getStatusCode().equals(HttpStatusCode.valueOf(HttpStatus.OK.value()));
    }

    @Override
    public void handleError(@NotNull ClientHttpResponse response) throws IOException {

    }

    @Override
    public void handleError(@NotNull URI url, @NotNull HttpMethod method, @NotNull ClientHttpResponse response) throws IOException {

    }
}
