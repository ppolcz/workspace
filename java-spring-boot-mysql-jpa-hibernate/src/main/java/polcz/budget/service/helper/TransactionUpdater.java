package polcz.budget.service.helper;

import java.util.Date;
import java.util.List;

import org.jboss.logging.Logger;
import org.junit.Assert;

import polcz.budget.global.R;
import polcz.budget.model.TChargeAccount;
import polcz.budget.model.TCluster;
import polcz.budget.model.TTransaction;
import polcz.budget.service.TransactionService;
import polcz.util.Util;

public class TransactionUpdater {
    private TChargeAccount none;
    private TCluster athelyezes;
    private Logger logger;
    private TransactionService service;
    private TransactionArguments args;

    /**
     * Ezt abstract metodusokkal elegansabban is megcsinalhattam volna, de nekem
     * igy kenyelmesebb.
     */
    public TransactionUpdater(TransactionService service, Logger logger, TransactionArguments args,
            TCluster athelyezes, TChargeAccount none) {
        this.service = service;
        this.args = args;
        this.logger = logger;
        this.athelyezes = athelyezes;
        this.none = none;
    }

    private TransactionUpdater recalculate(List<TTransaction> list) {
        // the first one should not be updated at all (that is a pivot element)

        if (list.isEmpty()) {
            System.out.println("[WARNING] pivot is null, list is empty. " + TransactionUpdater.class.getSimpleName());
            return this;
        }

        // list.isEmpty() == FALSE

        int offset;
        for (offset = 0; offset < list.size() && !list.get(offset).isPivot(); ++offset);

        /* in case when no pivot found: nothing to be done */
        if (offset == list.size()) return this;

        int balance = list.get(offset).getBalance();
        TChargeAccount ca = list.get(offset).getCa();

        info("offset = %d, list.size = %d", offset, list.size());
        for (int i = offset + 1; i < list.size(); ++i) {
            TTransaction t = list.get(i);

            if (t.isPivot()) {
                t.setAmount(t.getBalance() - balance);
                info("IS PIVOT: %s", t);

                /* henceforth use the new balance */
                balance = t.getBalance();
            } else {
                if (t.getCatransfer().equals(ca)) {
                    /**
                     * Transfer transaction (beneficiary side, who gets),
                     * balance: don't modify, because this balance is that of
                     * the other one (who gives)
                     */

                    Assert.assertTrue(!t.getCa().equals(ca) && !t.getCa().equals(none));
                    Assert.assertTrue(
                            String.format("t.cluster = %s, athelyezes = %s, athelyezes.sgn = %d", t.getCluster(), athelyezes, athelyezes.getSgn()),
                            t.getCluster().equals(athelyezes) && athelyezes.getSgn() == -1);

                    // t.setBalance(balance + t.getAmount());
                    balance += t.getAmount();
                } else if (t.getCa().equals(ca)) {
                    /**
                     * Simple transaction, knowing the sign (direction: in/out)
                     * of the amount, which we are talking about
                     */

                    Assert.assertTrue(!t.getCatransfer().equals(ca));

                    t.setBalance(balance + t.getCluster().getSgn() * t.getAmount());
                    balance = t.getBalance();
                }
            }
        }

        return this;
    }

    private List<TTransaction> findDirtyElements(Date from, Date to, TChargeAccount ca) {
        TTransaction fromPivot = service.findLastPivotBefore(from, ca);
        TTransaction toPivot = service.findFirstPivotAfter(to, ca);

        info("from, to PIVOT elements [can be null as well]: ");
        info("%s", fromPivot);
        info("%s", toPivot);

        Assert.assertTrue(fromPivot == null || toPivot == null ||
                fromPivot.getDate().getTime() <= toPivot.getDate().getTime());

        List<TTransaction> dirtyElements = service.findElementsBetween(
                fromPivot == null ? from : fromPivot.getDate(),
                toPivot == null ? null : toPivot.getDate(), ca);

        info("the dirty list is as follows:");
        for (TTransaction t : dirtyElements)
            info("%s", t);

        return dirtyElements;
    }

    private void updateDirtyElements(Date from, Date to, TChargeAccount ca) {
        List<TTransaction> dirtyElements = findDirtyElements(from, to, ca);
        recalculate(dirtyElements);
    }

    public void execute() {
        TTransaction old = args.getOldtr();
        TTransaction tr = args.getActtr();

        Assert.assertNotNull(
                String.format("tr.catransfer = %s [tr.catransfer shouldn't be null], tr = %s", tr.getCatransfer(), tr),
                tr.getCatransfer());
        Assert.assertTrue(
                String.format("tr.ca = %s [tr.ca shouldn't be `none`], tr = %s", tr.getCa(), tr),
                !tr.getCa().equals(none));
        Assert.assertTrue(
                String.format("tr.ca = %s, tr.catransfer = %s, tr = %s", tr.getCa(), tr.getCatransfer(), tr),
                !tr.getCa().equals(tr.getCatransfer()));

        Date fromDate, toDate;
        if (old == null) {
            fromDate = tr.getDate();
            toDate = tr.getDate();
        } else {
            info("Old = " + old);
            info("New = " + tr);

            if (tr.getDate().getTime() < old.getDate().getTime()) {
                fromDate = tr.getDate();
                toDate = old.getDate();
            } else {
                fromDate = old.getDate();
                toDate = tr.getDate();
            }

            info("date from: " + Util.str(fromDate));
            info("date to: " + Util.str(toDate));
        }

        Assert.assertTrue(fromDate.getTime() <= toDate.getTime());

        /* update new from:ca */
        updateDirtyElements(fromDate, toDate, tr.getCa());

        /* update new to:ca */
        if (!tr.getCatransfer().equals(none)) {
            updateDirtyElements(fromDate, toDate, tr.getCatransfer());
        }

        // to be sure, tr.getCa() [new from:ca] & tr.getCatransfer() [new to:ca]
        // are already validated

        if (old == null) return;

        Assert.assertNotNull(old.getCatransfer());
        Assert.assertTrue(!old.getCa().equals(none));
        Assert.assertTrue(!old.getCa().equals(old.getCatransfer()));

        /* update old from:ca */
        if (!old.getCa().equals(tr.getCa()) && !old.getCa().equals(tr.getCatransfer())) {
            updateDirtyElements(fromDate, toDate, old.getCa());
        }

        /* update old to:ca */
        if (!old.getCatransfer().equals(none) &&
                !old.getCatransfer().equals(tr.getCa()) &&
                !old.getCatransfer().equals(tr.getCatransfer())) {
            updateDirtyElements(fromDate, toDate, old.getCatransfer());
        }
    }

    private void info(String format, Object... obj) {
        if (R.LOG_TRANSACTIONAL_LOGIC) logger.infof(format, obj);
    }
}
