package polcz.budget.session;

import java.util.List;

import polcz.budget.service.AbstractService;

public interface AbstractEntityBeanHelper<T, S extends AbstractService<T>>
{
    S getService();

    List<T> getList();
}
