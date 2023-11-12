package mavmi.telegram_bot.common.utils.service;

import java.util.HashMap;
import java.util.Map;

public abstract class AbsService {

    protected Map<Long, AbsServiceUser> idToUser;

    public AbsService() {
        this.idToUser = new HashMap<>();
    }
}
