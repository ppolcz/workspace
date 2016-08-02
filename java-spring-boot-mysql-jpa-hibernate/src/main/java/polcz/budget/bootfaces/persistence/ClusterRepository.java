package polcz.budget.bootfaces.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import polcz.budget.model.Cluster;

/**
 *
 * @author Peter Polcz <ppolcz@gmail.com>
 */
public interface ClusterRepository extends JpaRepository<Cluster, Integer> {
}
