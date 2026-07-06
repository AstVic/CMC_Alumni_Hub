package ru.msu.cmc.alumnihub.tag.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.msu.cmc.alumnihub.tag.entity.Tag;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface TagRepository extends JpaRepository<Tag, Long> {

    Optional<Tag> findBySlug(String slug);

    boolean existsBySlug(String slug);

    List<Tag> findBySlugIn(Set<String> slugs);

    List<Tag> findAllByOrderByCategoryAscNameAsc();
}
