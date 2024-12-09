package guru.springframework.jdbc.dao;

import guru.springframework.jdbc.domain.Book;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import org.springframework.beans.factory.annotation.Lookup;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Function;

import static guru.springframework.jdbc.config.AppConfig.ENTITY_MANAGER_NAME;
import static guru.springframework.jdbc.domain.Book.FIND_BY_TITLE;

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

    @Override
    public Book findByIsbn(String isbn) {
        return execute(entityManager -> {
            TypedQuery<Book> query = entityManager.createQuery(
                    "select b from Book b where b.isbn = :isbn",
                    Book.class
            );
            query.setParameter("isbn", isbn);
            return query.getSingleResult();
        });
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
            TypedQuery<Book> query = manager.createNamedQuery(FIND_BY_TITLE, Book.class);
            query.setParameter("title", title);
            return query.getSingleResult();
        });
    }

    @Override
    public Book findBookByTitleCriteria(String title) {
        return execute(manager -> {
            CriteriaBuilder criteriaBuilder = manager.getCriteriaBuilder();
            CriteriaQuery<Book> query = criteriaBuilder.createQuery(Book.class);

            Root<Book> root = query.from(Book.class);
            ParameterExpression<String> titleParam = criteriaBuilder.parameter(String.class);

            Predicate titlePredicate = criteriaBuilder.equal(root.get("title"), titleParam);

            query.select(root).where(titlePredicate);

            TypedQuery<Book> typedQuery = manager.createQuery(query);
            typedQuery.setParameter(titleParam, title);

            return typedQuery.getSingleResult();
        });
    }

    @Override
    public List<Book> findAll() {
        return execute(entityManager ->
                entityManager
                        .createNamedQuery(Book.FIND_ALL, Book.class)
                        .getResultList()
        );
    }

    @Override
    public Book findBookByTitleNative(String title) {
        return (Book) execute(manager -> {
            Query query = manager.createNativeQuery("select * from book where title = :title",
                    Book.class);
            query.setParameter("title", title);
            return query.getSingleResult();
        });
    }
}
