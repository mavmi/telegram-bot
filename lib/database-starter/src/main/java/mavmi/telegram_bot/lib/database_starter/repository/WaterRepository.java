package mavmi.telegram_bot.lib.database_starter.repository;

import mavmi.telegram_bot.lib.database_starter.model.WaterModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface WaterRepository extends JpaRepository<WaterModel, Long> {

    @Query(
            value = "select * from water_stuff_telegram_bot.data " +
                    "where userid = :userid and " +
                    "name = :name",
            nativeQuery = true
    )
    Optional<WaterModel> findByUserIdAndGroupName(@Param("userid") long userid, @Param("name") String name);

    @Query(
            value = "select * from water_stuff_telegram_bot.data " +
                    "where userid = :userid",
            nativeQuery = true
    )
    List<WaterModel> findByUserId(@Param("userid") long userid);

    @Transactional
    @Modifying
    @Query(
            value = "update water_stuff_telegram_bot.data " +
                    "set name = :#{#model.name}, " +
                    "days_diff = :#{#model.daysDiff}, " +
                    "water_date = :#{#model.waterDate}, " +
                    "fertilize_date = :#{#model.fertilizeDate}, " +
                    "stop_notifications_until = :#{#model.stopNotificationsUntil}",
            nativeQuery = true
    )
    void updateByUserIdAndGroupName(@Param("model") WaterModel model);

    @Transactional
    @Modifying
    @Query(
            value = "delete from water_stuff_telegram_bot.data where userid = :userid and name = :name",
            nativeQuery = true
    )
    void removeByUserIdAndGroupName(@Param("userid") long userid, @Param("name") String name);
}
