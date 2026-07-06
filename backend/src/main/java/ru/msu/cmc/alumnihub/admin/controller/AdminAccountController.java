package ru.msu.cmc.alumnihub.admin.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.msu.cmc.alumnihub.admin.dto.AdminAccountDto;
import ru.msu.cmc.alumnihub.admin.dto.CreateAdminRequest;
import ru.msu.cmc.alumnihub.admin.service.AdminAccountService;
import ru.msu.cmc.alumnihub.security.CurrentUserService;

import java.util.List;

/**
 * Owner-only administration of admin accounts. The URL is guarded by
 * {@code hasRole('OWNER')} in the security config.
 */
@RestController
@RequestMapping("/api/admin/admins")
public class AdminAccountController {

    private final AdminAccountService adminAccountService;
    private final CurrentUserService currentUserService;

    public AdminAccountController(AdminAccountService adminAccountService,
                                 CurrentUserService currentUserService) {
        this.adminAccountService = adminAccountService;
        this.currentUserService = currentUserService;
    }

    @GetMapping
    public List<AdminAccountDto> list() {
        return adminAccountService.listAdmins();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AdminAccountDto create(@Valid @RequestBody CreateAdminRequest request) {
        return adminAccountService.createAdmin(request);
    }

    @PatchMapping("/{id}/block")
    public AdminAccountDto block(@PathVariable Long id,
                                 @RequestParam(defaultValue = "true") boolean blocked) {
        return adminAccountService.setBlocked(currentUserService.requireCurrentUserId(), id, blocked);
    }

    @PatchMapping("/{id}/transfer-ownership")
    public AdminAccountDto transferOwnership(@PathVariable Long id) {
        return adminAccountService.transferOwnership(currentUserService.requireCurrentUserId(), id);
    }
}
