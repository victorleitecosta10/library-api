package br.com.victorleitecosta.libraryapi.model.repository;

import br.com.victorleitecosta.libraryapi.api.model.entity.Book;
import br.com.victorleitecosta.libraryapi.api.model.repository.BookRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@DataJpaTest
public class BookRepositoryTest {

    @Autowired
    TestEntityManager entityManager;

    @Autowired
    BookRepository repository;

    @Test
    @DisplayName("Deve retornar verdadeiro quando existir um livro na base com o isbn informado.")
    public void returnTrueWhenIsbnExists() {
//        Cenário
        String isbn = "102";
        Book book = Book.builder().title("A tribo").author("Lindinho").isbn(isbn).build();
        entityManager.persist(book);

//        Execução
        boolean exists = repository.existsByIsbn(isbn);

//        Verificação
        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("Deve retornar falso quando não existir um livro na base com o isbn informado.")
    public void returnFalseWhenIsbnDoesntExists() {
//        Cenário
        String isbn = "102";

//        Execução
        boolean exists = repository.existsByIsbn(isbn);

//        Verificação
        assertThat(exists).isFalse();
    }
}
