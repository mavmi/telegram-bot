package mavmi.telegram_bot.common.database.repository;

import mavmi.telegram_bot.common.database.model.HbParticipantModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
@Transactional
public interface HbParticipantRepository extends JpaRepository<HbParticipantModel, Long> {

    @Query(
            value = "insert into hb_participant(edu_username, row) values(?1, (select (coalesce(max(row) + 1, 5)) from hb_participant));",
            nativeQuery = true
    )
    @Modifying
    void insertNonExistingPeer(String eduUsername);

    Optional<HbParticipantModel> findByEduUsername(String eduUsername);
}
