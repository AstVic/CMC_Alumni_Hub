package ru.msu.cmc.alumnihub.admin.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.msu.cmc.alumnihub.admin.dto.AdminAccountDto;
import ru.msu.cmc.alumnihub.admin.dto.CreateAdminRequest;
import ru.msu.cmc.alumnihub.common.exception.BadRequestException;
import ru.msu.cmc.alumnihub.common.exception.NotFoundException;
import ru.msu.cmc.alumnihub.user.entity.Role;
import ru.msu.cmc.alumnihub.user.entity.User;
import ru.msu.cmc.alumnihub.user.repository.UserRepository;

import java.util.List;

/**
 * Owner-only management of administrator accounts: listing, creating other
 * admins, blocking them, and transferring the "main admin" (owner) status.
 */
@Service
public class AdminAccountService {

    private static final Logger log = LoggerFactory.getLogger(AdminAccountService.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminAccountService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(readOnly = true)
    public List<AdminAccountDto> listAdmins() {
        return userRepository.findByRoleOrderByIdAsc(Role.ADMIN).stream()
                .map(AdminAccountDto::from)
                .toList();
    }

    @Transactional
    public AdminAccountDto createAdmin(CreateAdminRequest request) {
        String email = request.email().trim().toLowerCase();
        if (userRepository.existsByEmail(email)) {
            throw new BadRequestException("Пользователь с таким email уже существует");
        }
        User admin = new User();
        admin.setEmail(email);
        admin.setPasswordHash(passwordEncoder.encode(request.password()));
        admin.setRole(Role.ADMIN);
        admin.setEnabled(true);
        admin.setOwner(false);
        userRepository.save(admin);
        log.info("Owner created new admin: {}", email);
        return AdminAccountDto.from(admin);
    }

    @Transactional
    public AdminAccountDto setBlocked(Long currentOwnerId, Long targetId, boolean blocked) {
        User target = requireAdmin(targetId);
        if (target.getId().equals(currentOwnerId)) {
            throw new BadRequestException("Нельзя заблокировать самого себя");
        }
        if (target.isOwner()) {
            throw new BadRequestException("Нельзя заблокировать главного администратора");
        }
        target.setEnabled(!blocked);
        log.info("Owner set admin {} blocked={}", target.getEmail(), blocked);
        return AdminAccountDto.from(target);
    }

    /**
     * Transfers the owner (main admin) status from the current owner to another
     * admin. The current owner becomes a regular admin.
     */
    @Transactional
    public AdminAccountDto transferOwnership(Long currentOwnerId, Long targetId) {
        if (targetId.equals(currentOwnerId)) {
            throw new BadRequestException("Вы уже являетесь главным администратором");
        }
        User target = requireAdmin(targetId);
        if (!target.isEnabled()) {
            throw new BadRequestException("Нельзя передать права заблокированному администратору");
        }
        User currentOwner = userRepository.findById(currentOwnerId)
                .orElseThrow(() -> new NotFoundException("Текущий владелец не найден"));

        currentOwner.setOwner(false);
        target.setOwner(true);
        log.info("Ownership transferred from {} to {}", currentOwner.getEmail(), target.getEmail());
        return AdminAccountDto.from(target);
    }

    private User requireAdmin(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Администратор не найден"));
        if (user.getRole() != Role.ADMIN) {
            throw new BadRequestException("Пользователь не является администратором");
        }
        return user;
    }
}
