package polcz.budget.demo.jsf.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import polcz.budget.demo.jsf.model.Book;

/**
 *
 * @author Peter Polcz <ppolcz@gmail.com>
 */
public interface BookRepository extends JpaRepository<Book, Long> {
}
