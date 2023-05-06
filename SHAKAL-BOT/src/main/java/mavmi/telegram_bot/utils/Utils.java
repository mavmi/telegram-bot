package mavmi.telegram_bot.utils;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

public abstract class Utils {
    public static String readFile(InputStream inputStream){
        try (BufferedInputStream reader = new BufferedInputStream(inputStream)){
            byte[] buffer = new byte[1024];
            StringBuilder query = new StringBuilder();

            while (reader.available() > 0){
                int bytesReadCount = reader.read(buffer);
                String line = new String(buffer, 0, bytesReadCount);
                query.append(line);
            }

            return query.toString();
        } catch (IOException e){
            System.err.println(e.getMessage());
            return null;
        }
    }
}
