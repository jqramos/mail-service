package com.mail.client;


import com.mail.data.EmailDTO;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Base64;

@Service
@Qualifier("mailgun")
public class MailgunEmailProvider implements EmailProvider{


    @Value("${mail.provider.mailgun.api-key}")
    private String apiKey;

    @Value("${mail.provider.mailgun.domain}")
    private String domain;

    @Value("${mail.provider.mailgun.sender-email}")
    private String senderEmail;


    @Override
    public ResponseEntity<?> sendEmail(EmailDTO emailDTO) {
        String authHeader = "Basic " + Base64.getEncoder().encodeToString(("api:" + apiKey).getBytes());
        WebClient webClient = WebClient.builder()
                .baseUrl(domain)
                .build();
        try {
            Mono<String> result = webClient.post()
                    .uri("/messages")
                    .header("Authorization", authHeader)
                    .body(BodyInserters.fromFormData(createPayload(emailDTO)))
                    .retrieve()
                    .bodyToMono(String.class);

            return result.map(response -> {
                return response.contains("Queued. Thank you.") ? ResponseEntity.ok().body("Email sent successfully") :
                        ResponseEntity.internalServerError().body("Error sending email");
            }).block();
        } catch (Exception e) {
            throw new RuntimeException("Error sending email");
        }
    }

    private MultiValueMap<String, String> createPayload(EmailDTO emailDTO) {
        MultiValueMap<String, String> payload = new LinkedMultiValueMap<>();
        payload.add("from", "<" + senderEmail + ">");

        if (emailDTO.getCc() != null && !emailDTO.getCc().isEmpty()) {
            StringBuilder cc = new StringBuilder();
            for(String ccEmail : emailDTO.getCc()) {
                   cc.append("<").append(ccEmail).append(">,");
            }
            payload.add("cc", cc.toString());
        }

        if (emailDTO.getBcc() != null && !emailDTO.getBcc().isEmpty()) {
            StringBuilder bcc = new StringBuilder();
            for(String bccEmail : emailDTO.getBcc()) {
                bcc.append("<").append(bccEmail).append(">,");
            }
            payload.add("bcc", bcc.toString());
        }

        if (emailDTO.getTo() != null && !emailDTO.getTo().isEmpty()) {
            StringBuilder to = new StringBuilder();
            for(String toEmail : emailDTO.getTo()) {
                to.append("<").append(toEmail).append(">,");
            }
            payload.add("to", to.toString());
        }

        payload.add("subject", emailDTO.getSubject());
        payload.add("text", emailDTO.getText());
        return payload;
    }
}
