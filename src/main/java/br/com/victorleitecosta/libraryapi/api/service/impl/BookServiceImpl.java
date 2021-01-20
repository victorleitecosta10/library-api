package br.com.victorleitecosta.libraryapi.api.service.impl;

import br.com.victorleitecosta.libraryapi.api.model.entity.Book;
import br.com.victorleitecosta.libraryapi.api.service.BookService;
import br.com.victorleitecosta.libraryapi.api.model.repository.BookRepository;
import br.com.victorleitecosta.libraryapi.exception.BusinessException;
import org.springframework.stereotype.Service;

@Service
public class BookServiceImpl implements BookService {
    private BookRepository repository;

    public BookServiceImpl(BookRepository repository) {
        this.repository = repository;
    }

    @Override
    public Book save(Book book) {
        if (repository.existsByIsbn(book.getIsbn())) {
            throw new BusinessException("Isbn já cadastrado.");
        }
        return repository.save(book);
    }
}
