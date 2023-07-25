package mavmi.telegram_bot.common.database.repository;

import mavmi.telegram_bot.common.database.model.UserModel;
import org.springframework.jdbc.core.RowMapper;

import javax.sql.DataSource;

public class UserRepository extends AbsRepository{
    private static final RowMapper<UserModel> mapper = (rs, rowNum) -> {
        return UserModel.builder()
                .id(rs.getLong("id"))
                .chatId(rs.getLong("chatid"))
                .username(rs.getString("username"))
                .firstName(rs.getString("firstname"))
                .lastName(rs.getString("lastname"))
                .build();
    };

    public UserRepository(DataSource dataSource) {
        super(dataSource);
    }

    public void add(UserModel userModel){
        jdbcTemplate.update(
                "insert into \"user\" values (?, ?, ?, ?, ?);",
                userModel.getId(),
                userModel.getChatId(),
                userModel.getUsername(),
                userModel.getFirstName(),
                userModel.getLastName()
        );
    }

    public void update(UserModel userModel){
        jdbcTemplate.update(
                "update \"user\" set chatid = ?, username = ?, firstname = ?, lastname = ? where id = ?;",
                userModel.getChatId(),
                userModel.getUsername(),
                userModel.getFirstName(),
                userModel.getLastName(),
                userModel.getId()
        );
    }
}
