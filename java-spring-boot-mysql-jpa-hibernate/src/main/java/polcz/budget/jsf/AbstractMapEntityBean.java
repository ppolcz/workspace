package polcz.budget.jsf;

import java.io.Serializable;
import java.util.Map;
import java.util.TreeMap;

import polcz.budget.model.AbstractNameDescEntity;

public abstract class AbstractMapEntityBean<T extends AbstractNameDescEntity> extends AbstractEntityBean<T>
    implements Serializable
{
    private static final long serialVersionUID = 1123123L;

    private Map<String, T> items = new TreeMap<>();

    public AbstractMapEntityBean(Class<?> childClass, Class<T> entityClass)
    {
        super(childClass, entityClass);
    }

    public Map<String, T> getItems()
    {
        if (items.size() != getList().size())
        {
            items.clear();
            getList().stream().forEach((o) -> {
                items.put(o.getName(), o);
            });
            logger.info("selectOneMenu items regenerated");
            logger.info("itemsNew.length [after] = " + items.size());
        }
        return items;
    }

}
