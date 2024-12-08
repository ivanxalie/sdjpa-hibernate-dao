package guru.springframework.jdbc.dao;

import guru.springframework.jdbc.domain.Author;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Lookup;
import org.springframework.stereotype.Component;

import static guru.springframework.jdbc.config.AppConfig.ENTITY_MANAGER_NAME;

/**
 * Created by jt on 8/28/21.
 */
@Component
@RequiredArgsConstructor
public class AuthorDaoImpl implements AuthorDao {

    @Override
    public Author getById(Long id) {
        return manager().find(Author.class, id);
    }

    @Override
    public Author findAuthorByName(String firstName, String lastName) {
        TypedQuery<Author> query = manager().createQuery(
                "select a from Author a where a.firstName = :firstName " +
                        "and a.lastName = :lastName", Author.class);
        query.setParameter("firstName", firstName);
        query.setParameter("lastName", lastName);
        return query.getSingleResult();
    }

    @Override
    public Author saveNewAuthor(Author author) {
        EntityManager manager = manager();
        EntityTransaction transaction = manager.getTransaction();
        transaction.begin();
        manager.persist(author);
        transaction.commit();
        return author;
    }

    @Override
    public Author updateAuthor(Author author) {
        EntityManager manager = manager();
        EntityTransaction transaction = manager.getTransaction();
        transaction.begin();
        manager.merge(author);
        transaction.commit();
        return author;
    }

    @Override
    public void deleteAuthorById(Long id) {
        EntityManager manager = manager();
        EntityTransaction transaction = manager.getTransaction();
        transaction.begin();
        manager.remove(manager.find(Author.class, id));
        transaction.commit();
    }

    @Lookup(ENTITY_MANAGER_NAME)
    protected EntityManager manager() {
        return null;
    }
}
