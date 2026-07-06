package ru.msu.cmc.alumnihub.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import ru.msu.cmc.alumnihub.auth.entity.PasswordResetToken;

import java.util.Optional;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {

    Optional<PasswordResetToken> findByTokenHash(String tokenHash);

    /** Invalidates any outstanding (unused) reset tokens for a user. */
    @Modifying
    @Query("update PasswordResetToken t set t.usedAt = CURRENT_TIMESTAMP "
            + "where t.userId = :userId and t.usedAt is null")
    void invalidateActiveForUser(Long userId);
}
