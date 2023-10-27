package mavmi.telegram_bot.common.database.repository;

import mavmi.telegram_bot.common.database.model.RocketImModel;
import mavmi.telegram_bot.common.database.model.RocketUserModel;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.security.crypto.encrypt.TextEncryptor;

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
                .show_content(rs.getBoolean("show_content"))
                .build();
    };

    private final TextEncryptor textEncryptor;

    public RocketUserRepository(DataSource dataSource, TextEncryptor textEncryptor) {
        super(dataSource);
        this.textEncryptor = textEncryptor;
    }

    public void add(RocketUserModel rocketUserModel) {
        rocketUserModel = encrypt(rocketUserModel);

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
        return (!rocketUserModelList.isEmpty()) ?
                decrypt(rocketUserModelList.get(0)) :
                null;
    }

    public RocketUserModel get(String login) {
        List<RocketUserModel> rocketUserModelList = jdbcTemplate.query(
                "select * from rocket_user where login = ?;",
                mapper,
                login
        );
        return (!rocketUserModelList.isEmpty()) ?
                decrypt(rocketUserModelList.get(0)) :
                null;
    }

    public List<RocketUserModel> getAll() {
        List<RocketUserModel> models = jdbcTemplate.query(
                "select * from rocket_user;",
                mapper
        );

        for (int i = 0; i < models.size(); i++) {
            models.set(i, decrypt(models.get(i)));
        }

        return models;
    }

    public void delete(Long id) {
        jdbcTemplate.update(
                "delete from rocket_user where userid = ?;",
                id
        );
    }

    private RocketUserModel encrypt(RocketUserModel input) {
        return RocketUserModel.builder()
                .userid(input.getUserid())
                .login(textEncryptor.encrypt(input.getLogin()))
                .passwd(textEncryptor.encrypt(input.getPasswd()))
                .rc_uid(input.getRc_uid())
                .rc_token(textEncryptor.encrypt(input.getRc_token()))
                .token_exp(input.getToken_exp())
                .show_content(input.getShow_content())
                .build();
    }

    private RocketUserModel decrypt(RocketUserModel input) {
        return RocketUserModel.builder()
                .userid(input.getUserid())
                .login(textEncryptor.decrypt(input.getLogin()))
                .passwd(textEncryptor.decrypt(input.getPasswd()))
                .rc_uid(input.getRc_uid())
                .rc_token(textEncryptor.decrypt(input.getRc_token()))
                .token_exp(input.getToken_exp())
                .show_content(input.getShow_content())
                .build();
    }
}
