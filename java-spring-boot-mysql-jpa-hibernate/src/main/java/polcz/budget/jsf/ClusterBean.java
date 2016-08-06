package polcz.budget.jsf;

import java.io.Serializable;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;

import polcz.budget.model.Cluster;
import polcz.budget.service.EntityService;

@SessionScoped
@ManagedBean(name = "clBean")
public class ClusterBean extends AbstractMapEntityBean<Cluster> implements Serializable
{

    public ClusterBean()
    {
        super(ClusterBean.class, Cluster.class);
    }

    private static final long serialVersionUID = 8237279798736467461L;

//    @EJB
    @ManagedProperty(value = "entityService")
    private EntityService service;

    @Override
    public EntityService getService()
    {
        return service;
    }

    @Override
    protected Cluster createNewEntity()
    {
        return new Cluster();
    }
}
