package br.com.victorleitecosta.libraryapi.api.exception;

import br.com.victorleitecosta.libraryapi.exception.BusinessException;
import org.springframework.validation.BindingResult;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;

public class ApiErrors {
    List<String> errors;

    public ApiErrors(BindingResult bindingResult) {
        this.errors = new ArrayList<>();
        bindingResult.getAllErrors().forEach(error -> this.errors.add(error.getObjectName()));

    }

    public ApiErrors(BusinessException ex) {
        this.errors = asList(ex.getMessage());
    }

    public List<String> getErrors() {
            return errors;
    }
}
