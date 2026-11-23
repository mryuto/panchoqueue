package com.panchoqueue.panchoqueue.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class ApiExceptionHandler {

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ProblemDetail> manejarValidacion(MethodArgumentNotValidException exception) {
		Map<String, String> errores = new LinkedHashMap<>();
		exception.getBindingResult().getFieldErrors().forEach(error ->
				errores.putIfAbsent(error.getField(), error.getDefaultMessage()));

		ProblemDetail detalle = ProblemDetail.forStatusAndDetail(
				HttpStatus.BAD_REQUEST,
				"Uno o más datos enviados no son válidos.");
		detalle.setTitle("Solicitud inválida");
		detalle.setProperty("errores", errores);
		return ResponseEntity.badRequest().body(detalle);
	}
}
