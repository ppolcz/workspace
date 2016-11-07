package polcz.budget.jsf;

import java.io.Serializable;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;

import polcz.budget.model.Market;
import polcz.budget.service.EntityService;

@ManagedBean(name = "mkBean")
@SessionScoped
public class MarketBean extends AbstractMapEntityBean<Market> implements Serializable
{

    public MarketBean()
    {
        super(MarketBean.class, Market.class);
    }

    private static final long serialVersionUID = 2286250981768653297L;

//    @EJB
    @ManagedProperty(value = "#{entityService}")
    private EntityService service;

    @Override
    public EntityService getService()
    {
        return service;
    }

    @Override
    protected Market createNewEntity()
    {
        return new Market();
    }

    /**
     * @param service the service to set
     */
    public void setService(EntityService service) {
        this.service = service;
    }
}
