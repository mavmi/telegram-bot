package mavmi.telegram_bot.common.arguments;

import lombok.NoArgsConstructor;
import mavmi.telegram_bot.common.arguments.exception.ArgumentsParserException;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@NoArgsConstructor
public class ArgumentsParser {
    private final Map<String, String> keyToValue = new HashMap<>();

    public void parse(String[] args, List<String> keys){
        for (String arg : args) {
            String[] keyValue = splitLine(arg);
            String key = keyValue[0];

            if (!keys.contains(key)) {
                throw new ArgumentsParserException("Invalid argument's key: " + key);
            }

            keyToValue.put(keyValue[0], keyValue[1]);
        }
    }

    public String get(String key){
        return keyToValue.get(key);
    }

    private String[] splitLine(String line){
        int pos = line.indexOf('=');

        if (pos == -1) {
            throw new ArgumentsParserException("Invalid argument: " + line);
        }

        return new String[]{
                line.substring(0, pos),
                line.substring(pos + 1)
        };
    }
}
