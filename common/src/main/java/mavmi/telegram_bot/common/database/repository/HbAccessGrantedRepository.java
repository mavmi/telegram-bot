package mavmi.telegram_bot.common.database.repository;

import mavmi.telegram_bot.common.database.model.HbAccessGrantedModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
@Transactional
public interface HbAccessGrantedRepository extends JpaRepository<HbAccessGrantedModel, Long> {

    Optional<HbAccessGrantedModel> findByTelegramId(long id);
}
