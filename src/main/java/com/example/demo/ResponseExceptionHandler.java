package com.example.demo;

import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
//every exception for every request must be generated 
public class ResponseExceptionHandler extends ResponseEntityExceptionHandler {

	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid(
			MethodArgumentNotValidException ex,
			HttpHeaders headers, HttpStatusCode status, WebRequest request) {
		List<String> validationErrors = ex.getBindingResult()
				.getFieldErrors()
				.stream()
				.map( FieldError::getDefaultMessage)
				.toList();	
		System.out.println("Errors: "+validationErrors);

		return new ResponseEntity<>(validationErrors,HttpStatus.BAD_REQUEST);
	}
}
