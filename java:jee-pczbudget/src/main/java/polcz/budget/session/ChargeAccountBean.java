package polcz.budget.session;

import java.io.Serializable;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import polcz.budget.model.TChargeAccount;
import polcz.budget.service.AbstractService;
import polcz.budget.service.CAService;

@ManagedBean(name = "caBean")
@SessionScoped
public class ChargeAccountBean extends AbstractMapEntityBean<TChargeAccount> implements Serializable
{
    private static final long serialVersionUID = 2286250981768653297L;

    public ChargeAccountBean()
    {
        super(ChargeAccountBean.class);
    }

    @EJB
    private CAService service;

    @Override
    public AbstractService<TChargeAccount> getService()
    {
        return service;
    }

    @Override
    protected TChargeAccount createNewEntity()
    {
        return new TChargeAccount();
    }
}
