package mavmi.telegram_bot.hb.service;

import mavmi.telegram_bot.common.cache.api.AuthCache;
import mavmi.telegram_bot.common.cache.api.DataCache;
import mavmi.telegram_bot.common.service.service.direct.DirectService;
import mavmi.telegram_bot.hb.service.dto.HbServiceRequest;
import mavmi.telegram_bot.hb.service.dto.HbServiceResponse;
import org.springframework.stereotype.Component;

@Component
public class HbService implements DirectService<HbServiceResponse, HbServiceRequest> {

    @Override
    public HbServiceResponse handleRequest(HbServiceRequest request) {
        return null;
    }

    @Override
    public DataCache initDataCache(long chatId) {
        return null;
    }

    @Override
    public AuthCache initAuthCache(long chatId) {
        return null;
    }
}
