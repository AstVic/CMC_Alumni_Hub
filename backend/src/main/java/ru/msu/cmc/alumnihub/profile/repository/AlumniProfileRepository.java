package ru.msu.cmc.alumnihub.profile.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import ru.msu.cmc.alumnihub.profile.entity.AlumniProfile;
import ru.msu.cmc.alumnihub.profile.entity.ProfileStatus;

import java.util.Optional;

public interface AlumniProfileRepository
        extends JpaRepository<AlumniProfile, Long>, JpaSpecificationExecutor<AlumniProfile> {

    Optional<AlumniProfile> findByUserId(Long userId);

    long countByStatus(ProfileStatus status);

    java.util.List<AlumniProfile> findAllByOrderByUpdatedAtDesc();

    java.util.List<AlumniProfile> findByStatusOrderByUpdatedAtDesc(ProfileStatus status);
}
