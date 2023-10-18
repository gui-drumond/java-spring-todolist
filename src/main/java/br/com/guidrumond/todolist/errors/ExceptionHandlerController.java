package br.com.guidrumond.todolist.errors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice // Anotação do Spring para definir classes globais de tratamento de exceções
public class ExceptionHandlerController {
    

    @ExceptionHandler(HttpMessageNotReadableException.class) //definir qual tipo de Exception
    public ResponseEntity<String> handleHttpMessageNotReadableException(HttpMessageNotReadableException e ){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMostSpecificCause().getMessage());
    }

}
