package mavmi.telegram_bot.rocket_bot.jsonHandler.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.text.SimpleDateFormat;
import java.util.Date;

@Getter
@Builder
@AllArgsConstructor
public class ImHistoryResponse {
    public static final SimpleDateFormat INPUT_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
    public static final SimpleDateFormat OUTPUT_DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy HH:mm");

    private String msg_id;
    private String msg;
    private String timestamp;
    private String author_id;
    private String author_name;
}
