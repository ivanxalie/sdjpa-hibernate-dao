package guru.springframework.jdbc.dao;

import guru.springframework.jdbc.domain.Author;
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
import static guru.springframework.jdbc.domain.Author.FIND_ALL;
import static guru.springframework.jdbc.domain.Author.FIND_BY_NAME;

/**
 * Created by jt on 8/28/21.
 */
@Component
public class AuthorDaoImpl implements AuthorDao {

    @Override
    public Author getById(Long id) {
        return execute(manager -> manager.find(Author.class, id));
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
    public Author findAuthorByName(String firstName, String lastName) {
        return execute(manager -> {
            TypedQuery<Author> query = manager.createNamedQuery(FIND_BY_NAME, Author.class);
            query.setParameter("firstName", firstName);
            query.setParameter("lastName", lastName);
            return query.getSingleResult();
        });
    }

    @Override
    public Author findAuthorByNameCriteria(String firstName, String lastName) {
        return execute(entityManager -> {
            CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
            CriteriaQuery<Author> query = criteriaBuilder.createQuery(Author.class);

            Root<Author> root = query.from(Author.class);
            ParameterExpression<String> firstNameParam = criteriaBuilder.parameter(String.class);
            ParameterExpression<String> lastNameParam = criteriaBuilder.parameter(String.class);

            Predicate firstNamePredicate = criteriaBuilder.equal(root.get("firstName"), firstNameParam);
            Predicate lastNamePredicate = criteriaBuilder.equal(root.get("lastName"), lastNameParam);

            query.select(root).where(criteriaBuilder.and(firstNamePredicate, lastNamePredicate));

            TypedQuery<Author> typedQuery = entityManager.createQuery(query);
            typedQuery.setParameter(firstNameParam, firstName);
            typedQuery.setParameter(lastNameParam, lastName);

            return typedQuery.getSingleResult();
        });
    }

    @Override
    public Author findAuthorByNameNative(String firstName, String lastName) {
        return (Author) execute(manager -> {
            Query query = manager.createNativeQuery(
                    "select * from author where first_name = ? and last_name = ?", Author.class);
            query.setParameter(1, firstName);
            query.setParameter(2, lastName);
            return query.getSingleResult();
        });
    }

    @Override
    public Author saveNewAuthor(Author author) {
        return execute(manager -> {
            EntityTransaction transaction = manager.getTransaction();
            transaction.begin();
            manager.persist(author);
            transaction.commit();
            return author;
        });
    }

    @Override
    public Author updateAuthor(Author author) {
        return execute(manager -> {
            EntityTransaction transaction = manager.getTransaction();
            transaction.begin();
            manager.merge(author);
            transaction.commit();
            return author;
        });
    }

    @Override
    public void deleteAuthorById(Long id) {
        execute(manager -> {
            EntityTransaction transaction = manager.getTransaction();
            transaction.begin();
            manager.remove(manager.find(Author.class, id));
            transaction.commit();
            return Void.class;
        });
    }

    @Override
    public List<Author> authorsByLastNameLike(String lastName) {
        return execute(entityManager -> {
            TypedQuery<Author> query = entityManager.createQuery(
                    "select a from Author a where a.lastName like :lastName"
                    , Author.class);
            query.setParameter("lastName", lastName + "%");
            return query.getResultList();
        });
    }

    @Override
    public List<Author> findAll() {
        return execute(entityManager ->
                entityManager
                        .createNamedQuery(FIND_ALL, Author.class)
                        .getResultList()
        );
    }
}
