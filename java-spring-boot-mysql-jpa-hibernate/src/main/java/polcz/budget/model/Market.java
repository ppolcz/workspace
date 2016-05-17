package polcz.budget.model;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * The persistent class for the t_markets database table.
 */
@Entity
@Table(name = "markets")
// @NamedQueries(value = {
// @NamedQuery(name = "Market.findOne", query = "SELECT e FROM Market e where e.uid = :uid"),
// @NamedQuery(name = "Market.findByName", query = "SELECT e FROM Market e where e.name = :name"),
// @NamedQuery(name = "Market.findAll", query = "SELECT e FROM Market e") })
public class Market extends AbstractNameDescEntity {
    private static final long serialVersionUID = -2987156135972824713L;

    public Market() {}

    public Market(String name) {
        super(name, null);
    }

    public Market(String name, String desc) {
        super(name, desc);
    }
}