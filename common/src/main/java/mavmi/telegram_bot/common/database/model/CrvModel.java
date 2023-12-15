package mavmi.telegram_bot.common.database.model;

import lombok.*;

import java.sql.Array;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CrvModel {
    private Long id;
    private String username;
    private String passwd;
    private String cookie;
    private Boolean auto;
    private Array redirect;
}
