package ru.msu.cmc.alumnihub.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.msu.cmc.alumnihub.user.entity.Role;
import ru.msu.cmc.alumnihub.user.entity.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    long countByRole(Role role);

    List<User> findByRoleOrderByIdAsc(Role role);

    Optional<User> findFirstByRoleOrderByIdAsc(Role role);

    Optional<User> findByRoleAndOwnerTrue(Role role);

    boolean existsByRoleAndOwnerTrue(Role role);
}
