package mavmi.telegram_bot.common.database.repository;

import mavmi.telegram_bot.common.database.model.LogsWebsocketModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LogsWebsocketRepository extends JpaRepository<LogsWebsocketModel, Long> {

}
