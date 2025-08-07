package com.portfoliomanager.team18.exception.handler;

import com.portfoliomanager.team18.exception.InsufficientCashException;
import com.portfoliomanager.team18.exception.PortfolioIllegalArgumentException;
import com.portfoliomanager.team18.exception.StockIllegalArgumentException;
import jdk.jfr.Experimental;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Map;

@ControllerAdvice
public class PortfolioExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler({InsufficientCashException.class})
    protected ResponseEntity<Map<String, Object>> handleInsufficientCashException(InsufficientCashException ex) {
        Map<String, Object> response = Map.of(
                "errorMessage", ex.getMessage()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).contentType(MediaType.APPLICATION_JSON).body(response);
    }

    @ExceptionHandler({PortfolioIllegalArgumentException.class})
    protected ResponseEntity<Map<String, Object>> handlePortfolioIllegalArgumentException(IllegalArgumentException ex) {
        Map<String, Object> response = Map.of(
                "errorMessage", ex.getMessage()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).contentType(MediaType.APPLICATION_JSON).body(response);
    }

    @ExceptionHandler({StockIllegalArgumentException.class})
    protected ResponseEntity<Map<String, Object>> handleStockIllegalArgumentException(IllegalArgumentException ex) {
        Map<String, Object> response = Map.of(
                "errorMessage", ex.getMessage()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).contentType(MediaType.APPLICATION_JSON).body(response);
    }
}

