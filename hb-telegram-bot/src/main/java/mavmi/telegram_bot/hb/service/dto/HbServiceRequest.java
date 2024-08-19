package mavmi.telegram_bot.hb.service.dto;

import lombok.Getter;
import lombok.Setter;
import mavmi.telegram_bot.common.service.dto.common.MessageJson;
import mavmi.telegram_bot.common.service.dto.common.UserJson;
import mavmi.telegram_bot.common.service.service.dto.ServiceRequest;

@Getter
@Setter
public class HbServiceRequest extends ServiceRequest {
    private UserJson userJson;
    private MessageJson messageJson;
}
