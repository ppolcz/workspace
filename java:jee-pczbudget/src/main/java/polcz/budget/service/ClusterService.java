package polcz.budget.service;

import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import polcz.budget.model.TCluster;
import polcz.budget.model.TCluster_;

@Stateless
@RolesAllowed({ "ADMIN", "CUSTOMER" })
public class ClusterService extends AbstractService<TCluster> {

	@PersistenceContext(unitName = "mysqlPU", type = PersistenceContextType.TRANSACTION)
	private EntityManager em;

	@Override
	protected EntityManager em() {
		return em;
	}

	public ClusterService() {
		super(TCluster.class);
	}

	public List<TCluster> findChildren(TCluster parent) {
		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<TCluster> cq = builder.createQuery(TCluster.class);
		Root<TCluster> root = cq.from(TCluster.class);

		/* where statements */
		cq.where(builder.equal(root.get(TCluster_.parent), parent));
		TypedQuery<TCluster> tq = em.createQuery(cq);

		return tq.getResultList();
	}

	// Set<TCluster> children = new HashSet<>();
	//
	// public Set<TCluster> findAllChildren(TCluster parent) {
	// if (parent == null)
	// return null;
	//
	// List<TCluster> children = findChildren(parent);
	// for (TCluster c : children)
	// if (!children.contains(c))
	// children.addAll(findAllChildren(c));
	//
	// return
	// }
}
