package com.mail.client;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mail.data.EmailDTO;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Base64;

@Service
@Qualifier("mailjet")
public class MailJetEmailProvider implements EmailProvider{

    @Value("${mail.provider.mailjet.api-key}")
    private String apiKey;

    @Value("${mail.provider.mailjet.secret-key}")
    private String secretKey;

    @Value("${mail.provider.mailjet.domain}")
    private String domain;

    @Value("${mail.provider.mailjet.sender-email}")
    private String senderEmail;

    private final static String EMAIL = "Email";
    @Override
    public ResponseEntity<?> sendEmail(EmailDTO emailDTO) {
        String authHeader = "Basic " + Base64.getEncoder().encodeToString((apiKey + ":" + secretKey).getBytes());

        WebClient webClient = WebClient.builder()
                .baseUrl(domain)
                .build();
        try {
            Mono<String> result = webClient.post()
                    .uri("/send")
                    .header("Authorization", authHeader)
                    .body(BodyInserters.fromValue(createPayload(emailDTO)))
                    .retrieve()
                    .bodyToMono(String.class);

            return  result.map(response -> {
                return response.contains("success") ? ResponseEntity.ok().body("Email sent successfully") :
                        ResponseEntity.internalServerError().body("Error sending email");
            }).block();
        } catch (Exception e) {
            throw new RuntimeException("Error sending email");
        }
    }

    private String createPayload(EmailDTO emailDTO) {

        JsonObject data = new JsonObject();

        if (emailDTO.getCc() != null && !emailDTO.getCc().isEmpty()) {
            JsonArray cc = new JsonArray();
            emailDTO.getCc().forEach(ccEmail -> {
                JsonObject ccEmailJson = new JsonObject();
                ccEmailJson.addProperty(EMAIL, ccEmail);
                ccEmailJson.addProperty("Name", "");
                cc.add(ccEmailJson);
            });
            data.add("Cc", cc);
        }

        if (emailDTO.getBcc() != null && !emailDTO.getBcc().isEmpty()) {
            JsonArray bcc = new JsonArray();
            emailDTO.getBcc().forEach(bccEmail -> {
                JsonObject bccEmailJson = new JsonObject();
                bccEmailJson.addProperty(EMAIL, bccEmail);
                bccEmailJson.addProperty("Name", "");
                bcc.add(bccEmailJson);
            });
            data.add("Bcc", bcc);
        }

        if(emailDTO.getTo() != null && !emailDTO.getTo().isEmpty()) {
            JsonArray to = new JsonArray();
            emailDTO.getTo().forEach(toEmail -> {
                JsonObject toEmailJson = new JsonObject();
                toEmailJson.addProperty(EMAIL, toEmail);
                toEmailJson.addProperty("Name", "");
                to.add(toEmailJson);
            });
            data.add("To", to);
        }

        JsonObject from = new JsonObject();
        from.addProperty(EMAIL,  senderEmail);
        from.addProperty("Name", "MailJet test");
        data.add("From", from);

        data.addProperty("Subject", emailDTO.getSubject());
        data.addProperty("TextPart", emailDTO.getText());

        JsonArray jsonArray = new JsonArray();
        jsonArray.add(data);

        JsonObject payload = new JsonObject();
        payload.add("Messages", jsonArray);
        return payload.toString();
    }

}
