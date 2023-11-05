package mavmi.telegram_bot.congrats.utils.database.repository;

import mavmi.telegram_bot.common.database.repository.AbsRepository;
import mavmi.telegram_bot.congrats.utils.database.model.UserModel;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.List;

@Component("CongratsUserRepository")
public class UserRepository extends AbsRepository {
    private static final RowMapper<UserModel> mapper = (rs, rowNum) -> {
        return UserModel.builder()
                .id(rs.getLong("id"))
                .chatId(rs.getLong("chatid"))
                .username(rs.getString("username"))
                .firstName(rs.getString("firstname"))
                .lastName(rs.getString("lastname"))
                .intensive(rs.getBoolean("intensive"))
                .admin(rs.getBoolean("admin"))
                .build();
    };

    public UserRepository(DataSource dataSource) {
        super(dataSource);
    }

    public void add(UserModel userModel) {
        jdbcTemplate.update(
                "insert into \"user\" values(?, ?, ?, ?, ?, ?, ?);",
                userModel.getId(),
                userModel.getChatId(),
                userModel.getUsername(),
                userModel.getFirstName(),
                userModel.getLastName(),
                userModel.getIntensive(),
                userModel.getAdmin()
        );
    }

    @Nullable
    public UserModel get(Long id) {
        List<UserModel> userModelList = jdbcTemplate.query(
                "select * from \"user\" where id = ?;",
                mapper,
                id
        );

        if (userModelList.isEmpty()) {
            return null;
        } else {
            return userModelList.get(0);
        }
    }
}
