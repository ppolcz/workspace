package polcz.budget.bootfaces.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import polcz.budget.bootfaces.domain.Book;

/**
 *
 * @author Peter Polcz <ppolcz@gmail.com>
 */
public interface BookRepository extends JpaRepository<Book, Long> {
}
