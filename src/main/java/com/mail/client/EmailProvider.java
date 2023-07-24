package com.mail.client;

import com.mail.data.EmailDTO;
import org.springframework.http.ResponseEntity;

public interface EmailProvider {

        ResponseEntity<?> sendEmail(EmailDTO emailDTO);
}
