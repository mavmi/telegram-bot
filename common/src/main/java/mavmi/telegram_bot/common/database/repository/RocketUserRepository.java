package mavmi.telegram_bot.common.database.repository;

import mavmi.telegram_bot.common.database.model.RocketUserModel;
import org.springframework.jdbc.core.RowMapper;

import javax.sql.DataSource;
import java.util.List;

public class RocketUserRepository extends AbsRepository {
    private static final RowMapper<RocketUserModel> mapper = (rs, rowNum) -> {
        return RocketUserModel.builder()
                .userid(rs.getLong("userid"))
                .login(rs.getString("login"))
                .passwd(rs.getString("passwd"))
                .rc_uid(rs.getString("rc_uid"))
                .rc_token(rs.getString("rc_token"))
                .token_exp(rs.getLong("token_exp"))
                .build();
    };

    public RocketUserRepository(DataSource dataSource) {
        super(dataSource);
    }

    public void add(RocketUserModel rocketUserModel) {
        jdbcTemplate.update(
                "insert into rocket_user values (?, ?, ?, ?, ?, ?);",
                rocketUserModel.getUserid(),
                rocketUserModel.getLogin(),
                rocketUserModel.getPasswd(),
                rocketUserModel.getRc_uid(),
                rocketUserModel.getRc_token(),
                rocketUserModel.getToken_exp()
        );
    }

    public RocketUserModel get(Long id) {
        List<RocketUserModel> rocketUserModelList = jdbcTemplate.query(
                "select * from rocket_user where userid = ?;",
                mapper,
                id
        );
        return (rocketUserModelList.size() != 0) ? rocketUserModelList.get(0) : null;
    }

    public void delete(Long id) {
        jdbcTemplate.update(
                "delete from rocket_user where userid = ?;",
                id
        );
    }
}
