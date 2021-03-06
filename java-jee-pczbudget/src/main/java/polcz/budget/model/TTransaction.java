package polcz.budget.model;

import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.PostLoad;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.junit.Assert;

import polcz.budget.global.R;
import polcz.util.Util;

/**
 * The persistent class for the t_transactions database table.
 */
@Entity
@Table(name = "t_transactions")
@NamedQueries(value = {
    @NamedQuery(name = "TTransaction.findOne", query = "SELECT e FROM TTransaction e where e.uid = :uid"),
    @NamedQuery(name = "TTransaction.findAll", query = "SELECT e FROM TTransaction e") })
public class TTransaction extends AbstractEntity
{
    private static final long serialVersionUID = -5174036607489515049L;
    private static final String MSG_PREF = AbstractEntity.class.getSimpleName() + ": ";

    @Column(nullable = false)
    private int amount = 0;

    @Column(nullable = false)
    private int balance = 0;

    @Temporal(TemporalType.DATE)
    @Column(nullable = false)
    private Date date = new Date();

    // @Lob
    @Column(length = 255)
    private String remark = "";

    // @Type(type = "org.hibernate.type.NumericBooleanType")
    @Column(name = "pivot", nullable = false, columnDefinition = "TINYINT(1)")
    private boolean pivot = false;

    @ManyToOne
    @JoinColumn(name = "ca", nullable = false)
    private TChargeAccount ca;

    @ManyToOne
    @JoinColumn(name = "catransfer", nullable = false)
    private TChargeAccount catransfer;

    @ManyToOne
    @JoinColumn(name = "cluster", nullable = false)
    private TCluster cluster;

    @ManyToOne
    @JoinColumn(name = "market")
    private TMarket market;

    // @OneToOne(cascade = { CascadeType.ALL })
    // @JoinColumn(name = "pi", nullable = true)
    // private TProductInfo pi;

    @OneToMany(mappedBy = "transaction", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TProductInfo> pis;

    // transient fields:

    transient private TTransactionType type;

    transient private boolean productInfo;

    // private boolean isInfo()
    // {
    // return isProductInfo();
    // }

    @PrePersist
    private void settingsBeforePersist()
    {
        validateBeforeMerge();
        // generateInfo();
    }

    @PreUpdate
    private void settingsBeforeUpdate()
    {
        validateBeforeMerge();
        // generateInfo();
    }

    @PostLoad
    private void settingsAfterLoad()
    {

        /* [1] setup transient : productInfo */

        productInfo = pis != null && pis.size() > 0;

        /* [2] setup transient : type */

        boolean clIsTransfer = cluster.getName().equals(R.CLNAME_TRANSFER);
        boolean catransferIsNone = catransfer.getName().equals(R.CANAME_NONE);

        Assert.assertEquals(MSG_PREF + "pivot iff cluster = " + R.CLNAME_PIVOT,
            pivot, cluster.getName().equals(R.CLNAME_PIVOT));

        Assert.assertTrue(MSG_PREF + "RULE: cl=" + R.CLNAME_TRANSFER + " ==> catransfer!=" + R.CANAME_NONE
            + ";  GOT: " + cluster + ", " + catransfer + ";  clIsTransfer=" + clIsTransfer
            + " catransferIsNone=" + catransferIsNone, !clIsTransfer || !catransferIsNone);

        if (pivot) type = TTransactionType.pivot;
        else if (clIsTransfer) type = TTransactionType.transfer;
        else type = TTransactionType.simple;
        // TODO TTransactionType.exchange

    }

    private void validateBeforeMerge()
    {
        /* If this is also a product info then it's type should be simple */
        Assert.assertTrue(!isProductInfo() || type == TTransactionType.simple);
    }

    @SuppressWarnings("unused")
    private void generateInfo()
    {
        // if (isProductInfo()) pi = new TProductInfo(this);
        // else pi = null;
    }

    public TTransaction()
    {}

    public TTransaction(TChargeAccount from, TChargeAccount to, TCluster cl, TMarket mk)
    {
        ca = from;
        cluster = cl;
        market = mk;
        catransfer = to;
    }

    public TTransaction(int amount, int balance, Date date, String remark, TChargeAccount ca,
        TCluster cluster, TMarket market, boolean pivot)
    {
        this.amount = amount;
        this.balance = balance;
        this.date = date;
        this.remark = remark;
        this.ca = ca;
        this.cluster = cluster;
        this.market = market;
        this.pivot = pivot;
    }

    @Override
    public String toString()
    {
        return String.format(
            "{ %d, %s, %s | %8d, %8d %16.16s (%2d) | from:%s to:%s | mk:%16.16s | remark: %s }", getUid(),
            Util.SIMPLE_DATE_FORMAT.format(date),
            pivot ? "PIVOT" : "     ", amount, balance,
            cluster == null ? null : cluster.getName(),
            cluster == null ? 0 : cluster.getSgn(),
            ca == null ? null : ca.getName(),
            catransfer == null ? null : catransfer.getName(),
            market == null ? null : market.getName(),
            remark == null ? null : remark);
    }

    public int getAmount()
    {
        return this.amount;
    }

    public void setAmount(int amount)
    {
        this.amount = amount;
    }

    public int getBalance()
    {
        return this.balance;
    }

    public void setBalance(int balance)
    {
        this.balance = balance;
    }

    public Date getDate()
    {
        return this.date;
    }

    public void setDate(Date date)
    {
        this.date = date;
    }

    public String getRemark()
    {
        return this.remark;
    }

    public void setRemark(String remark)
    {
        this.remark = remark;
    }

    public List<TProductInfo> getProductInfos()
    {
        return this.pis;
    }

    public void setProductInfos(List<TProductInfo> TProductInfos)
    {
        this.pis = TProductInfos;
    }

    public TProductInfo addProductInfo(TProductInfo TProductInfo)
    {
        getProductInfos().add(TProductInfo);
        TProductInfo.setTTransaction(this);

        return TProductInfo;
    }

    public TProductInfo removeProductInfo(TProductInfo TProductInfo)
    {
        getProductInfos().remove(TProductInfo);
        TProductInfo.setTTransaction(null);

        return TProductInfo;
    }

    public TChargeAccount getCa()
    {
        return this.ca;
    }

    public void setCa(TChargeAccount TChargeAccount)
    {
        this.ca = TChargeAccount;
    }

    public TCluster getCluster()
    {
        return this.cluster;
    }

    public void setCluster(TCluster TCluster)
    {
        this.cluster = TCluster;
    }

    public TMarket getMarket()
    {
        return this.market;
    }

    public void setMarket(TMarket TMarket)
    {
        this.market = TMarket;
    }

    public boolean isPivot()
    {
        return pivot;
    }

    public void setPivot(boolean pivot)
    {
        this.pivot = pivot;
    }

    public TChargeAccount getCatransfer()
    {
        return catransfer;
    }

    public void setCatransfer(TChargeAccount catransfer)
    {
        this.catransfer = catransfer;
    }

    public TTransactionType getType()
    {
        return type;
    }

    public void setType(TTransactionType type)
    {
        this.type = type;
    }

    // public TProductInfo getPi()
    // {
    // return pi;
    // }
    //
    // public void setPi(TProductInfo pi)
    // {
    // this.pi = pi;
    // }

    public boolean isProductInfo()
    {
        return productInfo;
    }

    public void setProductInfo(boolean productInfo)
    {
        this.productInfo = productInfo;
    }
}