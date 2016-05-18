package polcz.budget.model;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "accounts")
// @NamedQueries(value = {
// @NamedQuery(name = "ChargeAccount.findByName", query = "SELECT e FROM ChargeAccount e where e.name = :name"),
// @NamedQuery(name = "ChargeAccount.findAll", query = "SELECT e FROM ChargeAccount e") })
public class ChargeAccount extends AbstractNameDescEntity {
    private static final long serialVersionUID = 1744614676427534063L;

    public ChargeAccount() {}

    public ChargeAccount(String name, String desc) {
        super(name, desc);
    }

    public ChargeAccount(String name) {
        this.setName(name);
    }
}