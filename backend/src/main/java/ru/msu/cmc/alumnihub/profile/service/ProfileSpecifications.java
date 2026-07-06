package ru.msu.cmc.alumnihub.profile.service;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import org.springframework.data.jpa.domain.Specification;
import ru.msu.cmc.alumnihub.profile.entity.AlumniProfile;
import ru.msu.cmc.alumnihub.profile.entity.ProfileStatus;
import ru.msu.cmc.alumnihub.tag.entity.Tag;

import java.util.Collection;

/**
 * Reusable JPA specifications for the public catalog. Always constrained to
 * PUBLISHED profiles.
 */
public final class ProfileSpecifications {

    private ProfileSpecifications() {
    }

    public static Specification<AlumniProfile> published() {
        return (root, query, cb) -> cb.equal(root.get("status"), ProfileStatus.PUBLISHED);
    }

    /** Case-insensitive search over full name, company and current position. */
    public static Specification<AlumniProfile> search(String term) {
        if (term == null || term.isBlank()) {
            return null;
        }
        String like = "%" + term.trim().toLowerCase() + "%";
        return (root, query, cb) -> cb.or(
                cb.like(cb.lower(root.get("fullName")), like),
                cb.like(cb.lower(root.get("company")), like),
                cb.like(cb.lower(root.get("currentPosition")), like));
    }

    public static Specification<AlumniProfile> graduationYear(Integer year) {
        if (year == null) {
            return null;
        }
        return (root, query, cb) -> cb.equal(root.get("graduationYear"), year);
    }

    public static Specification<AlumniProfile> company(String company) {
        if (company == null || company.isBlank()) {
            return null;
        }
        String like = "%" + company.trim().toLowerCase() + "%";
        return (root, query, cb) -> cb.like(cb.lower(root.get("company")), like);
    }

    /**
     * Profiles that have at least one of the given tag slugs. Uses an EXISTS
     * subquery so the root result set stays free of duplicates and pagination
     * counts remain correct.
     */
    public static Specification<AlumniProfile> hasAnyTag(Collection<String> slugs) {
        if (slugs == null || slugs.isEmpty()) {
            return null;
        }
        return (root, query, cb) -> {
            Subquery<Long> sub = query.subquery(Long.class);
            Root<AlumniProfile> subRoot = sub.from(AlumniProfile.class);
            Join<AlumniProfile, Tag> tagJoin = subRoot.join("tags");
            sub.select(subRoot.get("id"));
            Predicate sameProfile = cb.equal(subRoot.get("id"), root.get("id"));
            Predicate tagMatch = tagJoin.get("slug").in(slugs);
            sub.where(cb.and(sameProfile, tagMatch));
            return cb.exists(sub);
        };
    }
}
