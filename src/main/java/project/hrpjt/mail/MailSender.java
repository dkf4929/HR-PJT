package project.hrpjt.mail;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Properties;

public class MailSender {

    private static final String SUBJECT = "연차촉진 안내";

    public static void sendMail(String subject, List<String> content, List<String> toMailList) {
        String host = "smtp.naver.com";
        String user = "dkf4928@naver.com";
        String password = "password";

        // SMTP 서버 정보를 설정한다.
        Properties props = new Properties();
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", 587);
        props.put("mail.smtp.auth", "true");

        Session session = Session.getDefaultInstance(props, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(user, password);
            }
        });

        try {
            for (int i = 0; i < toMailList.size(); i++) {
                MimeMessage message = new MimeMessage(session);
                message.setFrom(new InternetAddress(user));
                message.addRecipient(Message.RecipientType.TO, new InternetAddress(toMailList.get(i)));

                message.setSubject(subject);  // 제목
                message.setText(content.get(i));  // 메일 내용

                Transport.send(message);
            }
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }
}
