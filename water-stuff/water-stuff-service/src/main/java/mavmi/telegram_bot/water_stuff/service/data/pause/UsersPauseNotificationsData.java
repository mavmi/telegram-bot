package mavmi.telegram_bot.water_stuff.service.data.pause;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import mavmi.telegram_bot.water_stuff.service.data.DataException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
public class UsersPauseNotificationsData {

    private final File pauseFile;
    @Getter
    private final Long pauseTime;

    @JsonProperty("user_pause_notifications")
    private Map<Long, Long> usersPauseNotificationsMap;

    public UsersPauseNotificationsData(
            @Value("${service.pause-file}") String filePath,
            @Value("${service.pause-time}") Long pauseTime
    ) {
        this.pauseFile = new File(filePath);
        this.pauseTime = pauseTime;
        this.usersPauseNotificationsMap = new HashMap<>();
        loadFormFile();
    }

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
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            usersPauseNotificationsMap = objectMapper.readValue(pauseFile, new TypeReference<Map<Long, Long>>() {});
        } catch (IOException e) {
            if (pauseFile.exists()) {
                throw new DataException(e);
            }
        }

//        usersPauseNotificationsMap.clear();
//
//        try (BufferedReader reader = new BufferedReader(new FileReader(pauseFile))) {
//
//            String line;
//            while ((line = reader.readLine()) != null) {
//                if (line.isEmpty()) {
//                    continue;
//                }
//
//                String[] splittedLine = line.split(FILE_LINE_SEPARATOR);
//                if (splittedLine.length != 2) {
//                    throw new DataException("Pause file's syntax is incorrect");
//                }
//
//                usersPauseNotificationsMap.put(
//                        Long.parseLong(splittedLine[0]),
//                        Long.parseLong(splittedLine[1])
//                );
//            }
//
//        } catch (IOException e) {
//            if (pauseFile.exists()) {
//                throw new DataException(e);
//            }
//        } catch (NumberFormatException e) {
//            throw new DataException(e);
//        }
    }

    private void saveToFile() {
        ObjectMapper objectMapper = new ObjectMapper();

        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(pauseFile))) {
            bufferedWriter.write(objectMapper.writeValueAsString(usersPauseNotificationsMap));
        } catch (IOException e) {
            throw new DataException(e);
        }

//        try (BufferedWriter writer = new BufferedWriter(new FileWriter(pauseFile))) {
//
//            StringBuilder stringBuilder = new StringBuilder();
//            for (Map.Entry<Long, Long> entry : usersPauseNotificationsMap.entrySet()) {
//                stringBuilder.append(entry.getKey())
//                        .append(FILE_LINE_SEPARATOR)
//                        .append(entry.getValue())
//                        .append("\n");
//            }
//            writer.write(stringBuilder.toString());
//
//        } catch (IOException e) {
//            throw new DataException(e);
//        }
    }
}
