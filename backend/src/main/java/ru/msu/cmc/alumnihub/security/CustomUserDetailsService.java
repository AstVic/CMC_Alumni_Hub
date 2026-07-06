package ru.msu.cmc.alumnihub.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.msu.cmc.alumnihub.user.repository.UserRepository;

/**
 * Loads users by email for authentication.
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email)
                .map(SecurityUser::new)
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден: " + email));
    }

    /** Loads a user by primary key — used by the JWT filter (subject = user id). */
    public UserDetails loadUserById(Long id) {
        return userRepository.findById(id)
                .map(SecurityUser::new)
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден: id=" + id));
    }
}
