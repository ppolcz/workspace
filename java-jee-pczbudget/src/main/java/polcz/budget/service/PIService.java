package polcz.budget.service;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;

import polcz.budget.model.TProductInfo;

@Stateless
@RolesAllowed({"ADMIN", "CUSTOMER"})
public class PIService extends AbstractService<TProductInfo>
{

    @PersistenceContext(unitName = "mysqlPU", type = PersistenceContextType.TRANSACTION)
    private EntityManager em;

    @Override
    protected EntityManager em() {
        return em;
    }

    public PIService() {
        super(TProductInfo.class);
    }
}
