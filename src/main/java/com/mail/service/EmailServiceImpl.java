package com.mail.service;

import com.mail.client.EmailProvider;
import com.mail.data.EmailDTO;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class EmailServiceImpl implements EmailService{

    private final EmailProvider mailgunEmailProvider;
    private final EmailProvider mailJetEmailProvider;

    public EmailServiceImpl(@Qualifier("mailgun") EmailProvider mailgunEmailProvider, @Qualifier("mailjet") EmailProvider mailJetEmailProvider) {
        this.mailgunEmailProvider = mailgunEmailProvider;
        this.mailJetEmailProvider = mailJetEmailProvider;
    }

    @Override
    public ResponseEntity<?> sendEmail(EmailDTO emailDTO) {
        try {
            return isEmailValidObject(emailDTO) ? mailgunEmailProvider.sendEmail(emailDTO) :
                    new ResponseEntity<>("Invalid email object", HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return sendEmailWithBackupProvider(emailDTO);
        }
    }

    private ResponseEntity<?> sendEmailWithBackupProvider(EmailDTO emailDTO) {
        try {
            return mailJetEmailProvider.sendEmail(emailDTO);
        } catch (Exception e) {
            return new ResponseEntity<>("All email providers are down", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private Boolean isEmailValidObject(EmailDTO emailDTO) {
        if (emailDTO.getTo() != null) {
            for (String email : emailDTO.getTo()) {
                if (!isEmailValid(email)) {
                    return false;
                }
            }
        } else {
            return false;
        }

        if (emailDTO.getCc() != null) {
            for (String email : emailDTO.getCc()) {
                if (!isEmailValid(email)) {
                    return false;
                }
            }
        }

        if (emailDTO.getBcc() != null) {
            for (String email : emailDTO.getBcc()) {
                if (!isEmailValid(email)) {
                    return false;
                }
            }
        }

        return emailDTO.getSubject() != null &&
                !emailDTO.getSubject().isEmpty();
    }

    private Boolean isEmailValid(String email) {
        String regex = "^(.+)@(.+)$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }
}
