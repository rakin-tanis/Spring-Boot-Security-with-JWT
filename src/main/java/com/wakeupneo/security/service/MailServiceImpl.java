package com.wakeupneo.security.service;

import com.wakeupneo.security.config.prop.MailProp;
import com.wakeupneo.security.model.Email;
import com.wakeupneo.security.model.User;
import freemarker.template.Configuration;
import freemarker.template.TemplateException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class MailServiceImpl implements MailService {

    private final MailProp mailProp;
    private final JavaMailSender javaMailSender;
    private final Configuration fmConfiguration;

    @Override
    public void sendResetTokenEmail(String token, User user) {
        String contextPath = ServletUriComponentsBuilder.fromCurrentContextPath().toUriString();
        Map<String, Object> model = new HashMap<>();
        model.put("name", Optional.of(user.getName()).orElse(""));
        model.put("surname", Optional.of(user.getUsername()).orElse(""));
        model.put("applicationName", Optional.of(mailProp.getApplicationName()).orElse(""));
        model.put("resetLink", Optional.of(getAbsoluteUrl(VALIDATE_PASSWORD_END_POINT, token)).orElse(""));
        Email email = Email.builder()
                .subject("Reset Password")
                .from(mailProp.getEmail())
                .to(user.getEmail())
                .templateName("reset_token_email.html")
                .model(model)
                .build();
        sendHtmlEmail(email);
    }

    public void sendActivationMail(String token, User user) {
        Map<String, Object> model = new HashMap<>();
        model.put("name", Optional.of(user.getName()).orElse(""));
        model.put("surname", Optional.of(user.getUsername()).orElse(""));
        model.put("applicationName", Optional.of(mailProp.getApplicationName()).orElse(""));
        model.put("link", Optional.of(getAbsoluteUrl(VERIFY_ACCOUNT_END_POINT, token)).orElse(""));
        Email email = Email.builder()
                .subject("Account Activation")
                .from(mailProp.getEmail())
                .to(user.getEmail())
                .templateName("activation_email.html")
                .model(model)
                .build();
        sendHtmlEmail(email);
    }

    private void sendHtmlEmail(Email mail) {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
            helper.setSubject(mail.getSubject());
            helper.setFrom(mail.getFrom());
            helper.setTo(mail.getTo());
            helper.setText(getContentFromTemplate(mail.getTemplateName(), mail.getModel()), true);
//            javaMailSender.send(helper.getMimeMessage());
            // TODO mail sender error is gonna fix.
        } catch (MessagingException | IOException | TemplateException e) {
            e.printStackTrace();
        }
    }

    private String getContentFromTemplate(String templateName, Map<String, Object> model)
            throws IOException, TemplateException {
        return FreeMarkerTemplateUtils.processTemplateIntoString(fmConfiguration.getTemplate(templateName), model);
    }

    private String getAbsoluteUrl(String path, String token) {
        return "";
//        return ServletUriComponentsBuilder.fromCurrentContextPath().path(path).toUriString() + "?token=" + token;
    }
}
