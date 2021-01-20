package br.com.victorleitecosta.libraryapi.api.model.repository;

import br.com.victorleitecosta.libraryapi.api.model.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRepository extends JpaRepository<Book, Long> {
    boolean existsByIsbn(String isbn);
}
