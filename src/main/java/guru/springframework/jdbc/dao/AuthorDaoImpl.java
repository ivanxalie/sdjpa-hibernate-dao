package guru.springframework.jdbc.dao;

import guru.springframework.jdbc.domain.Author;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaDelete;
import lombok.RequiredArgsConstructor;
import org.hibernate.Criteria;
import org.hibernate.query.criteria.internal.CriteriaBuilderImpl;
import org.hibernate.query.criteria.internal.CriteriaDeleteImpl;
import org.springframework.beans.factory.annotation.Lookup;
import org.springframework.stereotype.Component;

import java.util.Map;

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
        String jpql = "select a from Author a where a.firstName = :firstName and a.lastName = :lastName";
        TypedQuery<Author> query = manager().createQuery(jpql, Author.class);
        query.setParameter("firstName", firstName);
        query.setParameter("lastName", lastName);
        return query.getSingleResult();
    }

    @Override
    public Author saveNewAuthor(Author author) {
        EntityManager manager = manager();
        author = manager.merge(author);
        EntityTransaction transaction = manager.getTransaction();
        transaction.begin();
        manager.persist(author);
        transaction.commit();
        return author;
    }

    @Override
    public Author updateAuthor(Author author) {
        return saveNewAuthor(author);
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
