package mavmi.telegram_bot.common.utils.http;

import mavmi.telegram_bot.common.utils.dto.json.IRequestJson;

public abstract class AbsHttpClient<R extends IRequestJson> {
    public abstract int sendRequest(String endpoint, R serviceRequestJson);
}
