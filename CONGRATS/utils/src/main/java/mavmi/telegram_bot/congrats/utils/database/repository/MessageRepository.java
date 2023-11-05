package mavmi.telegram_bot.congrats.utils.database.repository;

import mavmi.telegram_bot.common.database.repository.AbsRepository;
import mavmi.telegram_bot.congrats.utils.database.model.MessageModel;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.List;

@Component("CongratsMessageRepository")
public class MessageRepository extends AbsRepository {
    private static final RowMapper<MessageModel> mapper = (rs, rowNum) -> {
        return MessageModel.builder()
                .id(rs.getLong("id"))
                .message(rs.getString("message"))
                .build();
    };

    public MessageRepository(DataSource dataSource) {
        super(dataSource);
    }

    public void add(MessageModel messageModel) {
        jdbcTemplate.update(
                "insert into message(message) values(?);",
                messageModel.getMessage()
        );
    }

    @Nullable
    public MessageModel get(Long id) {
        List<MessageModel> messageModelList = jdbcTemplate.query(
                "select * from message where id = ?;",
                mapper,
                id
        );

        if (messageModelList.isEmpty()) {
            return null;
        } else {
            return messageModelList.get(0);
        }
    }

    public List<MessageModel> getAll() {
        return jdbcTemplate.query(
                "select * from message;",
                mapper
        );
    }

    public void delete(MessageModel messageModel) {
        jdbcTemplate.update(
                "delete from message where id = ?;",
                messageModel.getId()
        );
    }
}
