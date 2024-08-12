package mavmi.telegram_bot.common.database.repository;

import mavmi.telegram_bot.common.database.model.RocketchatModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface RocketchatRepository extends JpaRepository<RocketchatModel, Long> {

    Optional<RocketchatModel> findByTelegramId(long id);

    @Transactional
    @Modifying
    @Query(
            value = "update rocketchat set telegram_username=?2, telegram_firstname=?3, telegram_lastname=?4, rocketchat_username=?5, rocketchat_password_hash=?6, rocketchat_token=?7, rocketchat_token_expiry_date=?8 where telegram_id=?1",
            nativeQuery = true
    )
    void updateByTelegramId(Long telegramId, String telegramUsername, String telegramFirstname, String telegramLastname,
                            String rocketchatUsername, String rocketchatPasswordHash, String rocketchatToken, Long rocketchatTokenExpiryDate);

    @Transactional
    @Modifying
    @Query(
            value = "delete from rocketchat where telegram_id=?1",
            nativeQuery = true
    )
    void deleteByTelegramId(Long telegramId);
}
