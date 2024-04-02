package mavmi.telegram_bot.common.database.repository;

import mavmi.telegram_bot.common.database.model.RequestModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RequestRepository extends JpaRepository<RequestModel, Long> {

}
