package polcz.budget.jsf;

import java.util.List;

import polcz.budget.service.AbstractService;
import polcz.budget.service.EntityService;

public interface AbstractEntityBeanHelper<T, S extends EntityService>
{
    S getService();

    List<T> getList();
}
