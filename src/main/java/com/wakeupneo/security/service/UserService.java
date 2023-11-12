package com.wakeupneo.security.service;

import com.wakeupneo.security.model.Role;
import com.wakeupneo.security.model.Token;
import com.wakeupneo.security.model.User;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Collection;
import java.util.UUID;

public interface UserService extends UserDetailsService {

    User registerUser(User user) throws EntityNotFoundException, EntityExistsException;

    void sendActivationMail(UUID uuid);

    User getUserById(UUID uuid);

    User getUserByEmail(String email);

    User getUserByUsername(String username);

    User updateProfile(User currentUser, User updateUser);

    void sendPasswordResetMail(String email);

    Token verifyPasswordResetToken(String token);

    User updateForgottenPassword(String token, String password);

    void changeMyPassword(User user, String oldPassword, String newPassword);

    boolean isUserExistWithEmail(String email);

    boolean isUserExistWithUsername(String username);

    boolean verifyAccount(String verificationToken);

    User updateProfileImage(User user, MultipartFile profileImage) throws IOException;

    Collection<Role> getRoles();

    Collection<String> getPermissions();

}
