package br.com.victorleitecosta.libraryapi.service;

import br.com.victorleitecosta.libraryapi.api.model.entity.Book;
import br.com.victorleitecosta.libraryapi.api.model.repository.BookRepository;
import br.com.victorleitecosta.libraryapi.api.service.BookService;
import br.com.victorleitecosta.libraryapi.api.service.impl.BookServiceImpl;
import br.com.victorleitecosta.libraryapi.exception.BusinessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class BookServiceTest {

    BookService service;

    @MockBean
    BookRepository repository;

    @BeforeEach
    public void setUp() {
        this.service = new BookServiceImpl(repository);
    }

    @Test
    @DisplayName("Deve salvar um livro.")
    public void saveBook() {
//        Cenário
        Book book = createValidBook();
        when(repository.existsByIsbn(anyString())).thenReturn(false);
        when(repository.save(book)).thenReturn(
                Book.builder().id(1l)
                        .title("A tribo")
                        .author("Lindinho")
                        .isbn("102").build());

//        Execução
        Book savedBook = service.save(book);

//        Verificação
        assertThat(savedBook.getId()).isNotNull();
        assertThat(savedBook.getTitle()).isEqualTo("A tribo");
        assertThat(savedBook.getAuthor()).isEqualTo("Lindinho");
        assertThat(savedBook.getIsbn()).isEqualTo("102");
    }

    @Test
    @DisplayName("Deve lançar um erro de negócio ao tentar salvar um livro com isbn duplicado.")
    public void shouldNotSaveABookWithDuplicatedIsbn() {
//        Cenário
        Book book = createValidBook();
        when(repository.existsByIsbn(anyString())).thenReturn(true);

//        Execução
        Throwable exception = catchThrowable(() -> service.save(book));

//        Verificações
        assertThat(exception)
                .isInstanceOf(BusinessException.class)
                .hasMessage("Isbn já cadastrado.");
        verify(repository, never()).save(book);
    }

    private Book createValidBook() {
        return Book.builder().title("A tribo").author("Lindinho").isbn("102").build();
    }

}
