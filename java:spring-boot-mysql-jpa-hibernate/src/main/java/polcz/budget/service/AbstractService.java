package polcz.budget.service;

import java.util.List;
import java.util.logging.Logger;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

public abstract class AbstractService<T>
{
    private Class<T> entityClass;
    protected Logger logger;

    public AbstractService(Class<T> entityClass)
    {
        this.entityClass = entityClass;
        logger = Logger.getLogger("PPOLCZ_" + entityClass.getSimpleName());
    }

    protected abstract EntityManager em();

    /**
     * Save the entity in the database.
     */
    public void create(T entity)
    {
        em().persist(entity);
    }


    /**
     * Update the passed entity in the database.
     */
    public void update(T entity)
    {
        em().merge(entity);
    }

    /**
     * Delete the entity from the database.
     */
    public void remove(T entity)
    {
        if (em().contains(entity))
        {
            em().remove(entity);
        }
        else
        {
            em().remove(em().merge(entity));
        }

        // ez volt regen:
        // em().remove(em().merge(entity));
    }

    /**
     * Find entity by ID.
     */
    public T find(Object id)
    {
        return em().find(entityClass, id);
    }

    /**
     * Return all entities of the table.
     */
    public List<T> findAll()
    {
        CriteriaQuery<T> cq = em().getCriteriaBuilder().createQuery(entityClass);
        cq.select(cq.from(entityClass));
        return em().createQuery(cq).getResultList();
    }

    /**
     * Return entities from range[0] to range[1].
     */
    public List<T> findRange(int[] range)
    {
        CriteriaQuery<T> cq = em().getCriteriaBuilder().createQuery(entityClass);
        cq.select(cq.from(entityClass));
        TypedQuery<T> q = em().createQuery(cq);
        q.setMaxResults(range[1] - range[0]);
        q.setFirstResult(range[0]);
        return q.getResultList();
    }

    /**
     * Count the number of entities of the table. 
     */
    public int count()
    {
        CriteriaQuery<Long> cq = em().getCriteriaBuilder().createQuery(Long.class);
        Root<T> rt = cq.from(entityClass);
        cq.select(em().getCriteriaBuilder().count(rt));
        TypedQuery<Long> q = em().createQuery(cq);
        return ((Long) q.getSingleResult()).intValue();
    }
}