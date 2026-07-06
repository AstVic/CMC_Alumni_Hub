package ru.msu.cmc.alumnihub.moderation.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.msu.cmc.alumnihub.moderation.entity.ModerationEntityType;
import ru.msu.cmc.alumnihub.moderation.entity.ModerationLog;

import java.util.List;

public interface ModerationLogRepository extends JpaRepository<ModerationLog, Long> {

    List<ModerationLog> findByEntityTypeAndEntityIdOrderByCreatedAtDesc(
            ModerationEntityType entityType, Long entityId);
}
