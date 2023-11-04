package mavmi.telegram_bot.water_stuff_bot.data.pause;

import lombok.Getter;
import mavmi.telegram_bot.water_stuff_bot.data.DataException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

@Component
public class UsersPauseNotificationsData {
    private static final String FILE_LINE_SEPARATOR = ";";

    private final File pauseFile;
    private final Map<Long, Long> usersPauseNotificationsMap;
    @Getter
    private final Long pauseTime;

    public UsersPauseNotificationsData(
            @Value("${bot.pause-file}") String filePath,
            @Value("${bot.pause-time}") Long pauseTime
    ) {
        this.pauseFile = new File(filePath);
        this.pauseTime = pauseTime;
        this.usersPauseNotificationsMap = new HashMap<>();
        loadFormFile();
    }

    @Nullable
    public Long get(Long userId) {
        return usersPauseNotificationsMap.get(userId);
    }

    public void put(Long userId, Long pause) {
        usersPauseNotificationsMap.put(userId, pause);
        saveToFile();
    }

    public void remove(Long userId) {
        usersPauseNotificationsMap.remove(userId);
        saveToFile();
    }

    private void loadFormFile() {
        usersPauseNotificationsMap.clear();

        try (BufferedReader reader = new BufferedReader(new FileReader(pauseFile))) {

            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isEmpty()) {
                    continue;
                }

                String[] splittedLine = line.split(FILE_LINE_SEPARATOR);
                if (splittedLine.length != 2) {
                    throw new DataException("Pause file's syntax is incorrect");
                }

                usersPauseNotificationsMap.put(
                        Long.parseLong(splittedLine[0]),
                        Long.parseLong(splittedLine[1])
                );
            }

        } catch (IOException e) {
            if (pauseFile.exists()) {
                throw new DataException(e);
            }
        } catch (NumberFormatException e) {
            throw new DataException(e);
        }
    }

    private void saveToFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(pauseFile))) {

            StringBuilder stringBuilder = new StringBuilder();
            for (Map.Entry<Long, Long> entry : usersPauseNotificationsMap.entrySet()) {
                stringBuilder.append(entry.getKey())
                        .append(FILE_LINE_SEPARATOR)
                        .append(entry.getValue())
                        .append("\n");
            }
            writer.write(stringBuilder.toString());

        } catch (IOException e) {
            throw new DataException(e);
        }
    }
}
