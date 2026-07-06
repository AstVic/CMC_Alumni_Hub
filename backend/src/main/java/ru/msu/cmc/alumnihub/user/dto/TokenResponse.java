package ru.msu.cmc.alumnihub.user.dto;

/**
 * JWT pair returned on login/refresh.
 */
public record TokenResponse(
        String accessToken,
        String refreshToken,
        String tokenType,
        long expiresIn,
        UserDto user) {

    public static TokenResponse of(String access, String refresh, long expiresIn, UserDto user) {
        return new TokenResponse(access, refresh, "Bearer", expiresIn, user);
    }
}
