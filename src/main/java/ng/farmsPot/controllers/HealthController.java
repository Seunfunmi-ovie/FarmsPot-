package ng.farmsPot.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.Map;

@RestController
public class HealthController {

    // Responds at both root and /health endpoints instantly without touching the DB,
    // allowing Render, pingers, and the frontend to pre-warm the backend container.
    @GetMapping({"/", "/health", "/api/health", "/ping"})
    public Map<String, Object> health() {
        return Map.of(
                "status", "ok",
                "service", "farmsPot-backend",
                "timestamp", Instant.now().toString()
        );
    }
}

