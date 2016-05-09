package polcz.budget.model;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 * The persistent class for the t_markets database table.
 */
@Entity
@Table(name = "markets")
@NamedQueries(value = {
    @NamedQuery(name = "TMarket.findOne", query = "SELECT e FROM TMarket e where e.uid = :uid"),
    @NamedQuery(name = "TMarket.findByName", query = "SELECT e FROM TMarket e where e.name = :name"),
    @NamedQuery(name = "TMarket.findAll", query = "SELECT e FROM TMarket e") })
public class TMarket extends AbstractNameDescEntity
{
    private static final long serialVersionUID = -2987156135972824713L;

    @OneToMany(mappedBy = "market", fetch = FetchType.LAZY)
    private List<TTransaction> trs;

    public TMarket()
    {}

    public TMarket(String name, String desc)
    {
        super(name, desc);
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
        tr.setMarket(this);

        return tr;
    }

    public TTransaction removeTransaction(TTransaction tr)
    {
        getTransactions().remove(tr);
        tr.setMarket(null);

        return tr;
    }
}