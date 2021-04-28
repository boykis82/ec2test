package org.caltech.miniswingpilot.util.http;

import lombok.extern.slf4j.Slf4j;
import org.caltech.miniswingpilot.exception.DataIntegrityViolationException;
import org.caltech.miniswingpilot.exception.IllegalServiceStatusException;
import org.caltech.miniswingpilot.exception.InvalidInputException;
import org.caltech.miniswingpilot.exception.NotFoundDataException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static org.springframework.http.HttpStatus.*;

@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
@Slf4j
public class GlobalRestControllerExceptionHandler {

    @ResponseStatus(NOT_FOUND)
    @ExceptionHandler(NotFoundDataException.class)
    public HttpErrorInfo handleNotFoundExceptions(ServerHttpRequest request, Exception ex) {
        return HttpErrorInfo.createHttpErrorInfo(NOT_FOUND, request, ex);
    }

    @ResponseStatus(BAD_REQUEST)
    @ExceptionHandler(InvalidInputException.class)
    public HttpErrorInfo handleInvalidInputException(ServerHttpRequest request, Exception ex) {
        return HttpErrorInfo.createHttpErrorInfo(BAD_REQUEST, request, ex);
    }

    @ResponseStatus(INTERNAL_SERVER_ERROR)
    @ExceptionHandler({DataIntegrityViolationException.class, IllegalServiceStatusException.class})
    public HttpErrorInfo handleDataIntegrityViolationException(ServerHttpRequest request, Exception ex) {
        return HttpErrorInfo.createHttpErrorInfo(INTERNAL_SERVER_ERROR, request, ex);
    }
}
