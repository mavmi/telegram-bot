package mavmi.telegram_bot.common.database.repository;

import mavmi.telegram_bot.common.database.model.RuleModel;
import org.springframework.jdbc.core.RowMapper;

import javax.sql.DataSource;
import java.util.List;

public class RuleRepository extends AbsRepository{
    private static final RowMapper<RuleModel> mapper = (rs, rowNum) -> {
        return RuleModel.builder()
                .userid(rs.getLong("userid"))
                .chatGpt(rs.getBoolean("chat_gpt"))
                .waterStuff(rs.getBoolean("water_stuff"))
                .crv(rs.getBoolean("crv"))
                .monitoring(rs.getBoolean("monitoring"))
                .rocket(rs.getBoolean("rocket"))
                .build();
    };

    public RuleRepository(DataSource dataSource) {
        super(dataSource);
    }

    public RuleModel get(Long userId){
        List<RuleModel> ruleModelsList = jdbcTemplate.query(
                "select * from rule where userid = ?;",
                mapper,
                userId
        );
        return (ruleModelsList.size() != 0) ? ruleModelsList.get(0) : null;
    }

    public List<RuleModel> getAll(){
        return jdbcTemplate.query(
                "select * from rule;",
                mapper
        );
    }
}
