package mavmi.telegram_bot.common.database.repository;

import mavmi.telegram_bot.common.database.model.PrivilegesModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PrivilegesRepository extends JpaRepository<PrivilegesModel, Long> {

}
