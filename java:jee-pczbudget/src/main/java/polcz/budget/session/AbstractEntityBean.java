package polcz.budget.session;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.annotation.PostConstruct;
import javax.faces.event.ActionEvent;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.event.ComponentSystemEvent;
import javax.faces.event.ValueChangeEvent;

import org.jboss.logging.Logger;

import polcz.budget.global.R;
import polcz.budget.model.AbstractEntity;
import polcz.budget.service.AbstractService;

public abstract class AbstractEntityBean<T extends AbstractEntity>
    implements AbstractEntityBeanHelper<T, AbstractService<T>>
{
    private int ID = new Random(System.currentTimeMillis()).nextInt();
    protected Logger logger;

    protected T entity;
    protected List<T> list = new ArrayList<>();

    protected abstract T createNewEntity();

    protected AbstractEntityBean(Class<?> childClass)
    {
        logger = R.getJBossLogger(childClass);
        logger.info("SESSION BEAN INSTATIATED: " + childClass.getSimpleName());
        initBeforeCreate();
    }

    protected void initBeforeCreate()
    {
        entity = createNewEntity();
    }

    public String create()
    {
        initBeforeCreate();
        return R.REDIRECT_CREATE;
    }

    public String edit(T e)
    {
        logger.infof(getClass().getSimpleName() + "::edit: %s", e);
        entity = e;
        return R.REDIRECT_EDIT;
    }

    public String merge()
    {
        getService().edit(entity);
        updateList();
        return R.REDIRECT_VIEW;
    }

    public String remove(T e)
    {
        getService().remove(e);
        updateList();
        return R.REDIRECT_VIEW;
    }

    /* EGY KIS GYORSITAS - NE KERDEZZEN LE FOLOSLEGESEN
     * http://stackoverflow.com/questions/2090033/why-jsf-calls-getters-multiple-times */

    @PostConstruct
    public void init()
    {
        updateList();
        logger.info(AbstractEntityBean.class.getSimpleName() + "::init() @PostConstruct");
    }

    public List<T> updateList()
    {
        list = getService().findAll();
        logger.info(this.getClass().getSimpleName() + "::updateList(), length = " + list.size());
        return list;
    }

    public void preRender(ComponentSystemEvent event)
    {
        // Or in some SystemEvent method (e.g. <f:event type="preRenderView">).
        updateList();
        logger.info(AbstractEntityBean.class.getSimpleName() + "::preRender()");
    }

    public void change(ValueChangeEvent event)
    {
        // Or in some FacesEvent method (e.g. <h:inputXxx valueChangeListener>).
        updateList();
        logger.info(AbstractEntityBean.class.getSimpleName() + "::change()");
    }

    public void ajaxListener(AjaxBehaviorEvent event)
    {
        // Or in some BehaviorEvent method (e.g. <f:ajax listener>).
        updateList();
        logger.info(AbstractEntityBean.class.getSimpleName() + "::ajaxListener()");
    }

    public void actionListener(ActionEvent event)
    {
        // Or in some ActionEvent method (e.g. <h:commandXxx actionListener>).
        updateList();
        logger.info(AbstractEntityBean.class.getSimpleName() + "::actionListener()");
    }

    public String submit()
    {
        // Or in Action method (e.g. <h:commandXxx action>).
        updateList();
        logger.info(AbstractEntityBean.class.getSimpleName() + "::submit()");
        return "outcome";
    }

    public List<T> getList()
    {
        return list;
    }

    public T getEntity()
    {
        return entity;
    }

    public void setEntity(T entity)
    {
        this.entity = entity;
    }

    public int getID()
    {
        return ID;
    }

}
