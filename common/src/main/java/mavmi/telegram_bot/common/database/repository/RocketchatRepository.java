package mavmi.telegram_bot.common.database.repository;

import mavmi.telegram_bot.common.database.model.RocketchatModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface RocketchatRepository extends JpaRepository<RocketchatModel, Long> {

    Optional<RocketchatModel> findByTelegramId(long id);

    @Transactional
    @Modifying
    @Query(
            value = "update rocketchat set " +
                    "telegram_username = :#{#model.telegramUsername}, " +
                    "telegram_firstname = :#{#model.telegramFirstname}, " +
                    "telegram_lastname = :#{#model.telegramLastname}, " +
                    "rocketchat_username = :#{#model.rocketchatUsername}, " +
                    "rocketchat_password_hash = :#{#model.rocketchatPasswordHash}, " +
                    "rocketchat_token = :#{#model.rocketchatToken}, " +
                    "rocketchat_token_expiry_date = :#{#model.rocketchatTokenExpiryDate} " +
                    "where telegram_id = :#{#model.telegramId}",
            nativeQuery = true
    )
    void updateByTelegramId(@Param("model") RocketchatModel model);

    @Transactional
    @Modifying
    @Query(value = "delete from rocketchat where telegram_id = :id", nativeQuery = true)
    void deleteByTelegramId(@Param("id") Long telegramId);
}
