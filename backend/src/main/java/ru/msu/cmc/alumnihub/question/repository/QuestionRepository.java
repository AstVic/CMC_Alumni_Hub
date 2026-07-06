package ru.msu.cmc.alumnihub.question.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.msu.cmc.alumnihub.question.entity.Question;
import ru.msu.cmc.alumnihub.question.entity.QuestionStatus;

import java.util.List;

public interface QuestionRepository extends JpaRepository<Question, Long> {

    Page<Question> findByStatus(QuestionStatus status, Pageable pageable);

    List<Question> findByAlumniProfileIdAndStatusOrderByCreatedAtDesc(
            Long alumniProfileId, QuestionStatus status);

    List<Question> findByAlumniProfileIdAndStatusAndReadByAlumniOrderByCreatedAtDesc(
            Long alumniProfileId, QuestionStatus status, boolean readByAlumni);

    long countByStatus(QuestionStatus status);
}
