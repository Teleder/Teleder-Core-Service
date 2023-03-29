package teleder.core.filters;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import teleder.core.exceptions.BadRequestException;
import teleder.core.exceptions.ForbiddenException;
import teleder.core.exceptions.NotFoundException;
import teleder.core.exceptions.UnauthorizedException;

@RestControllerAdvice
public class GlobalExceptionFilter {
    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<Object> handleUnauthorizedException(UnauthorizedException ex, HttpServletRequest request) {
        return new ResponseEntity<>(createError(ex, request, HttpStatus.UNAUTHORIZED.value()), HttpStatus.UNAUTHORIZED);
    }
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<Object> handleBadRequestException(BadRequestException ex, HttpServletRequest request) {
        return new ResponseEntity<>(createError(ex, request, HttpStatus.BAD_REQUEST.value()), HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<Object> handleNotFoundException(NotFoundException ex, HttpServletRequest request) {
        return new ResponseEntity<>(createError(ex, request, HttpStatus.NOT_FOUND.value()), HttpStatus.NOT_FOUND);
    }
    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<Object> handleForbiddenException(ForbiddenException ex, HttpServletRequest request) {
        return new ResponseEntity<>(createError(ex, request, HttpStatus.FORBIDDEN.value()), HttpStatus.FORBIDDEN);
    }
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleInternalException(RuntimeException ex, HttpServletRequest request) {
        return new ResponseEntity<>(createError(ex, request, HttpStatus.INTERNAL_SERVER_ERROR.value()), HttpStatus.INTERNAL_SERVER_ERROR);
    }
    public ExceptionResponse createError(RuntimeException ex, HttpServletRequest request, int statusCode) {
        ExceptionResponse errorResponse = new ExceptionResponse();
        errorResponse.setStatusCode(statusCode);
        errorResponse.setPath(request.getRequestURL().toString() + "?" + request.getQueryString());
        errorResponse.setMessage(ex.getLocalizedMessage());
        errorResponse.setError(ex.getCause() == null ? "Unknown" : ex.getCause().toString());
        return errorResponse;
    }
}
