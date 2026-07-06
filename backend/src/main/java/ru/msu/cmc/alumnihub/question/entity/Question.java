package ru.msu.cmc.alumnihub.question.entity;

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
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;

/**
 * A question submitted by an anonymous visitor to a specific alumni profile.
 */
@Entity
@Table(name = "questions")
@Getter
@Setter
@NoArgsConstructor
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "alumni_profile_id", nullable = false)
    private Long alumniProfileId;

    @Column(name = "sender_name")
    private String senderName;

    @Column(name = "sender_email")
    private String senderEmail;

    @Column(name = "question_text", nullable = false, columnDefinition = "text")
    private String questionText;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private QuestionStatus status = QuestionStatus.PENDING_MODERATION;

    @Enumerated(EnumType.STRING)
    @Column(name = "ai_moderation_status")
    private AiModerationStatus aiModerationStatus;

    @Column(name = "ai_moderation_reason", columnDefinition = "text")
    private String aiModerationReason;

    @Column(name = "admin_moderation_comment", columnDefinition = "text")
    private String adminModerationComment;

    @Column(name = "is_read_by_alumni", nullable = false)
    private boolean readByAlumni = false;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;
}
