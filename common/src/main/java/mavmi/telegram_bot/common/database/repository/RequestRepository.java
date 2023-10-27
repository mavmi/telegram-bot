package mavmi.telegram_bot.common.database.repository;

import mavmi.telegram_bot.common.database.model.RequestModel;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

@Component
public class RequestRepository extends AbsRepository{
    private static final RowMapper<RequestModel> mapper = (rs, rowNum) -> {
        return RequestModel.builder()
                .userid(rs.getLong("userid"))
                .message(rs.getString("message"))
                .date(rs.getDate("date"))
                .time(rs.getTime("time"))
                .build();
    };

    public RequestRepository(DataSource dataSource) {
        super(dataSource);
    }


    public void add(RequestModel crvModel){
        jdbcTemplate.update(
                "insert into request values (?, ?, ?, ?);",
                crvModel.getUserid(),
                crvModel.getMessage(),
                crvModel.getDate(),
                crvModel.getTime()
        );
    }
}
