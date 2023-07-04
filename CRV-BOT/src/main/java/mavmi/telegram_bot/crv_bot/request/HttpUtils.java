package mavmi.telegram_bot.crv_bot.request;

import lombok.NoArgsConstructor;
import mavmi.telegram_bot.crv_bot.user.User;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@NoArgsConstructor
public class HttpUtils {
    @Autowired
    @Qualifier("HttpData")
    private HttpData httpData;

    public Request getCrvCountRequest(User user){
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody requestBody = RequestBody.create(mediaType, httpData.getBody());

        Request.Builder requestBuilder = new Request.Builder()
                .url(httpData.getUrl())
                .post(requestBody);

        for (Map.Entry<String, String> entry : httpData.getHeaders().entrySet()){
            requestBuilder.addHeader(entry.getKey(), entry.getValue());
        }

        return requestBuilder
                .addHeader("content-length", Integer.toString(httpData.getBody().length()))
                .addHeader("cookie", "SI=" + user.getSI() + ";tokenId=" + user.getToken())
                .build();
    }
}
