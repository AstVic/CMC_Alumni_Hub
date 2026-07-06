package ru.msu.cmc.alumnihub.user.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.msu.cmc.alumnihub.common.exception.BadRequestException;
import ru.msu.cmc.alumnihub.security.JwtService;
import ru.msu.cmc.alumnihub.user.dto.ChangePasswordRequest;
import ru.msu.cmc.alumnihub.user.entity.User;
import ru.msu.cmc.alumnihub.user.repository.UserRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock AuthenticationManager authenticationManager;
    @Mock JwtService jwtService;
    @Mock UserRepository userRepository;
    @Mock PasswordEncoder passwordEncoder;

    private AuthService authService;
    private User user;

    @BeforeEach
    void setUp() {
        authService = new AuthService(
                authenticationManager, jwtService, userRepository, passwordEncoder);
        user = new User();
        user.setId(10L);
        user.setPasswordHash("old-hash");
        when(userRepository.findById(10L)).thenReturn(Optional.of(user));
    }

    @Test
    void changePasswordRejectsWrongCurrentPassword() {
        when(passwordEncoder.matches("wrong", "old-hash")).thenReturn(false);

        assertThrows(BadRequestException.class, () -> authService.changePassword(
                10L, new ChangePasswordRequest("wrong", "new-password", "new-password")));

        verify(passwordEncoder, never()).encode("new-password");
        assertEquals("old-hash", user.getPasswordHash());
    }

    @Test
    void changePasswordStoresEncodedNewPassword() {
        when(passwordEncoder.matches("current", "old-hash")).thenReturn(true);
        when(passwordEncoder.matches("new-password", "old-hash")).thenReturn(false);
        when(passwordEncoder.encode("new-password")).thenReturn("new-hash");

        authService.changePassword(
                10L, new ChangePasswordRequest("current", "new-password", "new-password"));

        assertEquals("new-hash", user.getPasswordHash());
    }
}
