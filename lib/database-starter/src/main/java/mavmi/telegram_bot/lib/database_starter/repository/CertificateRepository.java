package mavmi.telegram_bot.lib.database_starter.repository;

import mavmi.telegram_bot.lib.database_starter.model.CertificateModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CertificateRepository extends JpaRepository<CertificateModel, Long> {

}
