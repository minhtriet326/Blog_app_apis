package com.blog.exceptions;

import com.blog.auth.exceptions.RefreshTokenException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ResourceNotFoundException.class)
    public ProblemDetail resourceNotFoundExceptionHandler(ResourceNotFoundException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail methodArgumentNotValidExceptionHnadler(MethodArgumentNotValidException ex) {

        //Tạo problemDetail với status và chi tiết lỗi
        ProblemDetail problemDetail = ProblemDetail
                .forStatusAndDetail(HttpStatus.BAD_REQUEST, "One or more fields are not valid!");

        //Tạo map chứa các lỗi
        Map<String, String> errors = new HashMap<>();

//        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
//            String fieldName = error.getField();
//            String errorMessage = error.getDefaultMessage();
//
//            errors.put(fieldName, errorMessage);
//        }

        ex.getBindingResult().getFieldErrors().forEach( error -> {
            String fieldName = error.getField();
            String errorMessage = error.getDefaultMessage();

            errors.put(fieldName, errorMessage);
        });

        //Thêm lỗi vào problemDetail, setProperty cho phép thêm các thuộc tính tùy chỉnh
        problemDetail.setProperty("List of Errors", errors);

        return problemDetail;
    }

    @ExceptionHandler(CustomUniqueConstraintViolationException.class)
    public ProblemDetail customUniqueConstraintViolationExceptionHandler(CustomUniqueConstraintViolationException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(RefreshTokenException.class)
    public ProblemDetail refreshTokenExceptionHandler(RefreshTokenException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(FileServiceException.class)
    public ProblemDetail fileServiceExceptionHandler(FileServiceException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
    }
}
