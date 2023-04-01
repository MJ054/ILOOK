package com.example.illook.util;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiControllerAdvice {

    //유효성 검사 오류
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<?>> handleValidationExceptions(BindingResult bindingResult){
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ApiResponse.createFail(bindingResult));
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ApiResponse<?>> aaa2(IllegalStateException e){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.createError(e.getMessage()));
    }

    //컨트롤러의 코드상에서 파라미터를 요구하였으나, 결여되있을 때 발생하는 에러러
   @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiResponse<?>> missing(MissingServletRequestParameterException e){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.createError(e.getMessage()));
    }

}
