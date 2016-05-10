package polcz.budget.model;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "t_charge_accounts")
@NamedQueries(value = {
    @NamedQuery(name = "TChargeAccount.findByName", query = "SELECT e FROM TChargeAccount e where e.name = :name"),
    @NamedQuery(name = "TChargeAccount.findAll", query = "SELECT e FROM TChargeAccount e") })
public class TChargeAccount extends AbstractNameDescEntity
{
    private static final long serialVersionUID = 1744614676427534063L;
    
    @OneToMany(mappedBy = "ca", fetch = FetchType.LAZY)
    private List<TTransaction> TTransactions;

    public TChargeAccount()
    {}

    public TChargeAccount(String name, String desc)
    {
        super(name, desc);
    }

    public TChargeAccount(String name)
    {
        this.setName(name);
    }

    public List<TTransaction> getTTransactions()
    {
        return this.TTransactions;
    }

    public void setTTransactions(List<TTransaction> TTransactions)
    {
        this.TTransactions = TTransactions;
    }

    public TTransaction addTTransaction(TTransaction TTransaction)
    {
        getTTransactions().add(TTransaction);
        TTransaction.setCa(this);

        return TTransaction;
    }

    public TTransaction removeTTransaction(TTransaction TTransaction)
    {
        getTTransactions().remove(TTransaction);
        TTransaction.setCa(null);

        return TTransaction;
    }
}