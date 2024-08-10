package mavmi.telegram_bot.common.service.dto.common;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImageJson {
    @JsonProperty("file_path")
    private String filePath;
}
