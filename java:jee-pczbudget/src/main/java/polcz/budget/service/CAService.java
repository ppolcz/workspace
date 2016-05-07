package polcz.budget.service;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;

import polcz.budget.model.TChargeAccount;

@Stateless
@RolesAllowed({"ADMIN", "CUSTOMER"})
public class CAService extends AbstractService<TChargeAccount> {

	@PersistenceContext(unitName = "mysqlPU", type = PersistenceContextType.TRANSACTION)
	private EntityManager em;

	@Override
	protected EntityManager em() {
		return em;
	}

	public CAService() {
		super(TChargeAccount.class);
	}

	@Override
    @RolesAllowed({"ADMIN"})
	public void edit(TChargeAccount entity)
	{
	    super.edit(entity);
	}
	
	@Override
    @RolesAllowed({"ADMIN"})
	public void create(TChargeAccount entity)
	{
	    super.create(entity);
	}
	
	@Override
    @RolesAllowed({"ADMIN"})
	public void remove(TChargeAccount entity)
	{
	    super.remove(entity);
	}
}