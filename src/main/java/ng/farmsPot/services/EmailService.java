package ng.farmsPot.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Service
public class EmailService {

    @Value("${resend.api-key:re_placeholder_key}")
    private String apiKey;

    @Value("${resend.from:FarmsPot <onboarding@resend.dev>}")
    private String fromEmail;

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();

    private void sendEmail(String email, String subject, String html) {
        try {
            Map<String, Object> body = Map.of(
                    "from", fromEmail != null ? fromEmail : "onboarding@resend.dev",
                    "to", new String[]{email},
                    "subject", subject,
                    "html", html
            );

            String json = objectMapper.writeValueAsString(body);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.resend.com/emails"))
                    .header("Authorization", "Bearer " + apiKey)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json, StandardCharsets.UTF_8))
                    .build();

            HttpResponse<String> response = httpClient.send(
                    request,
                    HttpResponse.BodyHandlers.ofString()
            );

            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                throw new RuntimeException("Error Sending Mail. Response: " + response.body());
            }

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