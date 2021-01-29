package br.com.victorleitecosta.libraryapi.api.model.repository;

import br.com.victorleitecosta.libraryapi.api.model.entity.Book;
import br.com.victorleitecosta.libraryapi.api.model.entity.Loan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface LoanRepository extends JpaRepository<Loan, Long> {

    boolean existsByBookAndReturnedIsNullOrReturnedIsFalse(@Param("book") Book book);

    Page<Loan> findByBookIsbnOrCustomer(
            @Param("isbn") String isbn,
            @Param("customer") String customer,
            Pageable pageable);

    Page<Loan> findByBook(Book book, Pageable pageable);

    @Query(" select l from Loan l where l.loanDate <= :threeDaysAgo ")
    List<Loan> findByLoanDateLessThanAndReturnedIsNullOrReturnedIsFalse(@Param("threeDaysAgo") LocalDate threeDaysAgo);
}
