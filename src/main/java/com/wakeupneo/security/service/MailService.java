package com.wakeupneo.security.service;

import com.wakeupneo.security.model.User;

public interface MailService {

    String VALIDATE_PASSWORD_END_POINT = "/api/users/validatePasswordResetToken";
    String VERIFY_ACCOUNT_END_POINT = "/api/users/verifyAccount";

    void sendResetTokenEmail(String token, User user);

    void sendActivationMail(String token, User user);

}
