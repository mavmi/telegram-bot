package mavmi.telegram_bot.common.database.repository;

import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

public abstract class AbsRepository {
    protected JdbcTemplate jdbcTemplate;

    public AbsRepository(DataSource dataSource){
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

}
