package com.mail.data;
import lombok.Data;
import java.io.Serializable;
import java.util.List;

@Data
public class EmailDTO implements Serializable {

        private List<String> to;
        private List<String> cc;
        private List<String> bcc;

        private String subject;
        private String text;

}
