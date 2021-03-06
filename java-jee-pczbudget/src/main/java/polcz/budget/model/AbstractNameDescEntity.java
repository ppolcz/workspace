package polcz.budget.model;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class AbstractNameDescEntity extends AbstractEntity
{
    private static final long serialVersionUID = 6837319138092942594L;

    @Column(name = "description", length = 256)
    protected String desc;

    @Column(unique = true, nullable = false, length = 45)
    protected String name;

    public AbstractNameDescEntity()
    {}

    public AbstractNameDescEntity(String name, String desc)
    {
        this.setName(name);
        this.setDesc(desc);
    }

    @Override
    public String toString()
    {
        return this.getClass().getSimpleName() + "::" + getName();
    }

    public String getDesc()
    {
        return desc;
    }

    public void setDesc(String desc)
    {
        this.desc = desc;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }
}