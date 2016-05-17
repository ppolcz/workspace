package polcz.budget.model;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

/**
 * The persistent class for the t_product_infos database table.
 */
@Entity
@Table(name = "products")
@NamedQueries(value = {
    @NamedQuery(name = "ProductInfo.findOne", query = "SELECT e FROM ProductInfo e where e.uid = :uid"),
    @NamedQuery(name = "ProductInfo.findAll", query = "SELECT e FROM ProductInfo e") })
public class ProductInfo extends AbstractEntity implements Serializable
{
    private static final long serialVersionUID = 1L;

    @Column(nullable = false)
    private int amount = 0;

    @Column(name = "amount_orig")
    private int amountOrig = 0;

    @Column(name = "description", nullable = false, length = 300)
    private String desc;

    @ManyToOne(cascade = { CascadeType.ALL })
    @JoinColumn(name = "transaction", nullable = false)
    private Ugylet transaction;

    public ProductInfo()
    {}

    public ProductInfo(Ugylet tr)
    {
        amount = tr.getAmount();
        amountOrig = 0;
        transaction = tr;
        desc = tr.getRemark();
    }

    public int getAmount()
    {
        return this.amount;
    }

    public void setAmount(int amount)
    {
        this.amount = amount;
    }

    public int getAmountOrig()
    {
        return this.amountOrig;
    }

    public void setAmountOrig(int amountOrig)
    {
        this.amountOrig = amountOrig;
    }

    public String getDesc()
    {
        return this.desc;
    }

    public void setDesc(String desc)
    {
        this.desc = desc;
    }

    public Ugylet getUgylet()
    {
        return this.transaction;
    }

    public void setUgylet(Ugylet ugylet)
    {
        this.transaction = ugylet;
    }
}