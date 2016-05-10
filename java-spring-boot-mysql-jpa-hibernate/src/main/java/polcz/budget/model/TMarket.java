package polcz.budget.model;

import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
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
public class TMarket extends AbstractNameDescEntity {
    private static final long serialVersionUID = -2987156135972824713L;

    public TMarket() {}

    public TMarket(String name, String desc) {
        super(name, desc);
    }
}