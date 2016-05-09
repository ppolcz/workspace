package polcz.budget.service;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;

import org.springframework.stereotype.Repository;

import polcz.budget.model.AbstractNameDescEntity;
import polcz.budget.model.TCluster_;

@Repository
@Transactional
public class EntityService {

    @PersistenceContext
    private EntityManager em;

    public void create(Object entity) {
        em.persist(entity);
    }

    public void update(Object entity) {
        em.merge(entity);
    }

    public void remove(Object entity) {
        if (em.contains(entity)) {
            em.remove(entity);
        } else {
            em.remove(em.merge(entity));
        }
    }

    /**
     * Find entity by ID.
     */
    public <T> T find(Object id, Class<T> entityClass) {
        return em.find(entityClass, id);
    }

    public <T extends AbstractNameDescEntity> T findByName(String name, Class<T> entityClass) {
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<T> cq = builder.createQuery(entityClass);
        Root<T> root = cq.from(entityClass);
        cq.where(builder.like(root.get(TCluster_.name), name));
        cq.select(root);
        TypedQuery<T> q = em.createQuery(cq);
        q.setMaxResults(1);

        try {
            T res = q.getSingleResult();
            System.out.println("Result = " + res);
            return res;
        } catch (NoResultException ex) {
            System.out.println("No result");
            return null;
        }
    }

    /**
     * Return all entities of the table.
     */
    public <T> List<T> findAll(Class<T> entityClass) {
        CriteriaQuery<T> cq = em.getCriteriaBuilder().createQuery(entityClass);
        cq.select(cq.from(entityClass));
        return em.createQuery(cq).getResultList();
    }

    /**
     * Return entities from range[0] to range[1].
     */
    public <T> List<T> findRange(int[] range, Class<T> entityClass) {
        CriteriaQuery<T> cq = em.getCriteriaBuilder().createQuery(entityClass);
        cq.select(cq.from(entityClass));
        TypedQuery<T> q = em.createQuery(cq);
        q.setMaxResults(range[1] - range[0]);
        q.setFirstResult(range[0]);
        return q.getResultList();
    }

    /**
     * Count the number of entities of the table.
     */
    public <T> int count(Class<T> entityClass) {
        CriteriaQuery<Long> cq = em.getCriteriaBuilder().createQuery(Long.class);
        Root<T> rt = cq.from(entityClass);
        cq.select(em.getCriteriaBuilder().count(rt));
        TypedQuery<Long> q = em.createQuery(cq);
        return ((Long) q.getSingleResult()).intValue();
    }
}