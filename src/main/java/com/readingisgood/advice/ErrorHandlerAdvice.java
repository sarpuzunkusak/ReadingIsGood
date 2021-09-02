package com.readingisgood.advice;

import java.util.Collections;
import java.util.stream.Collectors;

import javax.validation.ConstraintViolationException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import com.readingisgood.controller.resource.ErrorResource;
import com.readingisgood.exception.AlreadyUpdatedException;
import com.readingisgood.exception.ApplicationException;
import com.readingisgood.exception.DuplicateEntryException;
import com.readingisgood.exception.OutOfStockException;
import com.readingisgood.exception.RecordNotFoundException;

@ControllerAdvice
public class ErrorHandlerAdvice {

	@ExceptionHandler(ConstraintViolationException.class)
	@ResponseBody
	ResponseEntity<ErrorResource> handleConstraintViolationException(ConstraintViolationException e) {
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResource(
				e.getConstraintViolations().stream().map(cv -> cv.getMessage()).collect(Collectors.toList())));

	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	@ResponseBody
	ResponseEntity<ErrorResource> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResource(e.getBindingResult()
				.getFieldErrors().stream().map(fe -> fe.getDefaultMessage()).collect(Collectors.toList())));
	}

	@ExceptionHandler(MissingRequestHeaderException.class)
	@ResponseBody
	ResponseEntity<ErrorResource> handleMissingRequestHeaderException(MissingRequestHeaderException e) {
		return ResponseEntity.status(HttpStatus.BAD_REQUEST)
				.body(new ErrorResource(Collections.singletonList(e.getMessage())));
	}

	@ExceptionHandler(OutOfStockException.class)
	@ResponseBody
	ResponseEntity<ErrorResource> handleOutOfStockException(OutOfStockException e) {
		return ResponseEntity.status(HttpStatus.BAD_REQUEST)
				.body(new ErrorResource(Collections.singletonList(e.getMessage())));
	}

	@ExceptionHandler(RecordNotFoundException.class)
	@ResponseBody
	ResponseEntity<ErrorResource> handleRecordNotFoundException(RecordNotFoundException e) {
		return ResponseEntity.status(HttpStatus.NOT_FOUND)
				.body(new ErrorResource(Collections.singletonList(e.getMessage())));
	}

	@ExceptionHandler(AlreadyUpdatedException.class)
	@ResponseBody
	ResponseEntity<ErrorResource> handleAlreadyUpdatedException(AlreadyUpdatedException e) {
		return ResponseEntity.status(HttpStatus.CONFLICT)
				.body(new ErrorResource(Collections.singletonList(e.getMessage())));
	}

	@ExceptionHandler(DuplicateEntryException.class)
	@ResponseBody
	ResponseEntity<ErrorResource> handleDuplicateEntryException(DuplicateEntryException e) {
		return ResponseEntity.status(HttpStatus.CONFLICT)
				.body(new ErrorResource(Collections.singletonList(e.getMessage())));
	}

	@ExceptionHandler(ApplicationException.class)
	@ResponseBody
	ResponseEntity<ErrorResource> handleApplicationException(ApplicationException e) {
		return ResponseEntity.status(HttpStatus.BAD_REQUEST)
				.body(new ErrorResource(Collections.singletonList(e.getMessage())));
	}

	@ExceptionHandler(Exception.class)
	@ResponseBody
	ResponseEntity<ErrorResource> handleException(Exception e) {
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
				.body(new ErrorResource(Collections.singletonList(e.getMessage())));
	}
}
