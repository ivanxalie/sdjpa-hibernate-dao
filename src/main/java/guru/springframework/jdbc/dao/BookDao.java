package guru.springframework.jdbc.dao;

import guru.springframework.jdbc.domain.Book;

import java.util.List;

public interface BookDao {
    Book getById(Long id);

    Book findByIsbn(String isbn);

    Book saveNewBook(Book book);

    void deleteBookById(Long id);

    Book updateBook(Book book);

    Book findBookByTitle(String title);

    List<Book> findAll();


}
