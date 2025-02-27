package co.kr.cocomu.common.exception;

import co.kr.cocomu.common.exception.domain.BadGatewayException;
import co.kr.cocomu.common.exception.domain.BadRequestException;
import co.kr.cocomu.common.exception.domain.NotFoundException;
import co.kr.cocomu.common.exception.domain.UnAuthorizedException;
import co.kr.cocomu.common.exception.dto.ErrorResponse;
import co.kr.cocomu.common.exception.dto.ExceptionResponse;
import jakarta.validation.ConstraintViolationException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler
    public ResponseEntity<ExceptionResponse> handleException(final Exception e) {
        e.printStackTrace();
        log.error("{} : {}", e.getClass(), e.getMessage());

        return ResponseEntity.internalServerError()
            .body(new ExceptionResponse(9000, "알 수 없는 서버 에러가 발생했습니다."));
    }

    @ExceptionHandler
    public ResponseEntity<ExceptionResponse> handleMethodArgumentNotValidException(
        final MethodArgumentNotValidException e
    ) {
        final List<ErrorResponse> errorResponses = e.getBindingResult()
            .getFieldErrors()
            .stream()
            .map(fieldError -> new ErrorResponse(fieldError.getField(), fieldError.getDefaultMessage()))
            .toList();

        return buildErrorResponse(9001, e, errorResponses.toString());
    }

    @ExceptionHandler
    public ResponseEntity<ExceptionResponse> handleMethodArgumentTypeMismatchException(
        final MethodArgumentTypeMismatchException e
    ) {
        final ErrorResponse errorResponse = new ErrorResponse(
            e.getName(),
            Objects.requireNonNull(e.getRequiredType()).getSimpleName() + " 타입으로 변환할 수 없는 요청입니다."
        );
        return buildErrorResponse(9002, e, errorResponse.toString());
    }

    @ExceptionHandler
    public ResponseEntity<ExceptionResponse> handleMissingServletRequestParameterException(
        final MissingServletRequestParameterException e
    ) {
        final ErrorResponse errorResponse = new ErrorResponse(e.getParameterName(), "파라미터가 필요합니다.");
        return buildErrorResponse(9003, e, errorResponse.toString());
    }

    @ExceptionHandler
    public ResponseEntity<ExceptionResponse> handleHttpMessageNotReadableException(
        final HttpMessageNotReadableException e
    ) {
        return buildErrorResponse(9004, e, "잘못된 요청 형식입니다.");
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ExceptionResponse> handleConstraintViolationException(final ConstraintViolationException e) {
        final List<ErrorResponse> errors = e.getConstraintViolations()
            .stream()
            .map(violation -> new ErrorResponse(violation.getPropertyPath().toString(), violation.getMessage()))
            .toList();

        return buildErrorResponse(9005, e, errors.toString());
    }

    @ExceptionHandler
    public ResponseEntity<ExceptionResponse> handleBadRequestException(final BadRequestException e) {
        log.error("{} : {}", e.getClass(), e.getMessage());
        return ResponseEntity.badRequest()
            .body(new ExceptionResponse(e.getExceptionType().getCode(), e.getMessage()));
    }

    @ExceptionHandler
    public ResponseEntity<ExceptionResponse> handleNotFoundException(final NotFoundException e) {
        log.error("{} : {}", e.getClass(), e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(new ExceptionResponse(e.getExceptionType().getCode(), e.getMessage()));
    }

    @ExceptionHandler
    public ResponseEntity<ExceptionResponse> handleUnAuthorizedException(final UnAuthorizedException e) {
        log.error("{} : {}", e.getClass(), e.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body(new ExceptionResponse(e.getExceptionType().getCode(), e.getMessage()));
    }

    @ExceptionHandler
    public ResponseEntity<ExceptionResponse> handleBadGatewayException(final BadGatewayException e) {
        log.error("{} : {}", e.getClass(), e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
            .body(new ExceptionResponse(e.getExceptionType().getCode(), e.getMessage()));
    }

    private ResponseEntity<ExceptionResponse> buildErrorResponse(
        final int code,
        final Exception e,
        final String message
    ) {
        log.error("{} - {}", e.getClass(), message);
        return ResponseEntity.badRequest().body(new ExceptionResponse(code, message));
    }

}