package mavmi.telegram_bot.crv_bot.request;

import lombok.Getter;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
public class RequestOptions {
    private final long sleepTime;
    private final List<String> urls;
    private final Map<String, String> headers;
    private final String body;
    private final List<String> elems;
    private final List<String> jsonFields;

    public RequestOptions(){
        urls = readUrls();
        headers = readHeaders();
        body = readBody();
        elems = readElems();
        jsonFields = readJsonFields();
        sleepTime = readSettings();
    }

    private List<String> readUrls(){
        BufferedReader reader = getFile("/url.properties");
        List<String> output = new ArrayList<>();

        try {
            String line;
            while ((line = reader.readLine()) != null){
                output.add(line);
            }
            return output;
        } catch (IOException e){
            System.err.println(e.getMessage());
            return null;
        }
    }

    private Map<String, String> readHeaders(){
        BufferedReader reader = getFile("/headers.properties");
        Map<String, String> hdrs = new HashMap<>();

        try {
            String line;
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

    private List<String> readElems(){
        BufferedReader reader = getFile("/elems.properties");
        List<String> output = new ArrayList<>();

        try {
            String line;
            while ((line = reader.readLine()) != null){
                output.add(line);
            }
            return output;
        } catch (IOException e){
            System.err.println(e.getMessage());
            return null;
        }
    }

    private List<String> readJsonFields(){
        BufferedReader reader = getFile("/json.properties");
        List<String> output = new ArrayList<>();

        try {
            String line;
            while ((line = reader.readLine()) != null){
                output.add(line);
            }
            return output;
        } catch (IOException e){
            System.err.println(e.getMessage());
            return null;
        }
    }

    private long readSettings(){
        BufferedReader reader = getFile("/settings.properties");
        try {
            return Long.parseLong(reader.readLine());
        } catch (IOException e){
            System.err.println(e.getMessage());
            return 0;
        }
    }

    private BufferedReader getFile(String filename){
        return new BufferedReader(new InputStreamReader(getClass().getResourceAsStream(filename)));
    }
}
