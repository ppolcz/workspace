package polcz.budget.model;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 * The persistent class for the t_cluster database table.
 */
@Entity
@Table(name = "clusters")
@NamedQueries(value = {
    @NamedQuery(name = "TCluster.findOne", query = "SELECT e FROM TCluster e where e.uid = :uid"),
    @NamedQuery(name = "TCluster.findByName", query = "SELECT e FROM TCluster e where e.name = :name"),
    @NamedQuery(name = "TCluster.findAll", query = "SELECT e FROM TCluster e") })
public class TCluster extends AbstractNameDescEntity
{
    private static final long serialVersionUID = 9002723472772706262L;

    @Column(nullable = false)
    private int sgn;

    @ManyToOne
    @JoinColumn(name = "parent")
    private TCluster parent;

    @OneToMany(mappedBy = "parent", fetch = FetchType.LAZY)
    private List<TCluster> children;

    @OneToMany(mappedBy = "cluster", fetch = FetchType.LAZY)
    private List<TTransaction> trs;

    public TCluster()
    {}

    public TCluster(String name, int sgn, TCluster parent)
    {
        this.name = name;
        this.sgn = sgn;
        this.parent = parent;
    }

    public TCluster(String name, TCluster parent)
    {
        this.name = name;
        this.sgn = parent.sgn;
        this.parent = parent;
    }

    public TCluster(String name)
    {
        this.name = name;
    }

    public int getSgn()
    {
        return this.sgn;
    }

    public void setSgn(int sgn)
    {
        this.sgn = sgn;
    }

    public TCluster getParent()
    {
        return this.parent;
    }

    public void setParent(TCluster cl)
    {
        this.parent = cl;
    }

    public List<TCluster> getChildren()
    {
        return this.children;
    }

    public void setChildren(List<TCluster> children)
    {
        this.children = children;
    }

    public TCluster addChild(TCluster child)
    {
        getChildren().add(child);
        child.setParent(this);

        return child;
    }

    public TCluster removeChild(TCluster child)
    {
        getChildren().remove(child);
        child.setParent(null);

        return child;
    }

    public List<TTransaction> getTransactions()
    {
        return this.trs;
    }

    public void setTransactions(List<TTransaction> trs)
    {
        this.trs = trs;
    }

    public TTransaction addTransaction(TTransaction tr)
    {
        getTransactions().add(tr);
        tr.setCluster(this);

        return tr;
    }

    public TTransaction removeTransaction(TTransaction tr)
    {
        getTransactions().remove(tr);
        tr.setCluster(null);

        return tr;
    }

    public TCluster rsetUid(int uid)
    {
        setUid(uid);
        return this;
    }
}