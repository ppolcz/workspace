package polcz.budget.service;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;

import org.jboss.logging.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import polcz.budget.model.AbstractEntity;
import polcz.budget.model.AbstractNameDescEntity;
import polcz.budget.model.Cluster_;

@Repository
@Transactional
public class EntityService {

    /**
     * TODO 
     * Demonstration: any project property, which is declared in 
     * application.properties, can be accessed in this way.
     */
    @Value("${polcz.props.demo}")
    private String demoProperty;

    @PersistenceContext
    private EntityManager em;

    private Logger logger;

    @PostConstruct
    private void init() {
        logger = Logger.getLogger("PPOLCZ_" + EntityService.class.getSimpleName());
    }

    public <T extends AbstractEntity> T update(T entity) {
        return em.merge(entity);
    }

    public <T extends AbstractNameDescEntity> T findByNameOrCreate(T entity, Class<T> entityClass) {

        /* find if this name already exists */
        T old = findByName(entity.getName(), entityClass);

        if (old != null) {
            return old;
        }
        return em.merge(entity);
    }

    public <T extends AbstractNameDescEntity> T update(T entity, Class<T> entityClass) {

        /* find if this name already exists */
        logger.infof("find if the name '%s' already exists", entity.getName());
        T old = findByName(entity.getName(), entityClass);

        if (old != null) {
            logger.info("an older entity found with this name, its id: " + old.getUid());
            entity.setUid(old.getUid());
        } else {
            logger.info("an existing entry with this name was not found");
        }

        T ret = null;
        try {
            ret = em.merge(entity);
        } catch (Exception e) {
            logger.info("Exception");
            e.printStackTrace();
        }
        return ret;
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
        logger.info("polcz.budget.service.EntityService.findByName(): demoProperty = " + demoProperty);

        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<T> cq = builder.createQuery(entityClass);
        Root<T> root = cq.from(entityClass);
        cq.where(builder.equal(root.get(Cluster_.name), name));
        cq.select(root);
        TypedQuery<T> q = em.createQuery(cq);
        q.setMaxResults(1);

        try {
            T res = q.getSingleResult();
            return res;
        } catch (NoResultException ex) {
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
