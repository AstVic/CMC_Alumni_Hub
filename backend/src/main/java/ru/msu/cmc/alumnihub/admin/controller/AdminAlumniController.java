package ru.msu.cmc.alumnihub.admin.controller;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.msu.cmc.alumnihub.admin.dto.AdminAlumniDto;
import ru.msu.cmc.alumnihub.admin.service.AdminAlumniService;
import ru.msu.cmc.alumnihub.profile.dto.AlumniProfileDto;
import ru.msu.cmc.alumnihub.profile.dto.UpdateProfileRequest;

import java.util.List;

/**
 * Admin management of alumni accounts. Path {id} is the alumni user id.
 */
@RestController
@RequestMapping("/api/admin/alumni")
public class AdminAlumniController {

    private final AdminAlumniService adminAlumniService;

    public AdminAlumniController(AdminAlumniService adminAlumniService) {
        this.adminAlumniService = adminAlumniService;
    }

    @GetMapping
    public List<AdminAlumniDto> list() {
        return adminAlumniService.listAlumni();
    }

    @GetMapping("/{id}")
    public AlumniProfileDto get(@PathVariable Long id) {
        return adminAlumniService.getAlumniProfile(id);
    }

    @PutMapping("/{id}")
    public AlumniProfileDto update(@PathVariable Long id,
                                   @Valid @RequestBody UpdateProfileRequest request) {
        return adminAlumniService.updateAlumniProfile(id, request);
    }

    @PatchMapping("/{id}/block")
    public AdminAlumniDto block(@PathVariable Long id,
                                @RequestParam(defaultValue = "true") boolean blocked) {
        return adminAlumniService.setBlocked(id, blocked);
    }
}
