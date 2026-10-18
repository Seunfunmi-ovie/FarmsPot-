package ng.farmsPot.config;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleBadRequest(IllegalArgumentException ex) {
        return build(HttpStatus.BAD_REQUEST, ex);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Map<String, String>> handleConflict(IllegalStateException ex) {
        return build(HttpStatus.CONFLICT, ex);
    }

    private ResponseEntity<Map<String, String>> build(HttpStatus status, RuntimeException ex) {
        String message = ex.getMessage() != null ? ex.getMessage() : "The request could not be completed";
        return ResponseEntity.status(status).body(Map.of("message", message));
    }
}