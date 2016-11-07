package polcz.budget.jsf.converter;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import org.jboss.logging.Logger;
import org.junit.Assert;

import polcz.budget.global.R;
import polcz.budget.jsf.ChargeAccountBean;
import polcz.budget.jsf.ClusterBean;
import polcz.budget.jsf.MarketBean;
import polcz.budget.model.AbstractNameDescEntity;

@ManagedBean(name = "mecBean")
@RequestScoped
@FacesConverter(value = "meConverter")
public class MapEntityConverter implements Converter
{
    Logger logger;

    @PostConstruct
    private void init()
    {
        logger = R.getJBossLogger(getClass());
    }

    @ManagedProperty(value = "#{mkBean}")
    private MarketBean mkBean;

    @ManagedProperty(value = "#{clBean}")
    private ClusterBean clBean;

    @ManagedProperty(value = "#{caBean}")
    private ChargeAccountBean caBean;

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value)
    {
        // logger.infof("getAsObject: value = %s", value);
        if (value == null) return null;

        String id = component.getId();

        logger.infof("================== id = %s", id);
        
        if (id.startsWith(R.JSF_CL_ID)) return clBean.getItems().get(value);

        if (id.startsWith(R.JSF_MK_ID)) return mkBean.getItems().get(value);

        if (id.startsWith(R.JSF_CA_ID)) return getCaBean().getItems().get(value);

        Assert.assertTrue(String.format("component ID should start with one of the following: "
            + "%s, %s, %s. Passed: '%s'.", R.JSF_CL_ID, R.JSF_MK_ID, R.JSF_CA_ID, id), false);

        return null;
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value)
    {
        // logger.infof("getAsString: value = %s", value);
        if (value == null) return "Not specified";
        // logger.infof("getAsString: value = %s", ((DefaultMappableEntity) value).getValue());
        return ((AbstractNameDescEntity) value).getName();
    }

    public MarketBean getMkBean()
    {
        return mkBean;
    }

    public void setMkBean(MarketBean mkBean)
    {
        this.mkBean = mkBean;
    }

    public ClusterBean getClBean()
    {
        return clBean;
    }

    public void setClBean(ClusterBean clBean)
    {
        this.clBean = clBean;
    }

    public ChargeAccountBean getCaBean()
    {
        return caBean;
    }

    public void setCaBean(ChargeAccountBean caBean)
    {
        this.caBean = caBean;
    }

}
