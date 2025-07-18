package mavmi.telegram_bot.lib.database_starter.repository;

import mavmi.telegram_bot.lib.database_starter.model.RocketchatModel;
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
            value = "update rocketchat_telegram_bot.rocketchat set " +
                    "telegram_username = :#{#model.telegramUsername}, " +
                    "telegram_firstname = :#{#model.telegramFirstname}, " +
                    "telegram_lastname = :#{#model.telegramLastname}, " +
                    "rocketchat_username = :#{#model.rocketchatUsername}, " +
                    "rocketchat_password_hash = :#{#model.rocketchatPasswordHash}, " +
                    "rocketchat_token = :#{#model.rocketchatToken}, " +
                    "last_qr_msg_id = :#{#model.lastQrMsgId} " +
                    "where telegram_id = :#{#model.telegramId}",
            nativeQuery = true
    )
    void updateByTelegramId(@Param("model") RocketchatModel model);

    @Transactional
    @Modifying
    @Query(
            value = "update rocketchat_telegram_bot.rocketchat set " +
                    "rocketchat_username = :username, " +
                    "rocketchat_password_hash = :passwordhash " +
                    "where telegram_id = :id",
            nativeQuery = true
    )
    void updateLoginPasswordByTelegramId(@Param("id") Long telegramId,
                                         @Param("username") String username,
                                         @Param("passwordhash") String passwordHash);

    @Transactional
    @Modifying
    @Query(
            value = "update rocketchat_telegram_bot.rocketchat set " +
                    "rocketchat_token = :token " +
                    "where telegram_id = :id",
            nativeQuery = true
    )
    void updateTokenTelegramId(@Param("id") Long telegramId, @Param("token") String token);

    @Transactional
    @Modifying
    @Query(value = "delete from rocketchat_telegram_bot.rocketchat where telegram_id = :id", nativeQuery = true)
    void deleteByTelegramId(@Param("id") Long telegramId);

    @Transactional
    @Modifying
    @Query(value = "update rocketchat_telegram_bot.rocketchat set last_qr_msg_id = :msgid where telegram_id = :id", nativeQuery = true)
    void updateLastQrMsgId(@Param("id") Long telegramId, @Param("msgid") Integer lastQrMsgId);
}
