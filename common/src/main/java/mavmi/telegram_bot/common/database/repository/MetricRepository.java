package mavmi.telegram_bot.common.database.repository;

import mavmi.telegram_bot.common.database.model.MetricModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.util.Optional;

@Repository
public interface MetricRepository extends JpaRepository<MetricModel, Long> {

    @Transactional
    @Modifying
    @Query(
            value = "update common.metric set " +
                    "count = :#{#model.count}, " +
                    "success = :#{#model.success}, " +
                    "error = :#{#model.error} where " +
                    "bot_name = :#{#model.botName} and " +
                    "telegram_id = :#{#model.telegramId} and " +
                    "\"date\" = :#{#model.date}",
            nativeQuery = true
    )
    void updateByTelegramIdAndDate(@Param("model") MetricModel model);

    @Query(
            value = "select * from common.metric where " +
                    "bot_name = :bot_name and " +
                    "telegram_id = :telegram_id and  " +
                    "\"date\" = :date " +
                    "limit 1",
            nativeQuery = true
    )
    Optional<MetricModel> find(@Param("bot_name") String botName, @Param("telegram_id") long telegramId, @Param("date") Date date);
}
