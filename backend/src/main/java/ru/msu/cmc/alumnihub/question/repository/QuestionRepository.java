package ru.msu.cmc.alumnihub.question.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.msu.cmc.alumnihub.question.entity.Question;
import ru.msu.cmc.alumnihub.question.entity.QuestionStatus;

import java.util.Collection;
import java.util.List;

public interface QuestionRepository extends JpaRepository<Question, Long> {

    List<Question> findByAlumniProfileIdAndStatusOrderByCreatedAtDesc(
            Long alumniProfileId, QuestionStatus status);

    List<Question> findByAlumniProfileIdAndStatusAndReadByAlumniOrderByCreatedAtDesc(
            Long alumniProfileId, QuestionStatus status, boolean readByAlumni);

    List<Question> findByStatusInOrderByCreatedAtDesc(Collection<QuestionStatus> statuses);

    List<Question> findAllByOrderByCreatedAtDesc();

    long countByStatus(QuestionStatus status);

    long countByStatusIn(Collection<QuestionStatus> statuses);
}
