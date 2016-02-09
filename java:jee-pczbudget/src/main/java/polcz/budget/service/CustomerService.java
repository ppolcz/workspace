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

import polcz.budget.interfaces.CustomerServiceLocal;
import polcz.budget.model.Customer;

@Stateless
@RolesAllowed({"ADMIN"})
public class CustomerService implements CustomerServiceLocal {

//	@PersistenceContext(unitName="laborPU", type=PersistenceContextType.TRANSACTION)
	@PersistenceContext(unitName = "mysqlPU", type = PersistenceContextType.TRANSACTION)
	private EntityManager entityManager;

	@Override
	public Customer addCustomer(String username, String lastname, String firstname) {
		Customer customer = new Customer(username, lastname, firstname);
		return entityManager.merge(customer);
	}

	@Override
	public void addCustomer(String userName, String lastName, String firstName, String password, String role) {
		Customer customer = new Customer(userName, lastName, firstName);
		customer.setPassword(password);
		customer.setRole(role);
		entityManager.merge(customer);
	}

	@Override
	public Customer getCustomer(String username) {
		TypedQuery<Customer> customerQuery = entityManager.createNamedQuery("findCustomerByName", Customer.class);
		customerQuery.setParameter("username", username);
		return customerQuery.getResultList().get(0);
	}

	@Override
	public void updateCustomer(Integer id, String userName, String lastName, String firstName, String password,
			String role) {
		Customer customer = new Customer(userName, lastName, firstName);
		customer.setId(id);
		customer.setPassword(password);
		customer.setRole(role);
		entityManager.merge(customer);
	}

	@Override
	public List<Customer> getCustomers() {
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<Customer> cq = cb.createQuery(Customer.class);
		Root<Customer> customer = cq.from(Customer.class);
		cq.select(customer);
		TypedQuery<Customer> query = entityManager.createQuery(cq);
		return query.getResultList();
	}

}
