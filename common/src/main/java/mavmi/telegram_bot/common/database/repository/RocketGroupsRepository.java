package mavmi.telegram_bot.common.database.repository;

import mavmi.telegram_bot.common.database.model.RocketGroupsModel;
import org.springframework.jdbc.core.RowMapper;

import javax.sql.DataSource;
import java.util.List;

public class RocketGroupsRepository extends AbsRepository {
    public RocketGroupsRepository(DataSource dataSource) {
        super(dataSource);
    }

    private static final RowMapper<RocketGroupsModel> mapper = (rs, rowNum) -> {
        return RocketGroupsModel.builder()
                .rc_uid(rs.getString("rc_uid"))
                .group_id(rs.getString("group_id"))
                .last_msg_id("last_msg_id")
                .last_msg_author_id("last_msg_author_id")
                .build();
    };

    public void add(RocketGroupsModel rocketGroupsModel) {
        jdbcTemplate.update(
                "insert into rocket_groups values (?, ?, ?, ?);",
                rocketGroupsModel.getRc_uid(),
                rocketGroupsModel.getGroup_id(),
                rocketGroupsModel.getLast_msg_id(),
                rocketGroupsModel.getLast_msg_author_id()
        );
    }

    public List<RocketGroupsModel> get(String rc_uid) {
        return jdbcTemplate.query(
                "select * from rocket_groups where rc_uid = ?;",
                mapper,
                rc_uid
        );
    }

    public void delete(String rc_uid) {
        jdbcTemplate.update(
                "delete from rocket_groups where rc_uid = ?;",
                rc_uid
        );
    }
}
