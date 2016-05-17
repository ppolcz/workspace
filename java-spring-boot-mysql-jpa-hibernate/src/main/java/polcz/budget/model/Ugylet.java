package polcz.budget.model;

import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.PostLoad;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.codehaus.jackson.annotate.JsonManagedReference;
import org.junit.Assert;

import polcz.budget.global.R;
import polcz.util.Util;

/**
 * The persistent class for the ugyletek database table.
 * ``Ugylet'' [HUN] = ``transaction''.
 */
@Entity
@Table(name = "ugyletek")
// @NamedQueries(value = {
// @NamedQuery(name = "TTransaction.findOne", query = "SELECT e FROM TTransaction e where e.uid = :uid"),
// @NamedQuery(name = "TTransaction.findAll", query = "SELECT e FROM TTransaction e") })
public class Ugylet extends AbstractEntity {
    private static final long serialVersionUID = -5174036607489515049L;
    private static final String MSG_PREF = AbstractEntity.class.getSimpleName() + ": ";

    @Column(nullable = false)
    private int amount = 0;

    @Column(nullable = false)
    private int balance = 0;

    private int endofdayBalance = 0;

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
    @JsonManagedReference
    @JoinColumn(name = "ca", nullable = false)
    private ChargeAccount ca;

    @ManyToOne
    @JoinColumn(name = "catransfer", nullable = false)
    private ChargeAccount catransfer;

    @ManyToOne
    @JoinColumn(name = "cluster", nullable = false)
    private Cluster cluster;

    @ManyToOne
    @JoinColumn(name = "market")
    private Market market;

    // @OneToOne(cascade = { CascadeType.ALL })
    // @JoinColumn(name = "pi", nullable = true)
    // private TProductInfo pi;

    @OneToMany(mappedBy = "transaction", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductInfo> pis;

    // transient fields:

    transient private UgyletType type;

    transient private boolean productInfo;

    // private boolean isInfo()
    // {
    // return isProductInfo();
    // }

    @PrePersist
    private void settingsBeforePersist() {
        validateBeforeMerge();
        // generateInfo();
    }

    @PreUpdate
    private void settingsBeforeUpdate() {
        validateBeforeMerge();
        // generateInfo();
    }

    @PostLoad
    private void settingsAfterLoad() {

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

        if (pivot) type = UgyletType.pivot;
        else if (clIsTransfer) type = UgyletType.transfer;
        else type = UgyletType.simple;
        // TODO TTransactionType.exchange

    }

    private void validateBeforeMerge() {
        /* If this is also a product info then it's type should be simple */
        Assert.assertTrue(!isProductInfo() || type == UgyletType.simple);
    }

    @SuppressWarnings("unused")
    private void generateInfo() {
        // if (isProductInfo()) pi = new TProductInfo(this);
        // else pi = null;
    }

    public Ugylet() {}

    public Ugylet(ChargeAccount from, ChargeAccount to, Cluster cl, Market mk) {
        ca = from;
        cluster = cl;
        market = mk;
        catransfer = to;
    }

    public Ugylet(int amount, int balance, Date date, String remark, ChargeAccount ca,
            Cluster cluster, Market market, boolean pivot) {
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
    public String toString() {
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

    public int getAmount() {
        return this.amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public int getBalance() {
        return this.balance;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }

    public Date getDate() {
        return this.date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getRemark() {
        return this.remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public List<ProductInfo> getProductInfos() {
        return this.pis;
    }

    public void setProductInfos(List<ProductInfo> pi) {
        this.pis = pi;
    }

    public ProductInfo addProductInfo(ProductInfo pi) {
        getProductInfos().add(pi);
        pi.setUgylet(this);

        return pi;
    }

    public ProductInfo removeProductInfo(ProductInfo pi) {
        getProductInfos().remove(pi);
        pi.setUgylet(null);

        return pi;
    }

    public ChargeAccount getCa() {
        return this.ca;
    }

    public void setCa(ChargeAccount ca) {
        this.ca = ca;
    }

    public Cluster getCluster() {
        return this.cluster;
    }

    public void setCluster(Cluster cluster) {
        this.cluster = cluster;
    }

    public Market getMarket() {
        return this.market;
    }

    public void setMarket(Market market) {
        this.market = market;
    }

    public boolean isPivot() {
        return pivot;
    }

    public void setPivot(boolean pivot) {
        this.pivot = pivot;
    }

    public ChargeAccount getCatransfer() {
        return catransfer;
    }

    public void setCatransfer(ChargeAccount catransfer) {
        this.catransfer = catransfer;
    }

    public UgyletType getType() {
        return type;
    }

    public void setType(UgyletType type) {
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

    public boolean isProductInfo() {
        return productInfo;
    }

    public void setProductInfo(boolean pi) {
        this.productInfo = pi;
    }

    public int getEndofdayBalance() {
        return endofdayBalance;
    }

    public void setEndofdayBalance(int andofdayBalance) {
        this.endofdayBalance = andofdayBalance;
    }
}