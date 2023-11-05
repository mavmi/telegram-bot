package mavmi.telegram_bot.congrats.utils.database.repository;

import mavmi.telegram_bot.common.database.repository.AbsRepository;
import mavmi.telegram_bot.congrats.utils.database.model.RequestModel;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

@Component("CongratsRequestRepository")
public class RequestRepository extends AbsRepository {
    private static final RowMapper<RequestModel> mapper = (rs, rowNum) -> {
        return RequestModel.builder()
                .id(rs.getLong("id"))
                .userId(rs.getLong("userid"))
                .message(rs.getString("message"))
                .date(rs.getDate("date"))
                .time(rs.getTime("time"))
                .build();
    };

    public RequestRepository(DataSource dataSource) {
        super(dataSource);
    }

    public void add(RequestModel requestModel) {
        jdbcTemplate.update(
                "insert into request(userid, message, \"date\", \"time\") values(?, ?, ?, ?);",
                requestModel.getUserId(),
                requestModel.getMessage(),
                requestModel.getDate(),
                requestModel.getTime()
        );
    }
}
