package mavmi.telegram_bot.crv_bot.user;

import lombok.Getter;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;
import mavmi.telegram_bot.crv_bot.request.RequestOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

@Getter
public class User {
    private long id;
    private String username;
    private String passwd;

    public User(long id, String username, String passwd){
        this.id = id;
        this.username = username;
        this.passwd = passwd;
    }

    public Request getCrvCountRequest(RequestOptions requestOptions){
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody requestBody = RequestBody.create(mediaType, requestOptions.getBody());

        Request.Builder requestBuilder = new Request.Builder()
                .url(requestOptions.getUrls().get(2))
                .post(requestBody);

        for (Map.Entry<String, String> entry : requestOptions.getHeaders().entrySet()){
            requestBuilder.addHeader(entry.getKey(), entry.getValue());
        }

        String cookiesStr = getCookies(requestOptions);
        if (cookiesStr == null) return null;
        return requestBuilder
                .addHeader("content-length", Integer.toString(requestOptions.getBody().length()))
                .addHeader("cookie", cookiesStr)
                .build();
    }

    private String getCookies(RequestOptions requestOptions){
        WebDriver webDriver = new FirefoxDriver(new FirefoxOptions().setHeadless(true));

        try {
            webDriver.get(requestOptions.getUrls().get(0));
            webDriver.findElement(By.id(requestOptions.getElems().get(0))).sendKeys(username);
            webDriver.findElement(By.id(requestOptions.getElems().get(1))).sendKeys(passwd);
            webDriver.findElement(By.xpath(requestOptions.getElems().get(2))).click();
            webDriver.get(requestOptions.getUrls().get(1));
            Thread.sleep(requestOptions.getSleepTime());
        } catch (Exception e){
            webDriver.quit();
            return null;
        }

        StringBuilder builder = new StringBuilder();
        Set<Cookie> cookies = webDriver.manage().getCookies();
        Iterator<Cookie> iter = cookies.iterator();
        while (iter.hasNext()){
            Cookie cookie = iter.next();
            builder.append(cookie.getName())
                    .append("=")
                    .append(cookie.getValue());
            if (iter.hasNext()) builder.append(";");
        }
        webDriver.quit();

        return builder.toString();
    }
}
