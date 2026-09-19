package ng.farmsPot.services;


import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

import lombok.AllArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.mail.javamail.MimeMessageHelper;


@Service
@AllArgsConstructor
public class EmailService {

    private  final JavaMailSender mailSender;



    public void registrationConfirmationEmail(String email, String farmersName)  {

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper mailMessage =  new MimeMessageHelper(message, "utf-8");
            mailMessage.setTo(email);
            mailMessage.setSubject("FarmsPot Registration Confirmation Message");
            mailMessage.setText("<h3>Welcome to farmsPot-Farmers Loan-Cooperative</h3><p>Can't wait to serve you better </p>" + farmersName, true);

            mailSender.send(mailMessage.getMimeMessage());

        }catch(MessagingException exception){
            throw new RuntimeException("Error Sending Mail");
        }

    }

}
