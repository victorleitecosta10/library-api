package br.com.victorleitecosta.libraryapi.model.repository;

import br.com.victorleitecosta.libraryapi.api.model.entity.Book;
import br.com.victorleitecosta.libraryapi.api.model.entity.Loan;
import br.com.victorleitecosta.libraryapi.api.model.repository.LoanRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.util.List;

import static br.com.victorleitecosta.libraryapi.model.repository.BookRepositoryTest.createNewBook;
import static java.time.LocalDate.now;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.data.domain.PageRequest.of;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@DataJpaTest
public class LoanRepositoryTest {

    @Autowired
    private LoanRepository repository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    @DisplayName("Deve verificar se existe empréstimo não devolvido para o livro.")
    public void existsByBookAndNotReturnedTest() {
//        Cenário
        Loan loan = createAndPersistLoan(now());
        Book book = loan.getBook();

//        Execução
        boolean exists = repository.existsByBookAndReturnedIsNullOrReturnedIsFalse(book);

        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("Deve buscar empréstimo pelo isbn do livro ou customer.")
    public void findByBookIsbnOrCustomerTest() {
        Loan loan = createAndPersistLoan(now());

        Page<Loan> result = repository.findByBookIsbnOrCustomer("102", "Cláudio", of(0, 10));

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent()).contains(loan);
        assertThat(result.getPageable().getPageSize()).isEqualTo(10);
        assertThat(result.getPageable().getPageNumber()).isEqualTo(0);
        assertThat(result.getTotalElements()).isEqualTo(1);
    }

    @Test
    @DisplayName("Deve obter empréstimos cuja data empréstimo for menor ou igual a três dias atrás e não retornados.")
    public void findByLoanDateLessThanAndNotReturnedTest() {
        Loan loan = createAndPersistLoan(now().minusDays(5));

        List<Loan> result = repository.findByLoanDateLessThanAndReturnedIsNullOrReturnedIsFalse(now().minusDays(4));

        assertThat(result).hasSize(1).contains(loan);

    }

    @Test
    @DisplayName("Deve retornar vazio quando não houver empréstimos atrasados.")
    public void notfindByLoanDateLessThanAndNotReturnedTest() {
        Loan loan = createAndPersistLoan(now());

        List<Loan> result = repository.findByLoanDateLessThanAndReturnedIsNullOrReturnedIsFalse(now().minusDays(4));

        assertThat(result).isEmpty();

    }

    private Loan createAndPersistLoan(LocalDate loandate) {
        Book book = createNewBook("123");
        entityManager.persist(book);

        Loan loan = Loan.builder().book(book).customer("Cláudio").loanDate(loandate).build();
        entityManager.persist(loan);
        return loan;
    }
}
