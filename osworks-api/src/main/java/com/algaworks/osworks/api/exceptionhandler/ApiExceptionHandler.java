package com.algaworks.osworks.api.exceptionhandler;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.algaworks.osworks.domain.exception.EntidadeNaoEncontradaException;
import com.algaworks.osworks.domain.exception.NegocioException;

@ControllerAdvice //Mostra que é um componente do spring que mostra que essa classe é para tratamente de exception de todos os controladores
public class ApiExceptionHandler extends ResponseEntityExceptionHandler{
	@Autowired
	private MessageSource messageSource; //Interface Spring usada para resolver mensagem
	
	@ExceptionHandler(EntidadeNaoEncontradaException.class)
	public ResponseEntity<Object> handleEntidadeNaoEncontradaException(NegocioException ex, WebRequest request) {
		var status =  HttpStatus.NOT_FOUND;
		var problema = new Problema();
		problema.setStatus(status.value());
		problema.setTitulo(ex.getMessage());
		problema.setDataHora(OffsetDateTime.now());
	
		return handleExceptionInternal(ex, problema, new HttpHeaders(), status, request);
	}
	
	
	
	@ExceptionHandler(NegocioException.class)
	public ResponseEntity<Object> handleNegocio(NegocioException ex, WebRequest request) {
		var status =  HttpStatus.BAD_REQUEST;
		var problema = new Problema();
		problema.setStatus(status.value());
		problema.setTitulo(ex.getMessage());
		problema.setDataHora(OffsetDateTime.now());
	
		return handleExceptionInternal(ex, problema, new HttpHeaders(), status, request);
	}
	
	
	@Override //Sobrescrevendo um método de tratamento de exception (handleMethodArhumentNotValidad)
	protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
			HttpHeaders headers, HttpStatus status, WebRequest request) {
		// TODO Auto-generated method stub
		var campos = new ArrayList<Problema.Campo>();
		
		for (ObjectError error : ex.getBindingResult().getAllErrors()) {
			//Adicionando todos os erros ao ArrayList Campos, pra depois atribuir ao problema
			String nome = ((FieldError) error).getField(); //Nome de campo 
			String mensagem = messageSource.getMessage(error, LocaleContextHolder.getLocale());
			
			campos.add(new Problema.Campo(nome, mensagem));
		}
		
		var problema =  new Problema();
		problema.setStatus(status.value());
		problema.setTitulo("Um ou mais campos estão inválidos."
				+ " Faça o preenchimento correto e tente novamente.");
		problema.setDataHora(OffsetDateTime.now());
		
		problema.setCampos(campos);
		return super.handleExceptionInternal(ex, problema, headers, status, request);
	}
}
