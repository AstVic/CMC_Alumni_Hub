package ru.msu.cmc.alumnihub.invite.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.msu.cmc.alumnihub.common.exception.NotFoundException;
import ru.msu.cmc.alumnihub.config.properties.AppProperties;
import ru.msu.cmc.alumnihub.email.EmailService;
import ru.msu.cmc.alumnihub.invite.dto.RegisterByInviteRequest;
import ru.msu.cmc.alumnihub.invite.entity.AlumniInvite;
import ru.msu.cmc.alumnihub.invite.entity.InviteStatus;
import ru.msu.cmc.alumnihub.invite.repository.AlumniInviteRepository;
import ru.msu.cmc.alumnihub.profile.repository.AlumniProfileRepository;
import ru.msu.cmc.alumnihub.security.JwtService;
import ru.msu.cmc.alumnihub.user.dto.TokenResponse;
import ru.msu.cmc.alumnihub.user.entity.Role;
import ru.msu.cmc.alumnihub.user.entity.User;
import ru.msu.cmc.alumnihub.user.repository.UserRepository;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InviteServiceTest {

    @Mock AlumniInviteRepository inviteRepository;
    @Mock UserRepository userRepository;
    @Mock AlumniProfileRepository profileRepository;
    @Mock InviteTokenGenerator tokenGenerator;
    @Mock InviteEmailComposer emailComposer;
    @Mock EmailService emailService;
    @Mock PasswordEncoder passwordEncoder;
    @Mock JwtService jwtService;
    private InviteService inviteService;

    @BeforeEach
    void setUp() {
        AppProperties appProperties = new AppProperties(
                "http://localhost:5173", false, null, null,
                new AppProperties.Invite(7), null, null, null);
        inviteService = new InviteService(
                inviteRepository,
                userRepository,
                profileRepository,
                tokenGenerator,
                emailComposer,
                emailService,
                passwordEncoder,
                jwtService,
                appProperties);
    }

    @Test
    void adminInviteCreatesAdminWithoutAlumniProfile() {
        AlumniInvite invite = activeInvite(Role.ADMIN);
        when(tokenGenerator.hash("raw-token")).thenReturn("token-hash");
        when(inviteRepository.findByTokenHash("token-hash")).thenReturn(Optional.of(invite));
        when(userRepository.existsByEmail(invite.getEmail())).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("password-hash");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User saved = invocation.getArgument(0);
            saved.setId(42L);
            return saved;
        });
        when(jwtService.generateAccessToken(any(User.class))).thenReturn("access");
        when(jwtService.generateRefreshToken(any(User.class))).thenReturn("refresh");
        when(jwtService.getAccessTtlSeconds()).thenReturn(900L);

        TokenResponse response = inviteService.registerByInvite(new RegisterByInviteRequest(
                "raw-token", "", "password123", "password123", true));

        assertEquals(Role.ADMIN, response.user().role());
        assertEquals(InviteStatus.USED, invite.getStatus());
        verify(profileRepository, never()).save(any());
    }

    @Test
    void alumniEndpointCannotManageAdminInvite() {
        AlumniInvite invite = activeInvite(Role.ADMIN);
        invite.setId(5L);
        when(inviteRepository.findById(5L)).thenReturn(Optional.of(invite));

        assertThrows(NotFoundException.class, () -> inviteService.resend(5L, Role.ALUMNI));

        verify(tokenGenerator, never()).generateRawToken();
    }

    private AlumniInvite activeInvite(Role role) {
        AlumniInvite invite = new AlumniInvite();
        invite.setEmail("new-admin@example.com");
        invite.setRole(role);
        invite.setStatus(InviteStatus.SENT);
        invite.setExpiresAt(Instant.now().plusSeconds(3600));
        return invite;
    }
}
