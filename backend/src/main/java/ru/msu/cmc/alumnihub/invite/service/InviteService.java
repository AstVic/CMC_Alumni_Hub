package ru.msu.cmc.alumnihub.invite.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.msu.cmc.alumnihub.common.exception.BadRequestException;
import ru.msu.cmc.alumnihub.common.exception.NotFoundException;
import ru.msu.cmc.alumnihub.config.properties.AppProperties;
import ru.msu.cmc.alumnihub.email.EmailService;
import ru.msu.cmc.alumnihub.invite.dto.CreateInviteRequest;
import ru.msu.cmc.alumnihub.invite.dto.InviteDto;
import ru.msu.cmc.alumnihub.invite.dto.InviteValidationResponse;
import ru.msu.cmc.alumnihub.invite.dto.RegisterByInviteRequest;
import ru.msu.cmc.alumnihub.invite.entity.AlumniInvite;
import ru.msu.cmc.alumnihub.invite.entity.InviteStatus;
import ru.msu.cmc.alumnihub.invite.repository.AlumniInviteRepository;
import ru.msu.cmc.alumnihub.profile.entity.AlumniProfile;
import ru.msu.cmc.alumnihub.profile.entity.ProfileStatus;
import ru.msu.cmc.alumnihub.profile.repository.AlumniProfileRepository;
import ru.msu.cmc.alumnihub.security.JwtService;
import ru.msu.cmc.alumnihub.user.dto.TokenResponse;
import ru.msu.cmc.alumnihub.user.dto.UserDto;
import ru.msu.cmc.alumnihub.user.entity.Role;
import ru.msu.cmc.alumnihub.user.entity.User;
import ru.msu.cmc.alumnihub.user.repository.UserRepository;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

/**
 * Invite lifecycle: creation, resend, revoke, validation and account
 * registration. Only token hashes are stored; email and role always come from
 * the invite, never from the client.
 */
@Service
public class InviteService {

    private static final Logger log = LoggerFactory.getLogger(InviteService.class);
    private static final List<InviteStatus> ACTIVE_STATUSES =
            List.of(InviteStatus.CREATED, InviteStatus.SENT);

    private final AlumniInviteRepository inviteRepository;
    private final UserRepository userRepository;
    private final AlumniProfileRepository profileRepository;
    private final InviteTokenGenerator tokenGenerator;
    private final InviteEmailComposer emailComposer;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final long expiryDays;

    public InviteService(AlumniInviteRepository inviteRepository,
                         UserRepository userRepository,
                         AlumniProfileRepository profileRepository,
                         InviteTokenGenerator tokenGenerator,
                         InviteEmailComposer emailComposer,
                         EmailService emailService,
                         PasswordEncoder passwordEncoder,
                         JwtService jwtService,
                         AppProperties appProperties) {
        this.inviteRepository = inviteRepository;
        this.userRepository = userRepository;
        this.profileRepository = profileRepository;
        this.tokenGenerator = tokenGenerator;
        this.emailComposer = emailComposer;
        this.emailService = emailService;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.expiryDays = appProperties.invite().expiryDays();
    }

    @Transactional
    public InviteDto createInvite(CreateInviteRequest request, Long adminId, Role role) {
        String email = request.email().trim().toLowerCase();
        if (userRepository.existsByEmail(email)) {
            throw new BadRequestException("Пользователь с таким email уже зарегистрирован");
        }
        if (inviteRepository.existsByEmailAndStatusIn(email, ACTIVE_STATUSES)) {
            throw new BadRequestException("Для этого email уже есть активное приглашение");
        }

        String rawToken = tokenGenerator.generateRawToken();
        AlumniInvite invite = new AlumniInvite();
        invite.setEmail(email);
        invite.setRole(role);
        invite.setTokenHash(tokenGenerator.hash(rawToken));
        invite.setStatus(InviteStatus.CREATED);
        invite.setCreatedByAdminId(adminId);
        invite.setExpiresAt(Instant.now().plus(expiryDays, ChronoUnit.DAYS));
        invite.setNote(request.note());
        inviteRepository.save(invite);
        log.info("Invite created id={} email={} role={} by admin={}", invite.getId(), email, role, adminId);

        deliver(invite, rawToken);
        return InviteDto.from(invite);
    }

    @Transactional(readOnly = true)
    public List<InviteDto> listInvites(Role role) {
        return inviteRepository.findByRoleOrderByCreatedAtDesc(role).stream()
                .map(this::withLazyExpiry)
                .map(InviteDto::from)
                .toList();
    }

    @Transactional
    public InviteDto resend(Long id, Role expectedRole) {
        AlumniInvite invite = getInvite(id, expectedRole);
        if (invite.getStatus() == InviteStatus.USED) {
            throw new BadRequestException("Приглашение уже использовано");
        }
        if (invite.getStatus() == InviteStatus.REVOKED) {
            throw new BadRequestException("Приглашение отозвано");
        }
        // Issue a fresh token; the previous link stops working.
        String rawToken = tokenGenerator.generateRawToken();
        invite.setTokenHash(tokenGenerator.hash(rawToken));
        invite.setExpiresAt(Instant.now().plus(expiryDays, ChronoUnit.DAYS));
        invite.setStatus(InviteStatus.CREATED);
        log.info("Invite resend id={} email={}", invite.getId(), invite.getEmail());

        deliver(invite, rawToken);
        return InviteDto.from(invite);
    }

    @Transactional
    public InviteDto revoke(Long id, Role expectedRole) {
        AlumniInvite invite = getInvite(id, expectedRole);
        if (invite.getStatus() == InviteStatus.USED) {
            throw new BadRequestException("Нельзя отозвать использованное приглашение");
        }
        invite.setStatus(InviteStatus.REVOKED);
        invite.setRevokedAt(Instant.now());
        log.info("Invite revoked id={} email={}", invite.getId(), invite.getEmail());
        return InviteDto.from(invite);
    }

    @Transactional
    public InviteValidationResponse validate(String rawToken) {
        return findValidatable(rawToken)
                .map(this::classify)
                .orElseGet(() -> InviteValidationResponse.of(InviteValidationResponse.Result.INVALID));
    }

    @Transactional
    public TokenResponse registerByInvite(RegisterByInviteRequest request) {
        AlumniInvite invite = findValidatable(request.token())
                .orElseThrow(() -> new BadRequestException("Недействительное приглашение"));

        InviteValidationResponse.Result result = classify(invite).result();
        if (result != InviteValidationResponse.Result.VALID) {
            throw new BadRequestException("Приглашение недействительно: " + result);
        }
        if (userRepository.existsByEmail(invite.getEmail())) {
            throw new BadRequestException("Аккаунт с этим email уже существует");
        }
        if (invite.getRole() == Role.ALUMNI
                && (request.fullName() == null || request.fullName().isBlank())) {
            throw new BadRequestException("Укажите ФИО выпускника");
        }

        // Email and role always come from the invite — never from the request.
        User user = new User();
        user.setEmail(invite.getEmail());
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        user.setRole(invite.getRole());
        user.setEnabled(true);
        userRepository.save(user);

        // Only alumni get a profile card; admins do not.
        if (invite.getRole() == Role.ALUMNI) {
            AlumniProfile profile = new AlumniProfile();
            profile.setUserId(user.getId());
            profile.setFullName(request.fullName().trim());
            profile.setStatus(ProfileStatus.DRAFT);
            profileRepository.save(profile);
        }

        invite.setStatus(InviteStatus.USED);
        invite.setUsedAt(Instant.now());
        log.info("Invite used id={} email={} -> userId={}", invite.getId(), invite.getEmail(), user.getId());

        String access = jwtService.generateAccessToken(user);
        String refresh = jwtService.generateRefreshToken(user);
        return TokenResponse.of(access, refresh, jwtService.getAccessTtlSeconds(), UserDto.from(user));
    }

    // ---- helpers ----

    private void deliver(AlumniInvite invite, String rawToken) {
        emailService.send(invite.getEmail(),
                emailComposer.subject(invite.getRole()),
                emailComposer.body(rawToken, invite.getRole()));
        invite.setStatus(InviteStatus.SENT);
        log.info("Invite sent id={} email={}", invite.getId(), invite.getEmail());
    }

    private Optional<AlumniInvite> findValidatable(String rawToken) {
        if (rawToken == null || rawToken.isBlank()) {
            return Optional.empty();
        }
        return inviteRepository.findByTokenHash(tokenGenerator.hash(rawToken));
    }

    /** Marks a persisted invite EXPIRED lazily when its expiry has passed. */
    private AlumniInvite withLazyExpiry(AlumniInvite invite) {
        if ((invite.getStatus() == InviteStatus.CREATED || invite.getStatus() == InviteStatus.SENT)
                && invite.getExpiresAt().isBefore(Instant.now())) {
            invite.setStatus(InviteStatus.EXPIRED);
        }
        return invite;
    }

    private InviteValidationResponse classify(AlumniInvite invite) {
        withLazyExpiry(invite);
        return switch (invite.getStatus()) {
            case CREATED, SENT -> InviteValidationResponse.valid(invite.getEmail(), invite.getRole());
            case USED -> InviteValidationResponse.of(InviteValidationResponse.Result.USED);
            case REVOKED -> InviteValidationResponse.of(InviteValidationResponse.Result.REVOKED);
            case EXPIRED -> InviteValidationResponse.of(InviteValidationResponse.Result.EXPIRED);
        };
    }

    private AlumniInvite getInvite(Long id, Role expectedRole) {
        AlumniInvite invite = inviteRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Приглашение не найдено"));
        // Do not let an endpoint for one invite type manage another type by ID.
        if (invite.getRole() != expectedRole) {
            throw new NotFoundException("Приглашение не найдено");
        }
        return invite;
    }
}
