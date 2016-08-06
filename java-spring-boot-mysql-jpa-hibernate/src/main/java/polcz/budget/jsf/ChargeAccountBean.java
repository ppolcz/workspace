package polcz.budget.jsf;

import java.io.Serializable;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import polcz.budget.model.ChargeAccount;

import polcz.budget.service.EntityService;

@ManagedBean(name = "caBean")
@SessionScoped
public class ChargeAccountBean extends AbstractMapEntityBean<ChargeAccount> implements Serializable
{
    private static final long serialVersionUID = 2286250981768653297L;

    public ChargeAccountBean()
    {
        super(ChargeAccountBean.class, ChargeAccount.class);
    }

//    @EJB
    @ManagedProperty(value = "entityService")
    private EntityService service;

    @Override
    public EntityService getService()
    {
        return service;
    }

    @Override
    protected ChargeAccount createNewEntity()
    {
        return new ChargeAccount();
    }
}
