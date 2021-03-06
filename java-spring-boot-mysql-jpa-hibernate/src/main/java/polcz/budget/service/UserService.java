package polcz.budget.service;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

import org.springframework.stereotype.Repository;

import polcz.budget.model.User;

/**
 * This class is used to access data for the User entity. Repository annotation
 * allows the component scanning support to find and configure the DAO wihtout
 * any XML configuration and also provide the Spring exceptiom translation.
 * Since we've setup setPackagesToScan and transaction manager on
 * DatabaseConfig, any bean method annotated with Transactional will cause
 * Spring to magically call begin() and commit() at the start/end of the method.
 * If exception occurs it will also call rollback().
 */
@Repository
@Transactional
public class UserService {

    // An EntityManager will be automatically injected from entityManagerFactory
    // setup on DatabaseConfig class.
    @PersistenceContext
    private EntityManager em;

	/**
	 * Save the user in the database.
	 */
	public void create(User user) {
		em.persist(user);
		System.out.println(em.hashCode());
		return;
	}

	/**
	 * Delete the user from the database.
	 */
	public void delete(User user) {
		if (em.contains(user))
			em.remove(user);
		else
			em.remove(em.merge(user));
		return;
	}

	/**
	 * Return all the users stored in the database.
	 */
	@SuppressWarnings("unchecked")
	public List<User> getAll() {
		return em.createQuery("from User").getResultList();
	}

	/**
	 * Return the user having the passed email.
	 */
	public User getByEmail(String email) {
		return (User) em.createQuery("from User where email = :email").setParameter("email", email)
				.getSingleResult();
	}

	/**
	 * Return the user having the passed id.
	 */
	public User getById(long id) {
		return em.find(User.class, id);
	}

	/**
	 * Update the passed user in the database.
	 */
	public void update(User user) {
		em.merge(user);
		return;
	}

} // class UserDao
