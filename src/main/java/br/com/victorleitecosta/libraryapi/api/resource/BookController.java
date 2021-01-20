package br.com.victorleitecosta.libraryapi.api.resource;

import br.com.victorleitecosta.libraryapi.api.dto.BookDTO;
import br.com.victorleitecosta.libraryapi.api.exception.ApiErrors;
import br.com.victorleitecosta.libraryapi.api.model.entity.Book;
import br.com.victorleitecosta.libraryapi.api.service.BookService;
import br.com.victorleitecosta.libraryapi.exception.BusinessException;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/books")
public class BookController {

    private ModelMapper modelMapper;

    private BookService service;

    public BookController(BookService service, ModelMapper modelMapper) {
        this.service = service;
        this.modelMapper = modelMapper;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookDTO create(@RequestBody @Valid BookDTO dto) {
        Book entity = modelMapper.map(dto, Book.class);

        entity = service.save(entity);

        return modelMapper.map(entity, BookDTO.class);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiErrors handleValidationExceptions(MethodArgumentNotValidException ex) {
        BindingResult bindingResult = ex.getBindingResult();

        return new ApiErrors(bindingResult);
    }

    @ExceptionHandler(BusinessException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiErrors handleBusinessException(BusinessException ex) {
        return new ApiErrors(ex);
    }
}
