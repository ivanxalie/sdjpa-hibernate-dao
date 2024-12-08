package guru.springframework.jdbc.dao;

import guru.springframework.jdbc.domain.Author;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.hibernate.query.criteria.internal.CriteriaBuilderImpl;
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
        return null;
    }

    @Override
    public Author saveNewAuthor(Author author) {
        return null;
    }

    @Override
    public Author updateAuthor(Author author) {
        return null;
    }

    @Override
    public void deleteAuthorById(Long id) {

    }

    @Lookup(ENTITY_MANAGER_NAME)
    protected EntityManager manager() {
        return null;
    }
}
