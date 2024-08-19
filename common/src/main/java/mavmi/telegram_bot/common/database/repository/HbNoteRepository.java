package mavmi.telegram_bot.common.database.repository;

import mavmi.telegram_bot.common.database.model.HbNoteModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
@Transactional
public interface HbNoteRepository extends JpaRepository<HbNoteModel, Long> {

    Optional<HbNoteModel> findByEduUsernameAndEventName(String eduUsername, String eventName);

    @Modifying
    @Query(
            value = "update hb_note set edu_username = ?2, event_name = ?3, grade = ?4, grade_with_factor = ?5, cell_position = ?6 where id = ?1",
            nativeQuery = true
    )
    void updateById(Long id, String eduUsername, String eventName, Long grade, double gradeWithFactor, String cellPosition);
}
