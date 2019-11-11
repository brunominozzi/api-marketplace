package com.leroy.apimarketplace.resources.exception;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.leroy.apimarketplace.services.exception.ObjectNotFoundException;

/**
 * Classe manipuladora de exceções na camada Resource.	
 * Controller Advice responsável por tratar alguns tipos de erros nas requisições.
 * 
 * @author bruno.minozzi
 * @since 08/11/2019
 */
@ControllerAdvice
public class ResourceExceptionHandler {
	
	@ExceptionHandler(ObjectNotFoundException.class)
	public ResponseEntity<StandardErrorRest> objectNotFound(ObjectNotFoundException e, HttpServletRequest request){
		HttpStatus status = HttpStatus.NOT_FOUND;
		StandardErrorRest err = new StandardErrorRest(System.currentTimeMillis(), status.value(), "Não encontrado", e.getMessage(), request.getRequestURI());
		return ResponseEntity.status(status).body(err);
	}
	
	@ExceptionHandler(JsonProcessingException.class)
	public ResponseEntity<StandardErrorRest> jsonProcessingException(JsonProcessingException e, HttpServletRequest request){
		HttpStatus status = HttpStatus.UNAUTHORIZED;
		StandardErrorRest err = new StandardErrorRest(System.currentTimeMillis(), status.value(), "Falha no processamento da planilha", e.getMessage(), request.getRequestURI());
		return ResponseEntity.status(status).body(err);
	}
	

}
