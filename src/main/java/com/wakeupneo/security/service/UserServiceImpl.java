package com.wakeupneo.security.service;

import com.wakeupneo.security.exception.AlreadyActivatedAccountException;
import com.wakeupneo.security.model.*;
import com.wakeupneo.security.repository.TokenRepository;
import com.wakeupneo.security.repository.UserRepository;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidParameterException;
import java.time.Instant;
import java.util.*;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

@Slf4j
@Service
@Transactional
public class UserServiceImpl implements UserService, UserDetailsService {

    final String SPECIFIED_USERNAME_DOES_NOT_EXIST = "A user with the specified username does not exist: %s";
    final String SPECIFIED_EMAIL_DOES_NOT_EXIST = "A user with the specified email does not exist: %s";
    final String USER_NOT_FOUND_WITH_USERNAME = "User not found with username : %s";
    final String FILE_SAVED_IN_FILE_SYSTEM = "File saved in file system.";
    final String USERNAME_ALREADY_EXIST = "A user with the specified username already exist.";
    final String ACCOUNT_ALREADY_ACTIVE = "The account is already active.";
    final String USER_NOT_FOUND_WITH_ID = "User not found with id : ";
    final String EMAIL_ALREADY_EXIST = "A user with the specified email already exist.";
    final String DIRECTORY_CREATED = "Directory Created!";
    final String USER_IMAGE_PATH = "api/users/image/";
    final String USER_NOT_FOUND = "User not found!";
    final String INVALID_TOKEN = "Invalid Token!";
    final String JPG_EXTENSION = "jpg";
    final String FORWARD_SLASH = "/";
    final String PROFILE_PATH = "profile/";
    final String USER_FOLDER = System.getProperty("user.home") + "/open-course-platform/user/";
    final String DOT = ".";

    private final MailService mailService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final LoginAttemptService loginAttemptService;
    private final TokenRepository tokenRepository;
    private final RoleService roleService;
    private final PermissionService permissionService;

    public UserServiceImpl(UserRepository userRepository,
                           PasswordEncoder passwordEncoder,
                           LoginAttemptService loginAttemptService,
                           MailService mailService,
                           TokenRepository tokenRepository,
                           RoleService roleService,
                           PermissionService permissionService) {
        this.mailService = mailService;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.loginAttemptService = loginAttemptService;
        this.tokenRepository = tokenRepository;
        this.roleService = roleService;
        this.permissionService = permissionService;
    }

    @Transactional
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        final User user = userRepository.findUserByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(String.format(USER_NOT_FOUND_WITH_USERNAME, username)));
        validateUserAttempt(user);
        return new UserPrincipal(user);
    }

    @Transactional
    @Override
    public User registerUser(User user) throws EntityExistsException {
        checkUserExistence(user);
        user = createBasicUser(user);
        sendActivationMail(user.getId());
        return user;
    }

    public void checkUserExistence(User user) {
        if (isUserExistWithEmail(user.getEmail())) {
            throw new EntityExistsException(EMAIL_ALREADY_EXIST);
        }
        if (isUserExistWithUsername(user.getUsername())) {
            throw new EntityExistsException(USERNAME_ALREADY_EXIST);
        }
    }

    @Transactional
    public User createBasicUser(User user) {

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRoles(Collections.singleton(roleService.getRole("ROLE_USER")));
//        user.setProfileImageUrl(getDefaultProfileImageUrl(user.getUsername()));
        user.setLastLoginDate(new Date(Instant.now().toEpochMilli()));
        updateLastLoginDates(user);
        return userRepository.save(user);
    }

    public String getDefaultProfileImageUrl(String username) {
        // TODO host url is not working, it needs to be fixed.
        return ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .path(USER_IMAGE_PATH + PROFILE_PATH + username)
                .build()
                .toUriString();
    }

    @Transactional
    public void updateLastLoginDates(User user) {
        user.setLastLoginDateDisplay(user.getLastLoginDate());
        user.setLastLoginDate(new Date(Instant.now().toEpochMilli()));
    }

    @Transactional
    @Override
    public void sendActivationMail(UUID uuid) {
        User user = getUserById(uuid);
        if (user.isVerified()) {
            throw new AlreadyActivatedAccountException(ACCOUNT_ALREADY_ACTIVE);
        }
        final String token = UUID.randomUUID().toString();
        final Token activationToken = createActivationToken(user, token);
        tokenRepository.save(activationToken);
        mailService.sendActivationMail(token, user);
    }

    @Override
    public User getUserById(UUID uuid) {
        return userRepository.findUserById(uuid)
                .orElseThrow(() -> new EntityNotFoundException(USER_NOT_FOUND_WITH_ID + uuid));
    }

    public Token createActivationToken(User user, String token) {
        return Token.builder()
                .token(token)
                .user(user)
                .expiryDate(Token.calculateExpiryDate())
                .type(TokenType.ACTIVATION)
                .build();
    }

    @Override
    public boolean isUserExistWithEmail(String email) {
        return userRepository.findUserByEmail(email).isPresent();
    }

    @Override
    public boolean isUserExistWithUsername(String username) {
        return userRepository.findUserByUsername(username).isPresent();
    }

    @Transactional
    @Override
    public User getUserByEmail(String email) {
        User user = userRepository.findUserByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException(String.format(SPECIFIED_EMAIL_DOES_NOT_EXIST, email)));
        validateUserAttempt(user);
        updateLastLoginDates(user);
        userRepository.save(user);
        return user;
    }

    @Transactional
    @Override
    public User getUserByUsername(String username) {
        User user = userRepository.findUserByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format(SPECIFIED_USERNAME_DOES_NOT_EXIST, username)));
        validateUserAttempt(user);
        updateLastLoginDates(user);
        userRepository.save(user);
        return user;
    }

    @Transactional
    public void validateUserAttempt(User user) {
        if (!user.isAccountLocked()) {
            user.setAccountLocked(loginAttemptService.hasExceededMaxAttempts(user.getUsername()));
        } else {
            loginAttemptService.evictUserFromLoginAttemptCache(user.getUsername());
        }
    }

    @Transactional
    @Override
    public User updateProfile(User currentUser, User updateUser) {
        checkUserExistence(updateUser);
        if (StringUtils.isNotBlank(updateUser.getEmail())) {
            currentUser.setEmail(updateUser.getEmail());
        }
        if (StringUtils.isNotBlank(updateUser.getName())) {
            currentUser.setName(updateUser.getName());
        }
        if (StringUtils.isNotBlank(updateUser.getSurname())) {
            currentUser.setSurname(updateUser.getSurname());
        }
        return userRepository.save(currentUser);
    }

    @Transactional
    @Override
    public void sendPasswordResetMail(String email) {
        User user = userRepository.findUserByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException(String.format(SPECIFIED_EMAIL_DOES_NOT_EXIST, email)));
        final String token = UUID.randomUUID().toString();
        saveAccessToken(user, token);
        mailService.sendResetTokenEmail(token, user);
    }

    @Transactional
    public void saveAccessToken(User user, String token) {
        final Token accessToken = Token.builder()
                .token(token)
                .user(user)
                .expiryDate(Token.calculateExpiryDate())
                .type(TokenType.PASSWORD_RESET)
                .build();
        tokenRepository.save(accessToken);
    }

    @Transactional
    @Override
    public Token verifyPasswordResetToken(String token) {
        final Token tkn = tokenRepository.findByTokenAndType(token, TokenType.PASSWORD_RESET)
                .orElseThrow(() -> new EntityNotFoundException(INVALID_TOKEN));

        validateToken(tkn);

        return tkn;
    }

    @Transactional
    @Override
    public User updateForgottenPassword(final String token, final String password) {
        User user = Optional.of(verifyPasswordResetToken(token).getUser())
                .orElseThrow(() -> new EntityNotFoundException(USER_NOT_FOUND));

        user.setPassword(passwordEncoder.encode(password));
        user = userRepository.save(user);
        tokenRepository.deleteByUserIdAndType(user.getId(), TokenType.PASSWORD_RESET);

        return user;
    }

    @Transactional
    @Override
    public void changeMyPassword(User user, String oldPassword, String newPassword) {
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    @Transactional
    @Override
    public boolean verifyAccount(String token) {
        final Token passToken = tokenRepository.findByTokenAndType(token, TokenType.ACTIVATION)
                .orElseThrow(() -> new EntityNotFoundException(INVALID_TOKEN));

        validateToken(passToken);

        User user = Optional.of(passToken.getUser())
                .orElseThrow(() -> new EntityNotFoundException(USER_NOT_FOUND));

        user.setVerified(true);
        user = userRepository.save(user);
        tokenRepository.deleteByUserIdAndType(user.getId(), TokenType.ACTIVATION);

        return true;
    }

    private void validateToken(Token token) {
        if (token.getExpiryDate() == null) {
            throw new InvalidParameterException("Token expire date error.");
        } else if (token.getExpiryDate().toInstant().isBefore(Instant.now())) {
            throw new InvalidParameterException("Your token has expired. Please register again.");
        }
    }

    @Transactional
    @Override
    public User updateProfileImage(User user, MultipartFile profileImage) throws IOException {
        String profileImageUrl = saveProfileImage(user.getUsername(), profileImage);
        user.setProfileImageUrl(profileImageUrl);
        user = userRepository.save(user);
        return user;
    }

    private String saveProfileImage(String username, MultipartFile profileImage) throws IOException {
        if (profileImage == null) return null;
        Path userFolder = Paths.get(USER_FOLDER + username).toAbsolutePath().normalize();
        if (Files.notExists(userFolder)) {
            Files.createDirectories(userFolder);
            log.info(DIRECTORY_CREATED + userFolder);
        }
        Path profileImagePath = Paths.get(userFolder + FORWARD_SLASH + username + DOT + JPG_EXTENSION);
        Files.deleteIfExists(profileImagePath);
        Files.copy(profileImage.getInputStream(), profileImagePath, REPLACE_EXISTING);
        log.info(FILE_SAVED_IN_FILE_SYSTEM);

        return ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .path(USER_IMAGE_PATH + PROFILE_PATH + username)
                .toUriString();
    }

    @Override
    public Collection<Role> getRoles() {
        return roleService.getRoles();
    }

    @Override
    public Collection<String> getPermissions() {
        return permissionService.getPermissions();
    }


}
