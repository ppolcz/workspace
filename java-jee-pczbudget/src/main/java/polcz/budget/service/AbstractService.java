package polcz.budget.service;

import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.jboss.logging.Logger;

@RolesAllowed({"ADMIN", "CUSTOMER"})
public abstract class AbstractService<T> {
	private Class<T> entityClass;

	public AbstractService(Class<T> entityClass) {
		this.entityClass = entityClass;
		logger = Logger.getLogger("PPOLCZ_" + entityClass.getSimpleName());
	}

	protected abstract EntityManager em();

	public void create(T entity) {
		em().persist(entity);
	}

	public void edit(T entity) {
		em().merge(entity);
	}

    @RolesAllowed({"ADMIN"})
	public void remove(T entity) {
		em().remove(em().merge(entity));
	}

	public T find(Object id) {
		return em().find(entityClass, id);
	}

	public List<T> findAll() {
		CriteriaQuery<T> cq = em().getCriteriaBuilder().createQuery(entityClass);
		cq.select(cq.from(entityClass));
		return em().createQuery(cq).getResultList();
	}

	public List<T> findRange(int[] range) {
		CriteriaQuery<T> cq = em().getCriteriaBuilder().createQuery(entityClass);
		cq.select(cq.from(entityClass));
		TypedQuery<T> q = em().createQuery(cq);
		q.setMaxResults(range[1] - range[0]);
		q.setFirstResult(range[0]);
		return q.getResultList();
	}

	public int count() {
		CriteriaQuery<Long> cq = em().getCriteriaBuilder().createQuery(Long.class);
		Root<T> rt = cq.from(entityClass);
		cq.select(em().getCriteriaBuilder().count(rt));
		TypedQuery<Long> q = em().createQuery(cq);
		return ((Long) q.getSingleResult()).intValue();
	}

	protected Logger logger;
}