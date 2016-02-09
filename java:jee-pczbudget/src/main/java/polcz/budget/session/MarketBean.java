package polcz.budget.session;

import java.io.Serializable;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import polcz.budget.model.TMarket;
import polcz.budget.service.AbstractService;
import polcz.budget.service.MarketService;

@ManagedBean(name = "mkBean")
@SessionScoped
public class MarketBean extends AbstractMapEntityBean<TMarket> implements Serializable
{

    public MarketBean()
    {
        super(MarketBean.class);
    }

    private static final long serialVersionUID = 2286250981768653297L;

    @EJB
    private MarketService service;

    @Override
    public AbstractService<TMarket> getService()
    {
        return service;
    }

    @Override
    protected TMarket createNewEntity()
    {
        return new TMarket();
    }
}
