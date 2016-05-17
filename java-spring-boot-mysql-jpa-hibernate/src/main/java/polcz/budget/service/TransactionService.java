package polcz.budget.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;

import org.jboss.logging.Logger;
import org.junit.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import polcz.budget.global.R;
import polcz.budget.model.ChargeAccount;
import polcz.budget.model.Cluster;
import polcz.budget.model.Market;
import polcz.budget.model.ProductInfo;
import polcz.budget.model.Ugylet;
import polcz.budget.model.UgyletType;
import polcz.budget.model.Ugylet_;
import polcz.budget.service.helper.TransactionArguments;
import polcz.budget.service.helper.TransactionUpdater;
import polcz.util.Util;

@Service
public class TransactionService extends AbstractService<Ugylet> {

    public TransactionService() {
        super(Ugylet.class);
    }

    @PersistenceContext
    private EntityManager em;

    @Autowired
    EntityService service;

    @Autowired
    StartupService ss;

    private static class SelectByArguments {
        ChargeAccount ca;
        boolean considerJustSourceCa;
        ChargeAccount cat;
        Cluster cl;
        Market mk;
        Date minDate;
        Date maxDate;
        boolean pivot;
        boolean asc;
        boolean firstResult;
        // int limit; // TODO: backend-side pagination

        public SelectByArguments(
                ChargeAccount ca, boolean considerJustSourceCa, ChargeAccount cat, Cluster cl, Market mk,
                Date minDate, Date maxDate,
                boolean pivot, boolean asc, boolean firstResult) {
            this.ca = ca;
            this.considerJustSourceCa = considerJustSourceCa;
            this.cat = cat;
            this.cl = cl;
            this.mk = mk;
            this.minDate = minDate;
            this.maxDate = maxDate;
            this.pivot = pivot;
            this.asc = asc;
            this.firstResult = firstResult;
        }
    }

    private void executeTransaction(TransactionArguments args) {
        args.validate();
        new TransactionUpdater(this, Logger.getLogger("PPOLCZ_" + TransactionUpdater.class), args,
                ss.Athelyezes(), ss.none()).execute();

        if (args.getActtr().isProductInfo()) {
            ProductInfo pi = new ProductInfo(args.getActtr());
            service.update(pi);
            // TODO: cascade instead of this fake
        }
    }

    // @Resource
    // private UserTransaction utx;

    private boolean makeRollback() {
        throw new NullPointerException("makeRollback: not implemented yet");
        // try
        // {
        // utx.rollback();
        // }
        // catch (IllegalStateException | SecurityException | SystemException e)
        // {
        // logger.fatal("PPOLCZ: Rollback failed!");
        // e.printStackTrace();
        // }
    }

    @Transactional
    public boolean makeTransaction(TransactionArguments args) {
        Ugylet entity = args.getActtr();
        validate(entity, args.getType());
        logger.infof("merge or persist: %s ", entity);

        switch (args.getType()) {
            case R.TR_INSERTION:
                service.update(args.getActtr());
                break;

            case R.TR_UPDATE:
                edit(args.getActtr());
                break;

            case R.TR_REMOVAL:
                Ugylet tr = args.getOldtr();
                args.setOldtr(null);

                // if (tr.getProductInfos() != null)
                // {
                // /* TODO: cascade */
                // for (TProductInfo pi : tr.getProductInfos())
                // pis.remove(pi);
                // tr.setProductInfos(null);
                // }
                service.remove(tr);
                break;

            default:
                logger.fatal("::makeTransaction(args) - no such type of operation "
                        + "[only update, insertion, removal]");
                // makeRollback();
        }

        if (args.getActtr() != null) executeTransaction(args);
        return true;
    }

    @Transactional
    public void edit(Ugylet tr) {
        service.update(tr);
    }

    private TypedQuery<Ugylet> find(SelectByArguments args) {
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<Ugylet> cq = builder.createQuery(Ugylet.class);
        Root<Ugylet> root = cq.from(Ugylet.class);

        List<Predicate> preds = new ArrayList<>();

        /* build up the predicates */

        if (args.ca != null) {
            if (args.considerJustSourceCa) preds.add(builder.equal(root.get(Ugylet_.ca), args.ca));

            else preds.add(builder.or(
                    builder.equal(root.get(Ugylet_.ca), args.ca),
                    builder.equal(root.get(Ugylet_.catransfer), args.ca)));
        }

        if (args.cat != null) preds.add(builder.equal(root.get(Ugylet_.catransfer), args.cat));

        if (args.cl != null) preds.add(builder.equal(root.get(Ugylet_.cluster), args.cl));

        if (args.mk != null) preds.add(builder.equal(root.get(Ugylet_.market), args.mk));

        if (args.pivot) preds.add(builder.isTrue(root.get(Ugylet_.pivot)));

        if (args.minDate != null)
            preds.add(builder.greaterThanOrEqualTo(root.get(Ugylet_.date), args.minDate));

        if (args.maxDate != null)
            preds.add(builder.lessThanOrEqualTo(root.get(Ugylet_.date), args.maxDate));

        /* convert the predicates into a simple array */
        Predicate[] predsArray = new Predicate[preds.size()];
        preds.toArray(predsArray);

        /* where statements */
        cq.where(predsArray);

        /* order by statements */
        if (args.asc) {
            cq.orderBy(
                    builder.asc(root.get(Ugylet_.date)),
                    builder.asc(root.get(Ugylet_.pivot)),
                    builder.asc(root.get(Ugylet_.uid)));
        } else {
            cq.orderBy(
                    builder.desc(root.get(Ugylet_.date)),
                    builder.desc(root.get(Ugylet_.pivot)),
                    builder.desc(root.get(Ugylet_.uid)));
        }

        TypedQuery<Ugylet> tq = em.createQuery(cq);

        if (args.firstResult) tq.setMaxResults(1).setFirstResult(0);

        return tq;
    }

    private <T> T findSingleOrNull(TypedQuery<T> tq) {
        try {
            return tq.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    /**
     * Query find all filtering by:
     * 
     * @param filterCa
     * @param filterCluster
     * @param filterMarket
     * @return
     */
    public List<Ugylet> findAll(ChargeAccount filterCa, ChargeAccount filterCatransfer,
            Cluster filterCluster, Market filterMarket) {
        return find(
                new SelectByArguments(filterCa, false, filterCatransfer, filterCluster, filterMarket,
                        null, null, /* no temporal limitation */
                        false, true, false)).getResultList();
    }

    /**
     * Find last pivot STRICTLY before date.
     * 
     * @param date
     * @param ca
     * @return
     */
    public Ugylet findLastPivotBefore(Date date, ChargeAccount ca) {
        return findSingleOrNull(
                find(new SelectByArguments(
                        ca, true /* test only source ca [from:ca] */, null, null, null,
                        null, Util.dayBefore(date) /* strictly less then */,
                        true /* pivot */, false /* DESC */, true /* single result */)));
    }

    /**
     * Find first pivot NOT strictly after date.
     * 
     * @param date
     * @param ca
     * @return
     */
    public Ugylet findFirstPivotAfter(Date date, ChargeAccount ca) {
        /**
         * [1]: in case of a pivot element the inequality between tr.date and minDate should be strict, in case of a
         * simple (non-pivot) element, it is enough to use only grater or equal. <br>
         * Heuristics: use strict inequality in every case
         */

        return findSingleOrNull(
                find(new SelectByArguments(
                        ca, true /* only source ca */, null, null, null,
                        Util.dayAfter(date) /* [1] */, null,
                        true /* pivot */, true /* ASC */, true /* single result */)));
    }

    /**
     * Find elements between first and last related to the charge account ca.
     * 
     * @param first
     * @param last
     * @param ca
     * @return
     */
    public List<Ugylet> findElementsBetween(Date first, Date last, ChargeAccount ca) {
        return find(
                new SelectByArguments(
                        ca, false /* not only source ca but beneficiary ca as well */, null, null, null,
                        first, last,
                        false /* not only pivot */, true /* ASC */, false /* multiple res */)).getResultList();
    }

    /**
     * The name speaks for itself.
     * 
     * @param tr
     * @return
     */
    public Ugylet findFirstSimpleTransactionBefore(Ugylet tr) {
        return findSingleOrNull(find(
                new SelectByArguments(
                        tr.getCa(), true, null, null, null,
                        null, Util.dayBefore(tr.getDate()),
                        false /* not only pivot */, true /* ASC */, true /* single result */)));
    }

    public Ugylet findLastPivotBefore(Ugylet tr) {
        return findLastPivotBefore(tr.getDate(), tr.getCa());
    }

    public Ugylet findFirstPivotAfter(Ugylet tr) {
        return findFirstPivotAfter(tr.getDate(), tr.getCa());
    }

    /**
     * Supply additional (necessary) data to the transaction instance, which are not given by the UI.
     * 
     * @param dbTransactionType
     */
    public Ugylet validate(Ugylet tr, int dbTransactionType) {
        /* this is processed after the JSF validation was done */

        /**
         * TODO TODO: ezt egy kicsit szepiteni kellene!!!!!! TODO
         */

        String msg = this.getClass().getSimpleName() + "::validate(Ugylet tr) ";

        /*
         * In case when the first row is going to be erased,
         * the 'acttr' == first simple row before the removable, the can be NULL
         */
        if (tr == null) {
            Assert.assertEquals("actual tr. cannot be null, unless it was a removal of the very "
                    + "first simple tr. or another tr. before it", dbTransactionType, R.TR_REMOVAL);
            return null;
        }

        /* Check transaction type field */
        boolean needTypePrediction = false;
        UgyletType type = tr.getType();
        if (type == null) {
            logger.warn(msg + "tr.type == null [transaction type will be predicted using the given data]");
            needTypePrediction = true;
        } else logger.infof(msg + "transaction type: " + type.getName());

        /* Check source charge account */
        Assert.assertNotNull(tr.getCa());

        /* Check destination charge account */
        if (tr.getCatransfer() == null) {
            Assert.assertNotEquals(
                    "In transfer mode the catransfer shouldn't be null, check the JSF validator bean (this error should be checked there)",
                    type, UgyletType.transfer);
            logger.warn(msg + "destination charge account is unassigned (null), assumed to be 'none'");
            tr.setCatransfer(ss.none());
        }

        if (type == UgyletType.transfer || !tr.getCatransfer().equals(ss.none())) {
            Assert.assertFalse("In transfer mode the catransfer shouldn't be 'none'",
                    tr.getCatransfer().equals(ss.none()));
            Assert.assertTrue("Type should be: transfer, got: " + type.getName(),
                    type == null || type == UgyletType.transfer);
            Assert.assertFalse(tr.isPivot());

            /* auxiliary settings */
            type = UgyletType.transfer;
            tr.setPivot(false);
            tr.setBalance(0);
            tr.setCluster(ss.Athelyezes());
            tr.setMarket(ss.Market_Not_Applicable());
        } else if (type == UgyletType.pivot || tr.isPivot() || tr.getCluster().equals(ss.Szamolas())) {
            /* These must be satisfied */
            Assert.assertEquals(tr.getCatransfer(), ss.none());
            Assert.assertTrue(type == null || type == UgyletType.pivot);

            /* auxiliary settings */
            type = UgyletType.pivot;
            tr.setPivot(true);
            tr.setAmount(0);
            tr.setCluster(ss.Szamolas());
            tr.setMarket(ss.Market_Not_Applicable());
        } else if (type == UgyletType.simple) {
            Assert.assertEquals("The destination charge account should be none in a simple transaction",
                    tr.getCatransfer(), ss.none());

            tr.setPivot(false);
            tr.setBalance(0);
        }

        if (needTypePrediction) {
            logger.warn("predicted transaction type: " + type.getName());
            tr.setType(type);
        }

        return tr;
    }

}
