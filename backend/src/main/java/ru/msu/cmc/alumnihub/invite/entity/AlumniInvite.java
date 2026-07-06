package ru.msu.cmc.alumnihub.invite.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import ru.msu.cmc.alumnihub.user.entity.Role;

import java.time.Instant;

/**
 * One-time email invitation issued by an admin. Only the token hash is stored.
 */
@Entity
@Table(name = "alumni_invites")
@Getter
@Setter
@NoArgsConstructor
public class AlumniInvite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String email;

    @Column(name = "token_hash", nullable = false, unique = true)
    private String tokenHash;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InviteStatus status = InviteStatus.CREATED;

    /** Which kind of account this invite creates on registration. */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role = Role.ALUMNI;

    @Column(name = "created_by_admin_id", nullable = false)
    private Long createdByAdminId;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    @Column(name = "used_at")
    private Instant usedAt;

    @Column(name = "revoked_at")
    private Instant revokedAt;

    @Column(columnDefinition = "text")
    private String note;
}
