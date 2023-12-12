package mavmi.telegram_bot.common.utils.dto.json.service.inner;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServiceTaskManagerJson {
    private String target;
    private String message;
}
