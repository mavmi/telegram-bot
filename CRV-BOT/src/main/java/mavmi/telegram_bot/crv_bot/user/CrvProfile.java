package mavmi.telegram_bot.crv_bot.user;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import mavmi.telegram_bot.common.database.model.CrvModel;
import mavmi.telegram_bot.common.database.repository.CrvRepository;
import mavmi.telegram_bot.crv_bot.request.RequestOptions;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

import java.util.*;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class CrvProfile {
    private CrvModel crvModel;

    public static List<CrvProfile> getCrvProfiles(CrvRepository crvRepository){
        List<CrvProfile> output = new ArrayList<>();
        for (CrvModel model : crvRepository.getAll()){
            output.add(new CrvProfile(model));
        }
        return output;
    }
    public static CrvProfile getCrvProfile(CrvRepository crvRepository, long id){
        CrvModel model = crvRepository.get(id);
        if (model == null) return null;
        return new CrvProfile(model);
    }
    public static void updateUser(CrvRepository crvRepository, CrvProfile crvProfile){
        crvRepository.update(crvProfile.getCrvModel());
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
            webDriver.findElement(By.id(requestOptions.getElems().get(0))).sendKeys(crvModel.getUsername());
            webDriver.findElement(By.id(requestOptions.getElems().get(1))).sendKeys(crvModel.getPasswd());
            webDriver.findElement(By.xpath(requestOptions.getElems().get(2))).click();
            webDriver.get(requestOptions.getUrls().get(1));
            Thread.sleep(
                    (long)(Math.random() * (requestOptions.getTimeoutTo() - requestOptions.getTimeoutFrom())) +
                            requestOptions.getTimeoutFrom()
            );
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
