package mavmi.telegram_bot.common.database.repository;

import mavmi.telegram_bot.common.database.model.RocketImModel;
import org.springframework.jdbc.core.RowMapper;

import javax.sql.DataSource;
import java.util.List;

public class RocketImRepository extends AbsRepository{
    private static final RowMapper<RocketImModel> mapper = (rs, rowNum) -> {
        return RocketImModel.builder()
                .rc_uid(rs.getString("rc_uid"))
                .chat_id(rs.getString("chat_id"))
                .last_msg_id(rs.getString("last_msg_id"))
                .last_msg_author_id(rs.getString("last_msg_author_id"))
                .build();
    };

    public RocketImRepository(DataSource dataSource) {
        super(dataSource);
    }

    public void add(RocketImModel rocketImModel) {
        jdbcTemplate.update(
                "insert into rocket_im values (?, ?, ?, ?);",
                rocketImModel.getRc_uid(),
                rocketImModel.getChat_id(),
                rocketImModel.getLast_msg_id(),
                rocketImModel.getLast_msg_author_id()
        );
    }

    public List<RocketImModel> get(String rc_uid) {
        return jdbcTemplate.query(
                "select * from rocket_im where rc_uid = ?;",
                mapper,
                rc_uid
        );
    }

    public void delete(String rc_uid) {
        jdbcTemplate.update(
                "delete from rocket_im where rc_uid = ?;",
                rc_uid
        );
    }
}
