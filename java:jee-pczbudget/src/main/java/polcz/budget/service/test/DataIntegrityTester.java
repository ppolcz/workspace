package polcz.budget.service.test;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;
import javax.interceptor.Interceptors;

import org.jboss.logging.Logger;

import polcz.budget.model.TTransaction;
import polcz.budget.service.StartupService;
import polcz.budget.service.TransactionService;
import polcz.util.Util;

@Interceptors({ TestMethodLogger.class })
@RolesAllowed({ "ADMIN", "CUSTOMER" })
@Named("dataIntegrityTester")
@SessionScoped
public class DataIntegrityTester implements Serializable
{
    private static final long serialVersionUID = -8275576532597703796L;

    protected Logger logger;

    protected void log(String format, Object... objs)
    {
        logger.info(String.format(format, objs));
    }

    protected void logTest(String format, Object... objs)
    {
        log(" ======================== TEST: " + format, objs);
    }

    /**
     * Ezeket kulon osztalyba kellene rakni, de mar nem volt idom ra.
     */
    @EJB
    private StartupService ss;

    @EJB
    private TransactionService ts;

    public void testFindElementsBetween()
    {
        Date from = new GregorianCalendar(2015, Calendar.DECEMBER, 8).getTime();
        Date to = new GregorianCalendar(2016, Calendar.FEBRUARY, 20).getTime();

        logTest("testFindElementsBetween: %s, %s, which: %s",
            Util.SIMPLE_DATE_FORMAT.format(from),
            Util.SIMPLE_DATE_FORMAT.format(to), ss.pkez().getName());

        List<TTransaction> list = ts.findElementsBetween(from, to, ss.pkez());

        for (TTransaction e : list)
            log("tr: %s", e);
    }

    public void testFindLastPivotBefore()
    {
        Date date = new GregorianCalendar(2015, Calendar.DECEMBER, 8).getTime();
        logTest("testFindLastPivotBefore: %s, which: %s", Util.SIMPLE_DATE_FORMAT.format(date),
            ss.pkez().getName());

        TTransaction e = ts.findLastPivotBefore(date, ss.pkez());
        log("Pivot found: %s", e);
    }

    public void testFindFirstPivotAfter()
    {
        Date date = new GregorianCalendar(2015, Calendar.DECEMBER, 8).getTime();
        logTest("testFindFirstPivotAfter: %s, which: %s", Util.SIMPLE_DATE_FORMAT.format(date),
            ss.pkez().getName());

        TTransaction e = ts.findFirstPivotAfter(date, ss.pkez());
        log("Pivot found: %s", e);
    }

    public void testFindFirstPivotAfter_WhenPivot()
    {
        Date date = new GregorianCalendar(2015, Calendar.DECEMBER, 8).getTime();
        logTest("testFindFirstPivotAfter_WhenPivot: %s, which: %s", Util.SIMPLE_DATE_FORMAT.format(date),
            ss.pkez().getName());

        TTransaction e = ts.findFirstPivotAfter(date, ss.pkez());
        log("Target pivot element is: %s", e);

        e = ts.findFirstPivotAfter(e);
        log("Pivot found: %s", e);
    }

    public void testFindFirstPivotAfter_WhenNoPivot()
    {
        Date date = new GregorianCalendar(2016, Calendar.APRIL, 22).getTime();
        logTest("testFindFirstPivotAfter_WhenNoPivot: %s, which: %s", Util.SIMPLE_DATE_FORMAT.format(date),
            ss.pkez().getName());

        TTransaction fromPivot = ts.findLastPivotBefore(date, ss.pkez());
        log("Pivot found before: %s", fromPivot);

        TTransaction toPivot = ts.findFirstPivotAfter(date, ss.pkez());
        log("Pivot found after : %s [should be null]", toPivot);

        List<TTransaction> dirtyElements = ts.findElementsBetween(
            fromPivot == null ? date : fromPivot.getDate(),
            toPivot == null ? null : toPivot.getDate(), ss.pkez());

        log("The dirty list is as follows:");
        for (TTransaction t : dirtyElements)
            log("dirty: %s", t);

    }

    public void testTransactionService()
    {
        logger = Logger.getLogger("PPOLCZ_" + DataIntegrityTester.class.getSimpleName());
        logger.info("SESSION BEAN INSTATIATED: " + DataIntegrityTester.class.getSimpleName());

        testFindElementsBetween();
        testFindLastPivotBefore();
        testFindFirstPivotAfter();
        testFindFirstPivotAfter_WhenPivot();
        testFindFirstPivotAfter_WhenNoPivot();
    }

}
