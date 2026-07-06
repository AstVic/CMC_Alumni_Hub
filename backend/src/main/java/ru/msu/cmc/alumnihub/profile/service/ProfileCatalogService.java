package ru.msu.cmc.alumnihub.profile.service;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.msu.cmc.alumnihub.common.dto.PageResponse;
import ru.msu.cmc.alumnihub.common.exception.NotFoundException;
import ru.msu.cmc.alumnihub.profile.dto.ProfileCardDto;
import ru.msu.cmc.alumnihub.profile.dto.ProfileDetailDto;
import ru.msu.cmc.alumnihub.profile.entity.AlumniProfile;
import ru.msu.cmc.alumnihub.profile.entity.ProfileStatus;
import ru.msu.cmc.alumnihub.profile.repository.AlumniProfileRepository;

import java.util.Set;

/**
 * Read-only public catalog: filtering, sorting, pagination and detail lookup.
 */
@Service
public class ProfileCatalogService {

    private static final int MAX_PAGE_SIZE = 60;

    private final AlumniProfileRepository profileRepository;

    public ProfileCatalogService(AlumniProfileRepository profileRepository) {
        this.profileRepository = profileRepository;
    }

    @Transactional(readOnly = true)
    public PageResponse<ProfileCardDto> search(String search,
                                               Set<String> tags,
                                               Integer graduationYear,
                                               String company,
                                               String sort,
                                               int page,
                                               int size) {
        Specification<AlumniProfile> spec = Specification.allOf(
                ProfileSpecifications.published(),
                ProfileSpecifications.search(search),
                ProfileSpecifications.hasAnyTag(tags),
                ProfileSpecifications.graduationYear(graduationYear),
                ProfileSpecifications.company(company));

        Pageable pageable = PageRequest.of(
                Math.max(page, 0),
                Math.min(Math.max(size, 1), MAX_PAGE_SIZE),
                CatalogSort.fromValue(sort).toSort());

        return PageResponse.from(profileRepository.findAll(spec, pageable), ProfileCardDto::from);
    }

    @Transactional(readOnly = true)
    public ProfileDetailDto getPublishedById(Long id) {
        AlumniProfile profile = profileRepository.findById(id)
                .filter(p -> p.getStatus() == ProfileStatus.PUBLISHED)
                .orElseThrow(() -> new NotFoundException("Карточка не найдена"));
        return ProfileDetailDto.from(profile);
    }
}
