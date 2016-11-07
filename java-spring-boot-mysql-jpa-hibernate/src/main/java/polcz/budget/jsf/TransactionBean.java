package polcz.budget.jsf;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Random;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.event.AjaxBehaviorEvent;

import org.junit.Assert;
import org.primefaces.component.datatable.DataTable;
import org.primefaces.event.RowEditEvent;

import polcz.budget.global.R;
import polcz.budget.model.ChargeAccount;
import polcz.budget.model.Cluster;
import polcz.budget.model.Market;
import polcz.budget.model.Ugylet;
import polcz.budget.model.UgyletType;
import polcz.budget.service.EntityService;
import polcz.budget.service.StartupService;
import polcz.budget.service.UgyletService;
import polcz.budget.service.helper.TransactionArguments;

/**
 * Ez borzasztoan bonyolultra sikerult, atgondolast igenyel
 */
@ManagedBean
@SessionScoped
public class TransactionBean extends AbstractEntityBean<Ugylet> implements Serializable
{

    private static final long serialVersionUID = -8931274643248777827L;
    // @Getter
    private final int ID = new Random(System.currentTimeMillis()).nextInt();

    /// @formatter:off
    @ManagedProperty(value = "#{startupService}")
    private StartupService ss;

    @ManagedProperty(value = "#{entityService}")
    private EntityService service;

    @ManagedProperty(value = "#{ugyletService}")
    private UgyletService ugyletService;
	/// @formatter:on

    // @ManagedProperty(value = "#{mecBean}")
    // private MapEntityConverter mecBean;

    /* type of modification: insertion, update or removal */
    // private UgyletType type;

    /*
     * the older transaction, which is going to be updated [only if type ==
     * update]
     */
    private Ugylet old;

    /* filter parameters */
    private ChargeAccount filterCa;
    private ChargeAccount filterCatransfer;
    private Market filterMarket;

    /*
     * Ha a child klaszterekre is ra akarok kerdezni, akkor elobb megkeresem az
     * osszeg gyereket valamilyen bejarassal, majd az SQL IN parancsnak oda adom
     * a teljes listat.
     */
    private Cluster filterCluster;

    public TransactionBean()
    {
        super(TransactionBean.class, Ugylet.class);
    }

    @PostConstruct
    public void init()
    {
        super.init();
        // filterCa = ss.pkez();
        Assert.assertNotNull(getSs().none());
    }

    public UgyletType[] getTypes()
    {
        return UgyletType.values();
    }

    public String create()
    {
        String ret = super.create();

        entity = new Ugylet(getSs().pkez(), getSs().none(), getSs().Napi_Szukseglet(), getSs().Market_Not_Applicable());
        entity.setType(UgyletType.simple);

        logger.infof("trDate = %s", new SimpleDateFormat("yyyy-MM-dd").format(entity.getDate()));
        return ret;
    }

    public String edit(Ugylet tr)
    {
        logger.infof("edit transaction: %s", tr);

        entity = tr;

        Assert.assertNotNull("ca is null", entity.getCa());
        Assert.assertNotNull("catransfer is null", entity.getCatransfer());
        Assert.assertNotNull("market is null", entity.getMarket());
        Assert.assertNotNull("cluster is null", entity.getCluster());

        if (entity.isPivot())
        {
            Assert.assertTrue(String.format("in pivot case, the catransfer should be 'none'. " + "Expected: %s, got %s",
                    getSs().none(), entity.getCatransfer()),
                getSs().none().equals(entity.getCatransfer()));

            entity.setType(UgyletType.pivot);
        }
        else if (entity.getCatransfer().equals(getSs().none()))
        {
            entity.setType(UgyletType.simple);
        }
        else
        {
            entity.setType(UgyletType.transfer);
        }

        /* here I store the old values of the selected transaction item */
        // TODO: tr.clone()
        old = new Ugylet();
        old.setDate(entity.getDate());
        old.setCa(entity.getCa());
        old.setCatransfer(entity.getCatransfer());

        logger.info(entity.toString());

        return R.REDIRECT_EDIT;
    }

    public String updateView()
    {
        updateList();
        return R.REDIRECT_VIEW;
    }

    @Override
    public List<Ugylet> updateList()
    {
        // TODO: egy ido utan ez egy kicsit lassu lesz (ha sok sora lesz)
        list = getUgyletService().findAll(filterCa, filterCatransfer, filterCluster, filterMarket);
        logger.infof(this.getClass().getSimpleName() + "::updateList(), length = " + list.size());
        logger.infof(String.format("ca = %s, mk = %s, cl = %s", filterCa, filterMarket, filterCluster));
        return list;
    }

    private String merge(TransactionArguments args)
    {
        getUgyletService().makeTransaction(args);
        return updateView();
    }

    public String persist()
    {
        return merge(new TransactionArguments(entity, null, R.TR_INSERTION));
    }

    @Override
    public String merge()
    {
        return merge(new TransactionArguments(entity, old, R.TR_UPDATE));
    }

    @Override
    public String remove(Ugylet removable)
    {
        logger.infof("WILL BE REMOVED: %s", removable);

        Ugylet tr = getUgyletService().findFirstSimpleTransactionBefore(removable);
        logger.infof("FIRST SIMPLE TRANSACTION BEFORE THE REMOVABLE: %s", tr);

        getUgyletService().makeTransaction(new TransactionArguments(tr, removable, R.TR_REMOVAL));
        return updateView();
    }

    @Override
    public EntityService getService()
    {
        return service;
    }
    
    /* ============================ ROW EDIT EVENTS  ============================= */

    public void onEdit(RowEditEvent event)
    {
        logger.info("onEdit(RowEditEvent)");

        AjaxBehaviorEvent evt = (AjaxBehaviorEvent) event;
        DataTable table = (DataTable) evt.getSource();
        int activeRow = table.getRowIndex();

        logger.infof(list.get(activeRow).getRemark());
        getUgyletService().edit(list.get(activeRow));
    }

    public void onCancel(RowEditEvent event)
    {
        logger.info("onCancel(RowEditEvent)");
    }

    /* ============================ GETTERS / SETTERS  ============================= */

    public Ugylet getOld()
    {
        return old;
    }

    public void setOld(Ugylet old)
    {
        this.old = old;
    }

    public ChargeAccount getFilterCa()
    {
        return filterCa;
    }

    public void setFilterCa(ChargeAccount filterCa)
    {
        this.filterCa = filterCa;
    }

    public Cluster getFilterCluster()
    {
        return filterCluster;
    }

    public void setFilterCluster(Cluster filterCluster)
    {
        this.filterCluster = filterCluster;
    }

    public Market getFilterMarket()
    {
        return filterMarket;
    }

    public void setFilterMarket(Market filterMarket)
    {
        this.filterMarket = filterMarket;
    }

    // public UgyletType getType()
    // {
    // return entity.getType();
    // }
    //
    // public void setType(UgyletType type)
    // {
    // entity.setType(type);
    // }

    public int getID()
    {
        return ID;
    }

    public ChargeAccount getFilterCatransfer()
    {
        return filterCatransfer;
    }

    public void setFilterCatransfer(ChargeAccount filterCatransfer)
    {
        this.filterCatransfer = filterCatransfer;
    }

    @Override
    protected Ugylet createNewEntity()
    {
        return new Ugylet();
    }

    // public MapEntityConverter getMecBean()
    // {
    // return mecBean;
    // }
    //
    // public void setMecBean(MapEntityConverter mecBean)
    // {
    // this.mecBean = mecBean;
    // }

    /**
     * @return the ss
     */
    public StartupService getSs() {
        return ss;
    }

    /**
     * @param ss the ss to set
     */
    public void setSs(StartupService ss) {
        this.ss = ss;
    }

    /**
     * @param service the service to set
     */
    public void setService(EntityService service) {
        this.service = service;
    }

    /**
     * @return the ugyletService
     */
    public UgyletService getUgyletService() {
        return ugyletService;
    }

    /**
     * @param ugyletService the ugyletService to set
     */
    public void setUgyletService(UgyletService ugyletService) {
        this.ugyletService = ugyletService;
    }
}
