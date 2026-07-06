package ru.msu.cmc.alumnihub.invite.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.msu.cmc.alumnihub.invite.entity.AlumniInvite;
import ru.msu.cmc.alumnihub.invite.entity.InviteStatus;

import java.util.List;
import java.util.Optional;

public interface AlumniInviteRepository extends JpaRepository<AlumniInvite, Long> {

    Optional<AlumniInvite> findByTokenHash(String tokenHash);

    List<AlumniInvite> findAllByOrderByCreatedAtDesc();

    boolean existsByEmailAndStatusIn(String email, List<InviteStatus> statuses);

    long countByStatus(InviteStatus status);
}
