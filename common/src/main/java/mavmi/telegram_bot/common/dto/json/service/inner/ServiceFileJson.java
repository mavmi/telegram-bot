package mavmi.telegram_bot.common.dto.json.service.inner;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServiceFileJson {
    @JsonProperty("file_path")
    private String filePath;
}
