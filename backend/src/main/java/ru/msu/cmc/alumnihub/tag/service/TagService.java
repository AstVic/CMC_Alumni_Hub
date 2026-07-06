package ru.msu.cmc.alumnihub.tag.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.msu.cmc.alumnihub.common.exception.BadRequestException;
import ru.msu.cmc.alumnihub.common.exception.NotFoundException;
import ru.msu.cmc.alumnihub.tag.dto.TagDto;
import ru.msu.cmc.alumnihub.tag.dto.TagRequest;
import ru.msu.cmc.alumnihub.tag.entity.Tag;
import ru.msu.cmc.alumnihub.tag.repository.TagRepository;

import java.util.List;

/**
 * Tag catalog management.
 */
@Service
public class TagService {

    private final TagRepository tagRepository;

    public TagService(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }

    @Transactional(readOnly = true)
    public List<TagDto> listAll() {
        return tagRepository.findAllByOrderByCategoryAscNameAsc().stream()
                .map(TagDto::from)
                .toList();
    }

    @Transactional
    public TagDto create(TagRequest request) {
        if (tagRepository.existsBySlug(request.slug())) {
            throw new BadRequestException("Тег с таким slug уже существует");
        }
        Tag tag = new Tag();
        apply(tag, request);
        return TagDto.from(tagRepository.save(tag));
    }

    @Transactional
    public TagDto update(Long id, TagRequest request) {
        Tag tag = getTag(id);
        if (!tag.getSlug().equals(request.slug()) && tagRepository.existsBySlug(request.slug())) {
            throw new BadRequestException("Тег с таким slug уже существует");
        }
        apply(tag, request);
        return TagDto.from(tag);
    }

    @Transactional
    public void delete(Long id) {
        Tag tag = getTag(id);
        tagRepository.delete(tag);
    }

    private void apply(Tag tag, TagRequest request) {
        tag.setName(request.name().trim());
        tag.setSlug(request.slug().trim());
        tag.setCategory(request.category());
    }

    private Tag getTag(Long id) {
        return tagRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Тег не найден"));
    }
}
