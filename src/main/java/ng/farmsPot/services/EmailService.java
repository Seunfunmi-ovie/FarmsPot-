package ng.farmsPot.services;

import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${mail.from-email:oluwaseunpu@gmail.com}")
    private String fromEmail;

    @Value("${mail.from-name:FarmsPot}")
    private String fromName;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    private void sendEmail(String email, String subject, String html) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper =
                    new MimeMessageHelper(message, StandardCharsets.UTF_8.name());

            helper.setFrom(fromEmail, fromName);
            helper.setTo(email);
            helper.setSubject(subject);
            helper.setText(html, true);

            mailSender.send(message);
        } catch (Exception exception) {
            throw new RuntimeException("Error Sending Mail", exception);
        }
    }

    public void registrationConfirmationEmail(String email, String farmersName) {
        String subject = "FarmsPot Registration Confirmation Message";
        String html = "<h3>Welcome to farmsPot-Farmers Loan-Cooperative</h3><p>Can't wait to serve you better </p>" + farmersName;
        sendEmail(email, subject, html);
    }

    public void loanDueWarningEmail(String email, String farmersName) {
        String subject = "FarmPot Loan Warning Message";
        String html = "<h3>Dear Esteemed " + farmersName + "This is to inform you that your Loan has expired today, Kindly repay your loan to avoid default loan repayment</h3>";
        sendEmail(email, subject, html);
    }

    public void loanApplicationReceivedEmail(String email, String farmersName, double requestedAmount) {
        String subject = "FarmsPot Loan Application Received";
        String html = "<h3>Dear " + farmersName + ", your loan application for " + requestedAmount + " has been received and is pending review.</h3>";
        sendEmail(email, subject, html);
    }

    public void loanDisbursedEmail(String email, String farmersName, double requestedAmount) {
        String subject = "FarmsPot Loan Approved and Disbursed";
        String html = "<h3>Dear " + farmersName + ", your loan of " + requestedAmount + " has been approved and disbursed to your account.</h3>";
        sendEmail(email, subject, html);
    }
}