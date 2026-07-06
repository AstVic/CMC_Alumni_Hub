package ru.msu.cmc.alumnihub.moderation;

import org.springframework.stereotype.Service;
import ru.msu.cmc.alumnihub.moderation.entity.ModerationEntityType;
import ru.msu.cmc.alumnihub.moderation.entity.ModerationLog;
import ru.msu.cmc.alumnihub.moderation.entity.ModeratorType;
import ru.msu.cmc.alumnihub.moderation.repository.ModerationLogRepository;

/**
 * Writes moderation decisions to the audit log.
 */
@Service
public class ModerationLogService {

    private final ModerationLogRepository repository;

    public ModerationLogService(ModerationLogRepository repository) {
        this.repository = repository;
    }

    public void log(ModerationEntityType entityType, Long entityId, ModeratorType moderatorType,
                    String decision, String reason) {
        ModerationLog entry = new ModerationLog();
        entry.setEntityType(entityType);
        entry.setEntityId(entityId);
        entry.setModeratorType(moderatorType);
        entry.setDecision(decision);
        entry.setReason(reason);
        repository.save(entry);
    }
}
