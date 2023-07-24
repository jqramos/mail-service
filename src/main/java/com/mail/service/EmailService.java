package com.mail.service;

import com.mail.data.EmailDTO;
import org.springframework.http.ResponseEntity;

public interface EmailService {

    ResponseEntity<?> sendEmail(EmailDTO emailDTO);
}
