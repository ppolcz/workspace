package polcz.budget.session;

import java.io.Serializable;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import polcz.budget.model.TCluster;
import polcz.budget.service.AbstractService;
import polcz.budget.service.ClusterService;

@SessionScoped
@ManagedBean(name = "clBean")
public class ClusterBean extends AbstractMapEntityBean<TCluster> implements Serializable
{

    public ClusterBean()
    {
        super(ClusterBean.class);
    }

    private static final long serialVersionUID = 8237279798736467461L;

    @EJB
    private ClusterService clservice;

    @Override
    public AbstractService<TCluster> getService()
    {
        return clservice;
    }

    @Override
    protected TCluster createNewEntity()
    {
        return new TCluster();
    }
}
