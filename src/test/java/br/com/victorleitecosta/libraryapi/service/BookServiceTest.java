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
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Optional;

import static java.util.Arrays.asList;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
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

    @Test
    @DisplayName("Deve obter um livro por id.")
    public void getByIdTest() {
        Long id = 1L;
        Book book = createValidBook();
        book.setId(id);
        when(repository.findById(id)).thenReturn(of(book));

//        Execução
        Optional<Book> foundBook = service.getById(id);

//        Verificações
        assertThat(foundBook.isPresent()).isTrue();
        assertThat(foundBook.get().getId()).isEqualTo(id);
        assertThat(foundBook.get().getTitle()).isEqualTo(book.getTitle());
        assertThat(foundBook.get().getAuthor()).isEqualTo(book.getAuthor());
        assertThat(foundBook.get().getIsbn()).isEqualTo(book.getIsbn());
    }

    @Test
    @DisplayName("Deve retornar vazio ao obter um livro por id quando ele não existe na base.")
    public void bookNotFoundByIdTest() {
        Long id = 1L;
        when(repository.findById(id)).thenReturn(empty());

//        Execução
        Optional<Book> book = service.getById(id);

//        Verificações
        assertThat(book.isPresent()).isFalse();
    }

    @Test
    @DisplayName("Deve deletar um livro.")
    public void deleteBookTest() {
        Book book = Book.builder().id(1L).build();

//        Execução
        assertDoesNotThrow(() -> service.delete(book));

//        Verificações
        verify(repository, times(1)).delete(book);
    }

    @Test
    @DisplayName("Deve ocorrer erro ao tentar deletar um livro inexistente.")
    public void deleteInvalidBookTest() {
        Book book = new Book();

//        Execução
        assertThrows(IllegalArgumentException.class, () -> service.delete(book));

//        Verificações
        verify(repository, never()).delete(book);
    }

    @Test
    @DisplayName("Deve atualizar um livro.")
    public void updateBookTest() {
        Long id = 1L;
//        Livro a atualizar
        Book updatingBook = Book.builder().id(id).build();

//        Simulação
        Book updatedBook = createValidBook();
        updatedBook.setId(id);

        when(repository.save(updatingBook)).thenReturn(updatedBook);

//        Execução
        Book book = service.update(updatingBook);

//        Verificações
        assertThat(book.getId()).isEqualTo(updatedBook.getId());
        assertThat(book.getTitle()).isEqualTo(updatedBook.getTitle());
        assertThat(book.getAuthor()).isEqualTo(updatedBook.getAuthor());
        assertThat(book.getIsbn()).isEqualTo(updatedBook.getIsbn());
    }

    @Test
    @DisplayName("Deve ocorrer erro ao tentar atualizar um livro inexistente.")
    public void updateInvalidBookTest() {
        Book book = new Book();

//        Execução
        assertThrows(IllegalArgumentException.class, () -> service.update(book));

//        Verificações
        verify(repository, never()).save(book);
    }

    @Test
    @DisplayName("Deve filtrar livros.")
    public void findBookTest() {
//        Cenário
        Book book = createValidBook();
        PageRequest pageRequest = PageRequest.of(0, 10);

        List<Book> lista = asList(book);
        Page<Book> page = new PageImpl<Book>(lista, pageRequest, 1);

        when(repository.findAll(any(Example.class), any(PageRequest.class)))
                .thenReturn(page);

//        Execução
        Page<Book> result = service.find(book, pageRequest);

//        Verificações
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent()).isEqualTo(lista);
        assertThat(result.getPageable().getPageNumber()).isEqualTo(0);
        assertThat(result.getPageable().getPageSize()).isEqualTo(10);

    }

    private Book createValidBook() {
        return Book.builder().title("A tribo").author("Lindinho").isbn("102").build();
    }

}
