package polcz.budget.service;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;

import polcz.budget.model.TMarket;

@Stateless
@RolesAllowed({ "ADMIN", "CUSTOMER" })
public class MarketService extends AbstractService<TMarket>
{

    @PersistenceContext(unitName = "mysqlPU", type = PersistenceContextType.TRANSACTION)
    private EntityManager em;

    @Override
    protected EntityManager em()
    {
        return em;
    }

    public MarketService()
    {
        super(TMarket.class);
    }

}
