package mavmi.telegram_bot.common.database.repository;

import mavmi.telegram_bot.common.database.model.RocketUserModel;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.List;

@Component
public class RocketUserRepository extends AbsRepository {
    private static final RowMapper<RocketUserModel> mapper = (rs, rowNum) -> {
        return RocketUserModel.builder()
                .userid(rs.getLong("userid"))
                .login(rs.getString("login"))
                .passwd(rs.getString("passwd"))
                .rc_uid(rs.getString("rc_uid"))
                .rc_token(rs.getString("rc_token"))
                .token_exp(rs.getLong("token_exp"))
                .show_content(rs.getBoolean("show_content"))
                .build();
    };

    public RocketUserRepository(DataSource dataSource) {
        super(dataSource);
    }

    public void add(RocketUserModel rocketUserModel) {
        jdbcTemplate.update(
                "insert into rocket_user values (?, ?, ?, ?, ?, ?, ?);",
                rocketUserModel.getUserid(),
                rocketUserModel.getLogin(),
                rocketUserModel.getPasswd(),
                rocketUserModel.getRc_uid(),
                rocketUserModel.getRc_token(),
                rocketUserModel.getToken_exp(),
                rocketUserModel.getShow_content()
        );
    }

    public RocketUserModel get(Long id) {
        List<RocketUserModel> rocketUserModelList = jdbcTemplate.query(
                "select * from rocket_user where userid = ?;",
                mapper,
                id
        );
        return (!rocketUserModelList.isEmpty()) ? rocketUserModelList.get(0) : null;
    }

    public RocketUserModel get(String login) {
        List<RocketUserModel> rocketUserModelList = jdbcTemplate.query(
                "select * from rocket_user where login = ?;",
                mapper,
                login
        );
        return (!rocketUserModelList.isEmpty()) ? rocketUserModelList.get(0) : null;
    }

    public List<RocketUserModel> getAll() {
        List<RocketUserModel> models = jdbcTemplate.query(
                "select * from rocket_user;",
                mapper
        );

        return models;
    }

    public void delete(Long id) {
        jdbcTemplate.update(
                "delete from rocket_user where userid = ?;",
                id
        );
    }
}
