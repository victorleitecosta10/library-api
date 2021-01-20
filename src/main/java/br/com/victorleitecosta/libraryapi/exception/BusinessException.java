package br.com.victorleitecosta.libraryapi.exception;

import org.springframework.validation.BindingResult;

public class BusinessException extends RuntimeException {
    public BusinessException(String s) {
        super(s);
    }

}
