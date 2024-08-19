package mavmi.telegram_bot.common.database.repository;

import mavmi.telegram_bot.common.database.model.HbScoreModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface HbScoreRepository extends JpaRepository<HbScoreModel, Long> {

    @Modifying
    @Transactional
    @Query(
            value = "update hb_score set score = ?2 where edu_username = ?1",
            nativeQuery = true
    )
    void updateScoreByEduUsername(String username, Double score);

    @Modifying
    @Transactional
    @Query(
            value = "update hb_score set fortune = ?2 where edu_username = ?1",
            nativeQuery = true
    )
    void updateFortuneByEduUsername(String username, Long fortune);

    @Modifying
    @Transactional
    @Query(
            value = "update hb_score set score = ?2, fortune = ?3 where edu_username = ?1",
            nativeQuery = true
    )
    void updateScoreAndFortuneByUsername(String username, Double score, Long fortune);

    Optional<HbScoreModel> findByEduUsername(String eduUsername);
}
