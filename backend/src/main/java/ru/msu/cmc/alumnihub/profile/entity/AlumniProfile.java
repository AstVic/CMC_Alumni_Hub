package ru.msu.cmc.alumnihub.profile.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import ru.msu.cmc.alumnihub.tag.entity.Tag;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

/**
 * Alumni card. One profile per alumni user.
 *
 * <p>{@code questionCount} is denormalised for cheap popularity sorting; it is
 * incremented when a question transitions to {@code VISIBLE_TO_ALUMNI}.
 */
@Entity
@Table(name = "alumni_profiles")
@Getter
@Setter
@NoArgsConstructor
public class AlumniProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false, unique = true)
    private Long userId;

    @Column(name = "full_name")
    private String fullName;

    @Column(name = "graduation_year")
    private Integer graduationYear;

    @Column
    private String department;

    @Column(name = "current_position")
    private String currentPosition;

    @Column
    private String company;

    @Column
    private String city;

    @Column
    private String country;

    @Column(name = "career_description", columnDefinition = "text")
    private String careerDescription;

    @Column(name = "interests_description", columnDefinition = "text")
    private String interestsDescription;

    @Column(name = "photo_url")
    private String photoUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProfileStatus status = ProfileStatus.DRAFT;

    @Column(name = "moderation_comment", columnDefinition = "text")
    private String moderationComment;

    @Column(name = "question_count", nullable = false)
    private int questionCount = 0;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "alumni_profile_tags",
            joinColumns = @JoinColumn(name = "profile_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id"))
    private Set<Tag> tags = new HashSet<>();

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @Column(name = "published_at")
    private Instant publishedAt;
}
