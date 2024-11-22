package com.grolabs.caselist.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.NoSuchElementException;

@ControllerAdvice
public class GlobalExcetionHandler {

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<String> handleNoSuchElementException(NoSuchElementException ex) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND) // 404 상태 코드
                .body(ex.getMessage());       // 예외 메시지 반환
    }

    @ExceptionHandler(CloneNotSupportedException.class)
    public ResponseEntity<String> handleCloneNotSupportedException(CloneNotSupportedException ex) {
        return ResponseEntity
                .status(463) // 463 상태 코드
                .body(ex.getMessage());       // 예외 메시지 반환
    }


}
