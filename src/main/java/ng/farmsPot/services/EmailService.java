package ng.farmsPot.services;

import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.text.NumberFormat;
import java.time.Year;
import java.util.Locale;

@Service
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    private final JavaMailSender mailSender;

    @Value("${mail.from-email:oluwaseunpu@gmail.com}")
    private String fromEmail;

    @Value("${mail.from-name:FarmsPot}")
    private String fromName;

    @Value("${app.frontend.url:https://farmspot-frontend.onrender.com}")
    private String frontendUrl;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    private void sendEmail(String email, String subject, String html) {
        if (email == null || email.isBlank()) {
            return;
        }
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper =
                    new MimeMessageHelper(message, StandardCharsets.UTF_8.name());

            helper.setFrom(fromEmail, fromName);
            helper.setTo(email.trim());
            helper.setSubject(subject);
            helper.setText(html, true);

            mailSender.send(message);
            logger.info("Sent email '{}' to {}", subject, email);
        } catch (Exception exception) {
            logger.error("Error sending email to {}", email, exception);
            throw new RuntimeException("Error Sending Mail", exception);
        }
    }

    public void registrationConfirmationEmail(String email, String farmersName) {
        String subject = "🌾 Welcome to FarmsPot - Registration Confirmed";
        String html = buildRegistrationHtml(farmersName, email);
        sendEmail(email, subject, html);
    }

    public void loanApplicationReceivedEmail(String email, String farmersName, double requestedAmount) {
        String subject = "📋 FarmsPot - Loan Application Received";
        String html = buildLoanApplicationReceivedHtml(farmersName, requestedAmount);
        sendEmail(email, subject, html);
    }

    public void loanDisbursedEmail(String email, String farmersName, double requestedAmount) {
        String subject = "🎉 FarmsPot - Loan Approved & Disbursed";
        String html = buildLoanDisbursedHtml(farmersName, requestedAmount);
        sendEmail(email, subject, html);
    }

    public void loanDueWarningEmail(String email, String farmersName) {
        String subject = "⚠️ Urgent: FarmsPot Loan Repayment Due Reminder";
        String html = buildLoanDueWarningHtml(farmersName);
        sendEmail(email, subject, html);
    }

    public void loanPaymentReceivedEmail(String email, String farmersName, double amountPaid, double remainingBalance) {
        String subject = "✅ FarmsPot - Loan Repayment Recorded";
        String html = buildLoanPaymentReceivedHtml(farmersName, amountPaid, remainingBalance);
        sendEmail(email, subject, html);
    }

    // =========================================================================
    // HTML Email Template Builders (Frontend Card UI)
    // =========================================================================

    public String buildRegistrationHtml(String farmersName, String email) {
        String name = sanitize(farmersName, "Farmer");
        String badge = "<span style=\"background-color:#e8f5e9;color:#2e7d32;font-size:12px;font-weight:700;padding:6px 14px;border-radius:20px;text-transform:uppercase;letter-spacing:0.5px;\">Registration Confirmed</span>";
        String headline = "Welcome to the FarmsPot Cooperative!";
        String greeting = "Hello, <strong>" + name + "</strong> 👋";
        String message = "Your farmer cooperative account has been created successfully. You now have direct access to seasonal farm credit financing, harvest export listings, and escrow-secured B2B commodity trading.";

        String detailsBox = """
            <div style="background-color:#f9fafb;border-left:4px solid #2e7d32;padding:18px 20px;border-radius:8px;margin:24px 0;">
                <table style="width:100%;border-collapse:collapse;font-size:14px;">
                    <tr>
                        <td style="color:#6b7280;padding:5px 0;">Account Name:</td>
                        <td style="color:#111827;font-weight:600;text-align:right;">%s</td>
                    </tr>
                    <tr>
                        <td style="color:#6b7280;padding:5px 0;">Registered Contact:</td>
                        <td style="color:#111827;font-weight:600;text-align:right;">%s</td>
                    </tr>
                    <tr>
                        <td style="color:#6b7280;padding:5px 0;">Membership Status:</td>
                        <td style="color:#2e7d32;font-weight:700;text-align:right;">Active Member</td>
                    </tr>
                </table>
            </div>
            """.formatted(name, email != null ? email : "Verified Phone");

        return renderShell("Welcome to FarmsPot", badge, headline, greeting, message, detailsBox, frontendUrl, "Access Your Dashboard");
    }

    public String buildLoanApplicationReceivedHtml(String farmersName, double requestedAmount) {
        String name = sanitize(farmersName, "Esteemed Member");
        String formattedAmount = formatCurrency(requestedAmount);

        String badge = "<span style=\"background-color:#eff6ff;color:#1d4ed8;font-size:12px;font-weight:700;padding:6px 14px;border-radius:20px;text-transform:uppercase;letter-spacing:0.5px;\">Application Received</span>";
        String headline = "Loan Application Under Review";
        String greeting = "Hello, <strong>" + name + "</strong>,";
        String message = "We have received your seasonal farm credit application. Our automated underwriting system and cooperative team are currently evaluating your farm parameters and historical crop yield metrics.";

        String detailsBox = """
            <div style="background-color:#f9fafb;border-left:4px solid #1d4ed8;padding:18px 20px;border-radius:8px;margin:24px 0;">
                <table style="width:100%;border-collapse:collapse;font-size:14px;">
                    <tr>
                        <td style="color:#6b7280;padding:5px 0;">Requested Amount:</td>
                        <td style="color:#111827;font-weight:700;font-size:16px;text-align:right;">%s</td>
                    </tr>
                    <tr>
                        <td style="color:#6b7280;padding:5px 0;">Application Status:</td>
                        <td style="color:#1d4ed8;font-weight:600;text-align:right;">Pending Review</td>
                    </tr>
                    <tr>
                        <td style="color:#6b7280;padding:5px 0;">Review Period:</td>
                        <td style="color:#111827;font-weight:600;text-align:right;">Within 24–48 Hours</td>
                    </tr>
                </table>
            </div>
            """.formatted(formattedAmount);

        return renderShell("Loan Application Received", badge, headline, greeting, message, detailsBox, frontendUrl, "Track Application Status");
    }

    public String buildLoanDisbursedHtml(String farmersName, double requestedAmount) {
        String name = sanitize(farmersName, "Esteemed Member");
        String formattedAmount = formatCurrency(requestedAmount);

        String badge = "<span style=\"background-color:#ecfdf5;color:#047857;font-size:12px;font-weight:700;padding:6px 14px;border-radius:20px;text-transform:uppercase;letter-spacing:0.5px;\">Funds Disbursed 🎉</span>";
        String headline = "Loan Approved & Disbursed!";
        String greeting = "Congratulations, <strong>" + name + "</strong>!";
        String message = "Your seasonal loan application has been approved and funds have been deposited directly into your FarmsPot cooperative account balance. You may now deploy these funds for seasonal inputs, harvest logistics, and equipment.";

        String detailsBox = """
            <div style="background-color:#ecfdf5;border:1px solid #a7f3d0;padding:18px 20px;border-radius:8px;margin:24px 0;">
                <table style="width:100%;border-collapse:collapse;font-size:14px;">
                    <tr>
                        <td style="color:#065f46;padding:5px 0;">Disbursed Amount:</td>
                        <td style="color:#065f46;font-weight:700;font-size:18px;text-align:right;">%s</td>
                    </tr>
                    <tr>
                        <td style="color:#065f46;padding:5px 0;">Account Credited:</td>
                        <td style="color:#065f46;font-weight:600;text-align:right;">FarmsPot Farmers Balance</td>
                    </tr>
                    <tr>
                        <td style="color:#065f46;padding:5px 0;">Repayment Schedule:</td>
                        <td style="color:#065f46;font-weight:600;text-align:right;">Harvest Escrow / Direct Pay</td>
                    </tr>
                </table>
            </div>
            """.formatted(formattedAmount);

        return renderShell("Loan Disbursed", badge, headline, greeting, message, detailsBox, frontendUrl, "View Account Balance");
    }

    public String buildLoanDueWarningHtml(String farmersName) {
        String name = sanitize(farmersName, "Esteemed Member");

        String badge = "<span style=\"background-color:#fef2f2;color:#b91c1c;font-size:12px;font-weight:700;padding:6px 14px;border-radius:20px;text-transform:uppercase;letter-spacing:0.5px;\">Repayment Due Reminder ⚠️</span>";
        String headline = "Action Required: Loan Repayment Due";
        String greeting = "Dear Esteemed Member, <strong>" + name + "</strong>,";
        String message = "This is a courtesy reminder that your loan repayment period has matured. Settle your outstanding balance promptly to maintain a high cooperative credit score and stay eligible for higher credit tiers and export listings.";

        String detailsBox = """
            <div style="background-color:#fff1f2;border-left:4px solid #e11d48;padding:18px 20px;border-radius:8px;margin:24px 0;">
                <table style="width:100%;border-collapse:collapse;font-size:14px;">
                    <tr>
                        <td style="color:#9f1239;padding:5px 0;">Notice Type:</td>
                        <td style="color:#9f1239;font-weight:700;text-align:right;">Loan Due Notification</td>
                    </tr>
                    <tr>
                        <td style="color:#9f1239;padding:5px 0;">Credit Score Benefit:</td>
                        <td style="color:#9f1239;font-weight:600;text-align:right;">+10 to +40 Points for Full Repayment</td>
                    </tr>
                </table>
            </div>
            """;

        return renderShell("Loan Due Reminder", badge, headline, greeting, message, detailsBox, frontendUrl, "Make Repayment");
    }

    public String buildLoanPaymentReceivedHtml(String farmersName, double amountPaid, double remainingBalance) {
        String name = sanitize(farmersName, "Esteemed Member");
        String formattedPayment = formatCurrency(amountPaid);
        String formattedRemaining = formatCurrency(remainingBalance);

        String badge = "<span style=\"background-color:#ecfdf5;color:#047857;font-size:12px;font-weight:700;padding:6px 14px;border-radius:20px;text-transform:uppercase;letter-spacing:0.5px;\">Payment Recorded</span>";
        String headline = "Loan Repayment Received";
        String greeting = "Hello, <strong>" + name + "</strong>,";
        String message = "We have received and credited your escrow loan repayment. Thank you for maintaining timely repayments with FarmsPot.";

        String detailsBox = """
            <div style="background-color:#f9fafb;border-left:4px solid #2e7d32;padding:18px 20px;border-radius:8px;margin:24px 0;">
                <table style="width:100%;border-collapse:collapse;font-size:14px;">
                    <tr>
                        <td style="color:#6b7280;padding:5px 0;">Amount Paid:</td>
                        <td style="color:#2e7d32;font-weight:700;font-size:16px;text-align:right;">%s</td>
                    </tr>
                    <tr>
                        <td style="color:#6b7280;padding:5px 0;">Remaining Balance:</td>
                        <td style="color:#111827;font-weight:600;text-align:right;">%s</td>
                    </tr>
                </table>
            </div>
            """.formatted(formattedPayment, formattedRemaining);

        return renderShell("Repayment Recorded", badge, headline, greeting, message, detailsBox, frontendUrl, "View Loan Details");
    }

    private String renderShell(String title, String badge, String headline, String greeting,
                               String message, String detailsBox, String ctaUrl, String ctaText) {
        int year = Year.now().getValue();
        return """
            <!DOCTYPE html>
            <html lang="en">
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>%s</title>
            </head>
            <body style="margin:0;padding:0;background-color:#f4f7f4;font-family:-apple-system,BlinkMacSystemFont,'Segoe UI',Roboto,Helvetica,Arial,sans-serif;color:#1f2937;">
                <table role="presentation" width="100%%" cellspacing="0" cellpadding="0" style="background-color:#f4f7f4;padding:32px 12px;">
                    <tr>
                        <td align="center">
                            <table role="presentation" width="100%%" style="max-width:580px;background:#ffffff;border-radius:16px;overflow:hidden;box-shadow:0 4px 16px rgba(0,0,0,0.06);border:1px solid #e5e7eb;">
                                <!-- Brand Header -->
                                <tr>
                                    <td style="background:linear-gradient(135deg,#1b5e20 0%%,#2e7d32 100%%);padding:36px 32px;text-align:center;">
                                        <div style="font-size:32px;line-height:1;margin-bottom:8px;">🌾</div>
                                        <h1 style="margin:0;color:#ffffff;font-size:24px;font-weight:700;letter-spacing:-0.5px;">FarmsPot</h1>
                                        <p style="margin:6px 0 0;color:rgba(255,255,255,0.85);font-size:12px;letter-spacing:1px;text-transform:uppercase;font-weight:500;">Farm Credit &amp; Export Marketplace</p>
                                    </td>
                                </tr>
                                <!-- Main Body Card -->
                                <tr>
                                    <td style="padding:36px 32px;">
                                        <div style="margin-bottom:16px;">%s</div>
                                        <h2 style="margin:0 0 12px;font-size:20px;font-weight:700;color:#111827;line-height:1.3;">%s</h2>
                                        <p style="margin:0 0 14px;font-size:15px;line-height:1.6;color:#4b5563;">%s</p>
                                        <p style="margin:0 0 20px;font-size:15px;line-height:1.6;color:#374151;">%s</p>

                                        %s

                                        <div style="text-align:center;margin:32px 0 12px;">
                                            <a href="%s" style="display:inline-block;background-color:#2e7d32;color:#ffffff;font-size:15px;font-weight:600;text-decoration:none;padding:14px 32px;border-radius:8px;box-shadow:0 2px 6px rgba(46,125,50,0.3);">
                                                %s &rarr;
                                            </a>
                                        </div>
                                    </td>
                                </tr>
                                <!-- Footer -->
                                <tr>
                                    <td style="background-color:#f9fafb;padding:24px 32px;border-top:1px solid #e5e7eb;text-align:center;">
                                        <p style="margin:0 0 6px;font-size:12px;color:#6b7280;">
                                            Questions? Contact our team at <a href="mailto:support@farmspot.ng" style="color:#2e7d32;text-decoration:none;font-weight:600;">support@farmspot.ng</a>
                                        </p>
                                        <p style="margin:0;font-size:11px;color:#9ca3af;">
                                            &copy; %d FarmsPot Cooperative Platform. All rights reserved.
                                        </p>
                                    </td>
                                </tr>
                            </table>
                        </td>
                    </tr>
                </table>
            </body>
            </html>
            """.formatted(title, badge, headline, greeting, message, detailsBox, ctaUrl, ctaText, year);
    }

    private String formatCurrency(double amount) {
        NumberFormat formatter = NumberFormat.getNumberInstance(Locale.US);
        formatter.setMinimumFractionDigits(2);
        formatter.setMaximumFractionDigits(2);
        return "₦" + formatter.format(amount);
    }

    private String sanitize(String value, String fallback) {
        if (value == null || value.isBlank()) {
            return fallback;
        }
        return value.trim();
    }
}
