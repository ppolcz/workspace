package polcz.budget.model;

import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

@Entity
@Table(name = "accounts")
@NamedQueries(value = {
    @NamedQuery(name = "TChargeAccount.findByName", query = "SELECT e FROM TChargeAccount e where e.name = :name"),
    @NamedQuery(name = "TChargeAccount.findAll", query = "SELECT e FROM TChargeAccount e") })
public class TChargeAccount extends AbstractNameDescEntity {
    private static final long serialVersionUID = 1744614676427534063L;

    public TChargeAccount() {}

    public TChargeAccount(String name, String desc) {
        super(name, desc);
    }

    public TChargeAccount(String name) {
        this.setName(name);
    }
}