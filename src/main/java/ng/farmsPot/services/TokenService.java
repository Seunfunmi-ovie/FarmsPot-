package ng.farmsPot.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;
import java.util.Optional;

/**
 * Issues and checks signed login tokens (HMAC-SHA256) without any extra library.
 * A token is "payload.signature" where the payload holds who it belongs to and an expiry time.
 * Farmer tokens carry the farmer id; buyer tokens carry "B" plus the buyer id, so one kind
 * can never be used where the other is expected.
 * The role is NOT stored in the token; it is read from the database on every protected call,
 * so suspending or demoting someone takes effect immediately.
 */
@Service
public class TokenService {

    private static final Logger logger = LoggerFactory.getLogger(TokenService.class);

    private final byte[] secret;
    private final long lifetimeSeconds;

    public TokenService(@Value("${app.auth.secret:}") String configuredSecret,
                        @Value("${app.auth.token-hours:12}") long tokenHours) {
        if (configuredSecret == null || configuredSecret.length() < 32) {
            logger.warn("AUTH_SECRET is missing or shorter than 32 characters. Using a random secret for this run, "
                    + "so everyone is signed out whenever the app restarts. Set AUTH_SECRET in production.");
            byte[] random = new byte[48];
            new SecureRandom().nextBytes(random);
            this.secret = random;
        } else {
            this.secret = configuredSecret.getBytes(StandardCharsets.UTF_8);
        }
        this.lifetimeSeconds = tokenHours * 3600;
    }

    public String createToken(int farmerId) {
        return build(String.valueOf(farmerId));
    }

    public String createBuyerToken(int buyerId) {
        return build("B" + buyerId);
    }

    /** Returns the farmer id if the token is a genuine, unexpired farmer token, otherwise empty. */
    public Optional<Integer> verify(String token) {
        Optional<String> subject = subject(token);
        if (subject.isEmpty() || subject.get().startsWith("B")) {
            return Optional.empty();
        }
        try {
            return Optional.of(Integer.parseInt(subject.get()));
        } catch (NumberFormatException exception) {
            return Optional.empty();
        }
    }

    /** Returns the buyer id if the token is a genuine, unexpired buyer token, otherwise empty. */
    public Optional<Integer> verifyBuyer(String token) {
        Optional<String> subject = subject(token);
        if (subject.isEmpty() || !subject.get().startsWith("B")) {
            return Optional.empty();
        }
        try {
            return Optional.of(Integer.parseInt(subject.get().substring(1)));
        } catch (NumberFormatException exception) {
            return Optional.empty();
        }
    }

    private String build(String subject) {
        long expiry = Instant.now().getEpochSecond() + lifetimeSeconds;
        String payload = subject + ":" + expiry;
        String encodedPayload = Base64.getUrlEncoder().withoutPadding()
                .encodeToString(payload.getBytes(StandardCharsets.UTF_8));
        return encodedPayload + "." + sign(encodedPayload);
    }

    /** The verified, unexpired subject of a token, or empty. */
    private Optional<String> subject(String token) {
        if (token == null) {
            return Optional.empty();
        }
        int dot = token.indexOf('.');
        if (dot <= 0 || dot == token.length() - 1) {
            return Optional.empty();
        }
        String encodedPayload = token.substring(0, dot);
        String signature = token.substring(dot + 1);

        byte[] expected = sign(encodedPayload).getBytes(StandardCharsets.UTF_8);
        if (!MessageDigest.isEqual(expected, signature.getBytes(StandardCharsets.UTF_8))) {
            return Optional.empty();
        }

        try {
            String payload = new String(Base64.getUrlDecoder().decode(encodedPayload), StandardCharsets.UTF_8);
            String[] parts = payload.split(":");
            if (parts.length != 2) {
                return Optional.empty();
            }
            long expiry = Long.parseLong(parts[1]);
            if (Instant.now().getEpochSecond() > expiry) {
                return Optional.empty();
            }
            return Optional.of(parts[0]);
        } catch (IllegalArgumentException exception) {
            return Optional.empty();
        }
    }

    private String sign(String data) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secret, "HmacSHA256"));
            return Base64.getUrlEncoder().withoutPadding()
                    .encodeToString(mac.doFinal(data.getBytes(StandardCharsets.UTF_8)));
        } catch (GeneralSecurityException exception) {
            throw new IllegalStateException("Could not sign the token", exception);
        }
    }
}