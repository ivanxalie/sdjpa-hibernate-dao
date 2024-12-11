package guru.springframework.jdbc.domain;

import jakarta.persistence.*;
import lombok.*;

import static guru.springframework.jdbc.domain.Author.FIND_ALL;
import static guru.springframework.jdbc.domain.Author.FIND_BY_NAME;

@Setter
@Getter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@NamedQueries({
        @NamedQuery(name = FIND_ALL, query = "from Author"),
        @NamedQuery(name = FIND_BY_NAME, query = "from Author a where a.firstName = :firstName and a.lastName = :lastName")
})
public class Author {
    public static final String FIND_ALL = "selectAuthors";
    public static final String FIND_BY_NAME = "findAuthorByName";
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String firstName;
    private String lastName;
}
