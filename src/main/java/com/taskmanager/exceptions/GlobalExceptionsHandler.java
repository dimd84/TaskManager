package com.taskmanager.exceptions;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestControllerAdvice
@Log4j2
public class GlobalExceptionsHandler {

    @ExceptionHandler(value = {RuntimeException.class})
    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    String getResponseRuntime(Exception e) {
        return exceptionResponse(e);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public String handleJsonErrors(HttpMessageNotReadableException exception){
        Pattern ENUM_MSG = Pattern.compile("values accepted for Enum class: (.*)");
        if (exception.getCause() != null && exception.getCause() instanceof InvalidFormatException) {
            Matcher match = ENUM_MSG.matcher(exception.getCause().getMessage());
            if (match.find()) {
                String msg = "value of ENUM should be: " + match.group(1);
                log.error(msg);
                return msg;
            }
        }
        return exceptionResponse(exception);
    }

    @ExceptionHandler(value= {ResourceNotFoundException.class})
    @ResponseStatus(code = HttpStatus.NOT_FOUND)
    String getResponseNotFound(Exception e) {
        return exceptionResponse(e);
    }


    private String exceptionResponse(Exception e) {
        log.error(e.toString());
        return e.getMessage();
    }
}
