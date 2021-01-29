package br.com.victorleitecosta.libraryapi.service;

import br.com.victorleitecosta.libraryapi.api.dto.LoanFilterDTO;
import br.com.victorleitecosta.libraryapi.api.model.entity.Book;
import br.com.victorleitecosta.libraryapi.api.model.entity.Loan;
import br.com.victorleitecosta.libraryapi.api.model.repository.LoanRepository;
import br.com.victorleitecosta.libraryapi.api.service.LoanService;
import br.com.victorleitecosta.libraryapi.api.service.impl.LoanServiceImpl;
import br.com.victorleitecosta.libraryapi.exception.BusinessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Optional;

import static java.time.LocalDate.now;
import static java.util.Arrays.asList;
import static java.util.Optional.of;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class LoanServiceTest {

    LoanService service;

    @MockBean
    LoanRepository repository;

    @BeforeEach
    public void setUp() {
        this.service = new LoanServiceImpl(repository);
    }

    @Test
    @DisplayName("Deve salvar um empréstimo.")
    public void saveLoanTest() {
        Book book = Book.builder().id(1L).build();
        String customer = "Cláudio";

        Loan savingLoan = Loan.builder()
                .book(book)
                .customer(customer)
                .loanDate(now())
                .build();

        Loan savedLoan = Loan.builder()
                .id(1L)
                .loanDate(now())
                .customer(customer)
                .book(book)
                .build();

        when(repository.existsByBookAndReturnedIsNullOrReturnedIsFalse(book)).thenReturn(false);
        when(repository.save(savingLoan)).thenReturn(savedLoan);

        Loan loan = service.save(savingLoan);

        assertThat(loan.getId()).isEqualTo(savedLoan.getId());
        assertThat(loan.getBook().getId()).isEqualTo(savedLoan.getBook().getId());
        assertThat(loan.getCustomer()).isEqualTo(savedLoan.getCustomer());
        assertThat(loan.getLoanDate()).isEqualTo(savedLoan.getLoanDate());

    }

    @Test
    @DisplayName("Deve lançar erro de negócio ao salvar um empréstimo com livro já emprestado.")
    public void loanedBookSaveTest() {
        Book book = Book.builder().id(1L).build();
        String customer = "Cláudio";

        Loan savingLoan = Loan.builder()
                .book(book)
                .customer(customer)
                .loanDate(now())
                .build();

        Loan savedLoan = Loan.builder()
                .id(1L)
                .loanDate(now())
                .customer(customer)
                .book(book)
                .build();

        when(repository.existsByBookAndReturnedIsNullOrReturnedIsFalse(book)).thenReturn(true);

        Throwable exception = catchThrowable(() -> service.save(savingLoan));

        assertThat(exception)
                .isInstanceOf(BusinessException.class)
                .hasMessage("Book already loaned");

        verify(repository, never()).save(savingLoan);

    }

    @Test
    @DisplayName("Deve obter as informações de um empréstimo pelo ID.")
    public void getLoanDetailsTest() {
//        Cenário
        Long id = 1L;
        Loan loan = createLoan();
        loan.setId(id);

        when(repository.findById(id)).thenReturn(of(loan));

//        Execução
        Optional<Loan> result = service.getById(id);

//        Verificação
        assertThat(result.isPresent()).isTrue();
        assertThat(result.get().getId()).isEqualTo(id);
        assertThat(result.get().getCustomer()).isEqualTo(loan.getCustomer());
        assertThat(result.get().getBook()).isEqualTo(loan.getBook());
        assertThat(result.get().getLoanDate()).isEqualTo(loan.getLoanDate());

        verify(repository).findById(id);
    }

    @Test
    @DisplayName("Deve atualizar um empréstimo.")
    public void updateLoanTest() {
        Loan loan = createLoan();
        loan.setId(1L);
        loan.setReturned(true);

        when(repository.save(loan)).thenReturn(loan);

        Loan updatedLoan = service.update(loan);

        assertThat(updatedLoan.getReturned()).isTrue();
        verify(repository).save(loan);

    }

    @Test
    @DisplayName("Deve filtrar empréstimos.")
    public void findLoanTest() {
//        Cenário
        LoanFilterDTO loanFilterDTO = LoanFilterDTO.builder().customer(createLoan().getCustomer()).isbn("102").build();
        Loan loan = createLoan();
        loan.setId(1L);
        PageRequest pageRequest = PageRequest.of(0, 10);

        List<Loan> lista = asList(loan);
        Page<Loan> page = new PageImpl<Loan>(lista, pageRequest, 1);

        when(repository.findByBookIsbnOrCustomer(
                anyString(),
                anyString(),
                any(PageRequest.class)))
                .thenReturn(page);

//        Execução
        Page<Loan> result = service.find(loanFilterDTO, pageRequest);

//        Verificações
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent()).isEqualTo(lista);
        assertThat(result.getPageable().getPageNumber()).isEqualTo(0);
        assertThat(result.getPageable().getPageSize()).isEqualTo(10);

    }

    public static Loan createLoan() {
        Book book = Book.builder().id(1L).build();
        String customer = "Cláudio";

        return Loan.builder()
                .book(book)
                .customer(customer)
                .loanDate(now())
                .build();
    }

}
