package mavmi.telegram_bot.common.database.repository;

import mavmi.telegram_bot.common.database.model.CrvModel;
import org.springframework.jdbc.core.RowMapper;

import javax.sql.DataSource;
import java.util.List;

public class CrvRepository extends AbsRepository{
    private static final RowMapper<CrvModel> mapper = (rs, rowNum) -> {
        return CrvModel.builder()
                .id(rs.getLong("id"))
                .username(rs.getString("username"))
                .passwd(rs.getString("passwd"))
                .cookie(rs.getString("cookie"))
                .auto(rs.getBoolean("auto"))
                .redirect(rs.getArray("redirect"))
                .build();
    };

    public CrvRepository(DataSource dataSource) {
        super(dataSource);
    }

    public CrvModel get(Long userid){
        List<CrvModel> crvModelList = jdbcTemplate.query(
                "select * from crv where id = ?;",
                mapper,
                userid
        );
        return (crvModelList.size() != 0) ? crvModelList.get(0) : null;
    }

    public void update(CrvModel crvModel){
        jdbcTemplate.update(
                "update crv set username = ?, passwd = ?, cookie = ?, auto = ?, redirect = ? where id = ?;",
                crvModel.getUsername(),
                crvModel.getPasswd(),
                crvModel.getCookie(),
                crvModel.getAuto(),
                crvModel.getRedirect(),
                crvModel.getId()
        );
    }
}
