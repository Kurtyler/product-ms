package com.collabera.kurt.product.exception;

import com.collabera.kurt.product.dto.response.ApiErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    public final ResponseEntity<ApiErrorResponse> handleNotFoundException(
            final NotFoundException ex,
            final HandlerMethod handlerMethod
    ) {

        log.error("[NotFoundException] Handler: {} Message: {}",
                handlerMethod.getMethod().getDeclaringClass(), ex.getMessage());

        final ApiErrorResponse apiErrorResponse = new ApiErrorResponse("Not found exception", ex.getMessage());
        apiErrorResponse.setCode(ex.toCode());

        return new ResponseEntity<>(apiErrorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(InvalidRequestException.class)
    public final ResponseEntity<ApiErrorResponse> handleInvalidInputException(
            final InvalidRequestException ex,
            final HandlerMethod handlerMethod
    ) {

        log.error("[InvalidRequestException] Handler: {} Message: {}",
                handlerMethod.getMethod().getDeclaringClass(), ex.getMessage());

        final ApiErrorResponse apiErrorResponse = new ApiErrorResponse(
                "Invalid Request Exception", ex.getMessage());
        apiErrorResponse.setCode(ex.toCode());

        return new ResponseEntity<>(apiErrorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidOrderException.class)
    public final ResponseEntity<ApiErrorResponse> handleInvalidOrderException(
            final InvalidOrderException ex,
            final HandlerMethod handlerMethod
    ) {

        log.error("[InvalidOrderException] Handler: {} Message: {}",
                handlerMethod.getMethod().getDeclaringClass(), ex.getMessage());

        final ApiErrorResponse apiErrorResponse = new ApiErrorResponse("Invalid order exception", ex.getMessage());
        apiErrorResponse.setCode(ex.toCode());

        return new ResponseEntity<>(apiErrorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public final ResponseEntity<ApiErrorResponse> handleRuntimeExceptions(
            final Exception ex,
            final HandlerMethod handlerMethod
    ) {
        final InternalServerErrorException internalServerErrorException = new
                InternalServerErrorException(ex.getMessage());
        log.error("[Exception] Handler: {} Message: {}",
                handlerMethod.getMethod().getDeclaringClass(), ex.getMessage());

        final ApiErrorResponse apiErrorResponse = new ApiErrorResponse();
        apiErrorResponse.setCode(internalServerErrorException.toCode());
        apiErrorResponse.setMessage(ex.getMessage());
        apiErrorResponse.setTitle("Something happened");

        return new ResponseEntity<>(apiErrorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
