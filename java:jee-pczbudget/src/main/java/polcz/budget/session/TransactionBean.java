package polcz.budget.session;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Random;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.event.AjaxBehaviorEvent;

import org.junit.Assert;
import org.primefaces.component.datatable.DataTable;
import org.primefaces.event.RowEditEvent;

import polcz.budget.global.R;
import polcz.budget.model.TChargeAccount;
import polcz.budget.model.TCluster;
import polcz.budget.model.TMarket;
import polcz.budget.model.TTransaction;
import polcz.budget.model.TTransactionType;
import polcz.budget.service.AbstractService;
import polcz.budget.service.CAService;
import polcz.budget.service.ClusterService;
import polcz.budget.service.MarketService;
import polcz.budget.service.OdfLoaderService;
import polcz.budget.service.StartupService;
import polcz.budget.service.TransactionService;
import polcz.budget.service.helper.TransactionArguments;

/**
 * Ez borzasztoan bonyolultra sikerult, atgondolast igenyel
 */
@ManagedBean
@SessionScoped
public class TransactionBean extends AbstractEntityBean<TTransaction> implements Serializable
{

    private static final long serialVersionUID = -8931274643248777827L;
    // @Getter
    private final int ID = new Random(System.currentTimeMillis()).nextInt();

    /// @formatter:off
	@EJB private StartupService ss;
	@EJB private TransactionService trservice;
	@EJB private CAService caservice;
	@EJB private MarketService mkservice;
	@EJB private ClusterService clservice;
	@EJB private OdfLoaderService odfservice;
	/// @formatter:on

    // @ManagedProperty(value = "#{mecBean}")
    // private MapEntityConverter mecBean;

    /* type of modification: insertion, update or removal */
    // private TTransactionType type;

    /*
     * the older transaction, which is going to be updated [only if type ==
     * update]
     */
    private TTransaction old;

    /* filter parameters */
    private TChargeAccount filterCa;
    private TChargeAccount filterCatransfer;
    private TMarket filterMarket;

    /*
     * Ha a child klaszterekre is ra akarok kerdezni, akkor elobb megkeresem az
     * osszeg gyereket valamilyen bejarassal, majd az SQL IN parancsnak oda adom
     * a teljes listat.
     */
    private TCluster filterCluster;

    public TransactionBean()
    {
        super(TransactionBean.class);
    }

    @PostConstruct
    public void init()
    {
        super.init();
        // filterCa = ss.pkez();
        Assert.assertNotNull(ss.none());
    }

    public TTransactionType[] getTypes()
    {
        return TTransactionType.values();
    }

    public String create()
    {
        String ret = super.create();

        entity = new TTransaction(ss.pkez(), ss.none(), ss.Napi_Szukseglet(), ss.Market_Not_Applicable());
        entity.setType(TTransactionType.simple);

        logger.infof("trDate = %s", new SimpleDateFormat("yyyy-MM-dd").format(entity.getDate()));
        return ret;
    }

    @Override
    public String edit(TTransaction tr)
    {
        logger.infof("edit transaction: %s", tr);

        entity = tr;

        Assert.assertNotNull("ca is null", entity.getCa());
        Assert.assertNotNull("catransfer is null", entity.getCatransfer());
        Assert.assertNotNull("market is null", entity.getMarket());
        Assert.assertNotNull("cluster is null", entity.getCluster());

        if (entity.isPivot())
        {
            Assert.assertTrue(
                String.format("in pivot case, the catransfer should be 'none'. " + "Expected: %s, got %s",
                    ss.none(), entity.getCatransfer()),
                ss.none().equals(entity.getCatransfer()));

            entity.setType(TTransactionType.pivot);
        }
        else if (entity.getCatransfer().equals(ss.none()))
        {
            entity.setType(TTransactionType.simple);
        }
        else
        {
            entity.setType(TTransactionType.transfer);
        }

        /* here I store the old values of the selected transaction item */
        // TODO: tr.clone()
        old = new TTransaction();
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
    public List<TTransaction> updateList()
    {
        // TODO: egy ido utan ez egy kicsit lassu lesz (ha sok sora lesz)
        list = trservice.findAll(filterCa, filterCatransfer, filterCluster, filterMarket);
        logger.infof(this.getClass().getSimpleName() + "::updateList(), length = " + list.size());
        logger.infof(String.format("ca = %s, mk = %s, cl = %s", filterCa, filterMarket, filterCluster));
        return list;
    }

    private String merge(TransactionArguments args)
    {
        trservice.makeTransaction(args);
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
    public String remove(TTransaction removable)
    {
        logger.infof("WILL BE REMOVED: %s", removable);

        TTransaction tr = trservice.findFirstSimpleTransactionBefore(removable);
        logger.infof("FIRST SIMPLE TRANSACTION BEFORE THE REMOVABLE: %s", tr);

        trservice.makeTransaction(new TransactionArguments(tr, removable, R.TR_REMOVAL));
        return updateView();
    }

    @Override
    public AbstractService<TTransaction> getService()
    {
        return trservice;
    }

    public String loadOdf()
    {
        odfservice.process();
        return updateView();
    }
    
    /* ============================ ROW EDIT EVENTS  ============================= */

    public void onEdit(RowEditEvent event)
    {
        logger.info("onEdit(RowEditEvent)");

        AjaxBehaviorEvent evt = (AjaxBehaviorEvent) event;
        DataTable table = (DataTable) evt.getSource();
        int activeRow = table.getRowIndex();

        logger.infof(list.get(activeRow).getRemark());
        trservice.edit(list.get(activeRow));
    }

    public void onCancel(RowEditEvent event)
    {
        logger.info("onCancel(RowEditEvent)");
    }

    /* ============================ GETTERS / SETTERS  ============================= */

    public TTransaction getOld()
    {
        return old;
    }

    public void setOld(TTransaction old)
    {
        this.old = old;
    }

    public TChargeAccount getFilterCa()
    {
        return filterCa;
    }

    public void setFilterCa(TChargeAccount filterCa)
    {
        this.filterCa = filterCa;
    }

    public TCluster getFilterCluster()
    {
        return filterCluster;
    }

    public void setFilterCluster(TCluster filterCluster)
    {
        this.filterCluster = filterCluster;
    }

    public TMarket getFilterMarket()
    {
        return filterMarket;
    }

    public void setFilterMarket(TMarket filterMarket)
    {
        this.filterMarket = filterMarket;
    }

    // public TTransactionType getType()
    // {
    // return entity.getType();
    // }
    //
    // public void setType(TTransactionType type)
    // {
    // entity.setType(type);
    // }

    public int getID()
    {
        return ID;
    }

    public TChargeAccount getFilterCatransfer()
    {
        return filterCatransfer;
    }

    public void setFilterCatransfer(TChargeAccount filterCatransfer)
    {
        this.filterCatransfer = filterCatransfer;
    }

    @Override
    protected TTransaction createNewEntity()
    {
        return new TTransaction();
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
}
