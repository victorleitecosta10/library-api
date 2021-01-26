package br.com.victorleitecosta.libraryapi.api.service;

import br.com.victorleitecosta.libraryapi.api.model.entity.Loan;

public interface LoanService {
    Loan save(Loan loan);
}
