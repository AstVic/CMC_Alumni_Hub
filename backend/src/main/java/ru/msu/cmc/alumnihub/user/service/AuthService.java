package ru.msu.cmc.alumnihub.user.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import ru.msu.cmc.alumnihub.common.exception.BadRequestException;
import ru.msu.cmc.alumnihub.common.exception.ForbiddenException;
import ru.msu.cmc.alumnihub.security.JwtService;
import ru.msu.cmc.alumnihub.user.dto.LoginRequest;
import ru.msu.cmc.alumnihub.user.dto.TokenResponse;
import ru.msu.cmc.alumnihub.user.dto.UserDto;
import ru.msu.cmc.alumnihub.user.entity.User;
import ru.msu.cmc.alumnihub.user.repository.UserRepository;

/**
 * Authentication use cases: login and token refresh.
 */
@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserRepository userRepository;

    public AuthService(AuthenticationManager authenticationManager,
                       JwtService jwtService,
                       UserRepository userRepository) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.userRepository = userRepository;
    }

    public TokenResponse login(LoginRequest request) {
        // Throws BadCredentialsException on failure -> handled globally as 401.
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password()));

        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new BadRequestException("Пользователь не найден"));
        return issueTokens(user);
    }

    public TokenResponse refresh(String refreshToken) {
        final Claims claims;
        try {
            claims = jwtService.parse(refreshToken);
        } catch (JwtException | IllegalArgumentException ex) {
            throw new ForbiddenException("Недействительный refresh-токен");
        }
        if (!jwtService.isRefreshToken(claims)) {
            throw new ForbiddenException("Ожидался refresh-токен");
        }
        User user = userRepository.findById(Long.valueOf(claims.getSubject()))
                .orElseThrow(() -> new ForbiddenException("Пользователь не найден"));
        if (!user.isEnabled()) {
            throw new ForbiddenException("Аккаунт заблокирован");
        }
        return issueTokens(user);
    }

    private TokenResponse issueTokens(User user) {
        String access = jwtService.generateAccessToken(user);
        String refresh = jwtService.generateRefreshToken(user);
        return TokenResponse.of(access, refresh, jwtService.getAccessTtlSeconds(), UserDto.from(user));
    }
}
