package mavmi.telegram_bot.crv_bot.request;

import lombok.Getter;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
public class RequestOptions {
    private long waitFrom;
    private long waitTo;
    private long timeoutFrom;
    private long timeoutTo;
    private String body;

    private final List<String> urls = new ArrayList<>();
    private final Map<String, String> headers = new HashMap<>();
    private final List<String> elems = new ArrayList<>();
    private final List<String> jsonFields = new ArrayList<>();

    public RequestOptions(){
        readSettings();
        readHeaders();
    }

    private void readSettings(){
        try (BufferedReader reader = getFile("/settings.properties")){
            String line;
            while ((line = reader.readLine()) != null){
                int pos = line.indexOf('=');
                String key = line.substring(0, pos);
                String value = line.substring(pos + 1);

                switch (key) {
                    case "waitFrom" -> waitFrom = Long.parseLong(value);
                    case "waitTo" -> waitTo = Long.parseLong(value);
                    case "timeoutFrom" -> timeoutFrom = Long.parseLong(value);
                    case "timeoutTo" -> timeoutTo = Long.parseLong(value);
                    case "body" -> body = value;
                    case "json1", "json2", "json3" -> jsonFields.add(value);
                    case "url1", "url2", "url3" -> urls.add(value);
                    case "elem1", "elem2", "elem3" -> elems.add(value);
                }
            }
        } catch (IOException e){
            System.err.println(e.getMessage());
        }
    }

    private void readHeaders(){
        try (BufferedReader reader = getFile("/headers.properties")){
            String line;
            while ((line = reader.readLine()) != null){
                line = line.replaceAll(" ", "");
                int pos = line.indexOf(':');

                headers.put(
                        line.substring(0, pos),
                        line.substring(pos + 1)
                );
            }
        } catch (IOException e){
            System.err.println(e.getMessage());
        }
    }


    private BufferedReader getFile(String filename){
        return new BufferedReader(new InputStreamReader(getClass().getResourceAsStream(filename)));
    }
}
