package mavmi.telegram_bot.crv_bot.request;

import lombok.Getter;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

@Getter
public class HttpData {
    private final String url;
    private final Map<String, String> headers;
    private final String body;

    public HttpData(){
        url = readUrl();
        headers = readHeaders();
        body = readBody();
    }

    private String readUrl(){
        BufferedReader reader = getFile("/url.properties");
        try {
            return reader.readLine();
        } catch (IOException e){
            System.err.println(e.getMessage());
            return null;
        }
    }

    private Map<String, String> readHeaders(){
        BufferedReader reader = getFile("/headers.properties");
        Map<String, String> hdrs = new HashMap<>();

        String line;
        try {
            while ((line = reader.readLine()) != null){
                line = line.replaceAll(" ", "");
                int pos = line.indexOf(':');

                hdrs.put(
                        line.substring(0, pos),
                        line.substring(pos + 1)
                );
            }
        } catch (IOException e){
            System.err.println(e.getMessage());
            return null;
        }

        return hdrs;
    }

    private String readBody(){
        BufferedReader reader = getFile("/body.properties");
        try {
            return reader.readLine();
        } catch (IOException e){
            System.err.println(e.getMessage());
            return null;
        }
    }

    private BufferedReader getFile(String filename){
        return new BufferedReader(new InputStreamReader(getClass().getResourceAsStream(filename)));
    }
}
