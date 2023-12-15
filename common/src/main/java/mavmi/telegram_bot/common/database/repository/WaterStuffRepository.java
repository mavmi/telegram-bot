package mavmi.telegram_bot.common.database.repository;

import mavmi.telegram_bot.common.database.model.WaterStuffModel;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.List;

@Component
@ConditionalOnBean(DataSource.class)
public class WaterStuffRepository extends AbsRepository{
    private static final RowMapper<WaterStuffModel> mapper = (rs, rowNum) -> {
        return WaterStuffModel.builder()
                .id(rs.getLong("id"))
                .name(rs.getString("name"))
                .water(rs.getDate("water"))
                .fertilize(rs.getDate("fertilize"))
                .diff(rs.getInt("diff"))
                .build();
    };

    public WaterStuffRepository(DataSource dataSource) {
        super(dataSource);
    }

    public WaterStuffModel get(String name){
        List<WaterStuffModel> waterStuffModelList = jdbcTemplate.query(
                "select * from water_stuff where name = ?;",
                mapper,
                name
        );
        return (waterStuffModelList.size() != 0) ? waterStuffModelList.get(0) : null;
    }

    public List<WaterStuffModel> getAll(){
        return jdbcTemplate.query("select * from water_stuff;", mapper);
    }

    public void update(WaterStuffModel waterStuffModel){
        jdbcTemplate.update(
                "update water_stuff set name = ?, water = ?, fertilize = ?, diff = ? where id = ?;",
                waterStuffModel.getName(),
                waterStuffModel.getWater(),
                waterStuffModel.getFertilize(),
                waterStuffModel.getDiff(),
                waterStuffModel.getId()
        );
    }

    public void insert(WaterStuffModel waterStuffModel){
        jdbcTemplate.update(
                "insert into water_stuff(name, water, fertilize, diff) values(?, ?, ?, ?);",
                waterStuffModel.getName(),
                waterStuffModel.getWater(),
                waterStuffModel.getFertilize(),
                waterStuffModel.getDiff()
        );
    }

    public void remove(WaterStuffModel waterStuffModel){
        jdbcTemplate.update(
                "delete from water_stuff where id = ?;",
                waterStuffModel.getId()
        );
    }
}
