package polcz.budget.service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

import org.springframework.stereotype.Repository;

import polcz.budget.model.TChargeAccount;

@Repository
@Transactional
public class CAService extends AbstractService<TChargeAccount> {

	@PersistenceContext
	private EntityManager em;

	@Override
	protected EntityManager em() {
		return em;
	}

	public CAService() {
		super(TChargeAccount.class);
	}
}