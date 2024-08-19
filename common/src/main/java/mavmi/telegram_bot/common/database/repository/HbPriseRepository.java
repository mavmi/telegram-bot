package mavmi.telegram_bot.common.database.repository;

import mavmi.telegram_bot.common.database.model.HbPriseModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public interface HbPriseRepository extends JpaRepository<HbPriseModel, Long> {

    @Query(
            value = "select * from hb_prise where edu_username = ?1",
            nativeQuery = true
    )
    List<HbPriseModel> getAllByEduUsername(String eduUsername);
}
