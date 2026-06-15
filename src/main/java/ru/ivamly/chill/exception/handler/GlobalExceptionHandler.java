package ru.ivamly.chill.exception.handler;

import java.time.Instant;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import jakarta.persistence.EntityNotFoundException;
import ru.ivamly.chill.exception.ChillModificationNotAllowedException;
import ru.ivamly.chill.exception.OverlappingChillException;
import ru.ivamly.chill.exception.UnauthorizedException;
import ru.ivamly.chill.exception.UserExistException;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(OverlappingChillException.class)
    public ResponseEntity<ProblemDetail> handle(OverlappingChillException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
        problem.setTitle("Существует chill в выбранные даты");
        problem.setProperty("timestamp", Instant.now());
        problem.setProperty("userId", ex.getUserId());
        problem.setProperty("startDate", ex.getStartDate());
        problem.setProperty("endDate", ex.getEndDate());
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(problem);
    }

    @ExceptionHandler(ChillModificationNotAllowedException.class)
    public ResponseEntity<ProblemDetail> handle(ChillModificationNotAllowedException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
        problem.setTitle("Нельзя модифицировать chill после его начала");
        problem.setProperty("timestamp", Instant.now());
        problem.setProperty("userId", ex.getUserId());
        problem.setProperty("chillStartDate", ex.getChillStartDate());
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(problem);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ProblemDetail> handle(EntityNotFoundException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        problem.setTitle("Not Found");
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(problem);
    }

    @ExceptionHandler(UserExistException.class)
    public ResponseEntity<ProblemDetail> handle(UserExistException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
        problem.setTitle("User с таким именем уже существует");
        problem.setProperty("timestamp", Instant.now());
        problem.setProperty("name", ex.getName());
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(problem);
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ProblemDetail> handle(UnauthorizedException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.UNAUTHORIZED, ex.getMessage());
        problem.setTitle("Ошибка аутентификации");
        problem.setProperty("timestamp", Instant.now());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(problem);
    }
}
