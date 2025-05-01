package mavmi.telegram_bot.lib.database_starter.repository;

import mavmi.telegram_bot.lib.database_starter.model.LogsWebsocketModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LogsWebsocketRepository extends JpaRepository<LogsWebsocketModel, Long> {

}
