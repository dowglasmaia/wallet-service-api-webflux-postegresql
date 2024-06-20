package com.dowglasmaia.wallet.exeptions.handler;

import com.dowglasmaia.wallet.exeptions.BusinessException;
import com.dowglasmaia.wallet.exeptions.NotFoundException;
import com.dowglasmaia.wallet.exeptions.StandardError;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.support.HttpRequestHandlerServlet;
import org.springframework.web.context.support.ServletContextResourceLoader;
import org.springframework.web.filter.ServletContextRequestLoggingFilter;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Log4j2
@RestControllerAdvice
public class AdviceExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    public Mono<ResponseEntity<StandardError>> handleNotFound(NotFoundException ex, ServerWebExchange servlet){
        String path = servlet.getRequest().getPath().pathWithinApplication().value();

        StandardError error = StandardError.builder()
              .timestamp(System.currentTimeMillis())
              .error("Not Found")
              .message(ex.getMessage())
              .status(HttpStatus.NOT_FOUND.value())
              .path(path)
              .build();
        return Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND).body(error));
    }

    @ExceptionHandler(BusinessException.class)
    public Mono<ResponseEntity<StandardError>> handleBusinessException(BusinessException ex, ServerWebExchange servlet){
        String path = servlet.getRequest().getPath().pathWithinApplication().value();

        StandardError error = StandardError.builder()
              .timestamp(System.currentTimeMillis())
              .error("Business Error")
              .message(ex.getMessage())
              .status(ex.getHttpStatus().value())
              .path(path)
              .build();
        return Mono.just(ResponseEntity.status(ex.getHttpStatus()).body(error));
    }
}
