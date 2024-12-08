package guru.springframework.jdbc.dao;

import guru.springframework.jdbc.domain.Book;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;
import org.springframework.beans.factory.annotation.Lookup;
import org.springframework.stereotype.Component;

import java.util.function.Function;

import static guru.springframework.jdbc.config.AppConfig.ENTITY_MANAGER_NAME;

@Component
public class BookDaoImpl implements BookDao {
    @Override
    public Book getById(Long id) {
        return execute(entityManager -> manager().find(Book.class, id));
    }

    private <TYPE> TYPE execute(Function<EntityManager, TYPE> function) {
        EntityManager manager = manager();
        try {
            return function.apply(manager);
        } finally {
            manager.close();
        }
    }

    @Lookup(ENTITY_MANAGER_NAME)
    protected EntityManager manager() {
        return null;
    }

    @Override
    public Book saveNewBook(Book book) {
        return execute(manager -> {
            EntityTransaction transaction = manager.getTransaction();
            transaction.begin();
            manager.persist(book);
            transaction.commit();
            return book;
        });
    }

    @Override
    public void deleteBookById(Long id) {
        execute(manager -> {
            EntityTransaction transaction = manager.getTransaction();
            transaction.begin();
            manager.remove(manager.find(Book.class, id));
            transaction.commit();
            return Void.class;
        });
    }

    @Override
    public Book updateBook(Book book) {
        return execute(manager -> {
            EntityTransaction transaction = manager.getTransaction();
            transaction.begin();
            manager.merge(book);
            transaction.commit();
            return book;
        });
    }

    @Override
    public Book findBookByTitle(String title) {
        return execute(manager -> {
            TypedQuery<Book> query = manager.createQuery(
                    "select b from Book b where b.title = :title"
                    , Book.class);
            query.setParameter("title", title);
            return query.getSingleResult();
        });
    }
}
