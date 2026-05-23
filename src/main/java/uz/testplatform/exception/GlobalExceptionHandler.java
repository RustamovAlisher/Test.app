package uz.testplatform.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Global Exception Handler.
 *
 * Barcha xatolarni bir joyda boshqaradi.
 * Har xato uchun mos HTTP status code qaytaradi.
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {


    // 404 Not Found - resurs topilmadi
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNotFound(NotFoundException e) {
        log.warn("Resurs topilmadi: {}", e.getMessage());
        return buildResponse(HttpStatus.NOT_FOUND, e.getMessage());
    }


    // 400 Bad Request - noto'g'ri so'rov
    @ExceptionHandler(RequestException.class)
    public ResponseEntity<Map<String, Object>> handleRequest(RequestException e) {
        log.warn("Noto'g'ri so'rov: {}", e.getMessage());
        return buildResponse(HttpStatus.BAD_REQUEST, e.getMessage());
    }


    // 409 Conflict - konflikt (email band, va h.k.)
    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<Map<String, Object>> handleConflict(ConflictException e) {
        log.warn("Konflikt: {}", e.getMessage());
        return buildResponse(HttpStatus.CONFLICT, e.getMessage());
    }


    // 401 Unauthorized - avtorizatsiya muammosi
    @ExceptionHandler(AuthorizationException.class)
    public ResponseEntity<Map<String, Object>> handleAuthorization(AuthorizationException e) {
        log.warn("Avtorizatsiya muammosi: {}", e.getMessage());
        return buildResponse(HttpStatus.UNAUTHORIZED, e.getMessage());
    }


    // 400 Bad Request - @Valid validation xatosi
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException e) {
        String message = e.getBindingResult()
                .getFieldErrors()
                .get(0)
                .getDefaultMessage();

        log.warn("Validation xatosi: {}", message);
        return buildResponse(HttpStatus.BAD_REQUEST, message);
    }


    // 500 Internal Server Error - boshqa barcha kutilmagan xatolar
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneral(Exception e) {
        log.error("Kutilmagan xatolik: {}", e.getMessage(), e);
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Serverda xatolik yuz berdi");
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNoResource(NoResourceFoundException e) {
        return buildResponse(HttpStatus.NOT_FOUND, "Resurs topilmadi");
    }

    private ResponseEntity<Map<String, Object>> buildResponse(HttpStatus status, String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("status", status.value());
        response.put("error", status.getReasonPhrase());
        response.put("message", message);
        return ResponseEntity.status(status).body(response);
    }
}