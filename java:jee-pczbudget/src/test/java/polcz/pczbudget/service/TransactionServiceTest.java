package polcz.pczbudget.service;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import polcz.pczbudget.category.TransactionManagementTests;

/**
 * Ez igy nem fog menni:
 * http://deltaspike.apache.org/documentation/test-control.html, azonban ez CDI
 * implementacio specifikus. Ha lesz idom, akkor bemutato idopontjaig ezt is
 * megprobalom csinalni, de nem tudom megigerni.
 * @author Polcz Peter
 */
//@ManagedBean
//@SessionScoped
public class TransactionServiceTest
{
    /// @formatter:off
//  @EJB TransactionService ts;
//  @EJB StartupService ss;
//  @EJB CAService cs;
//  @EJB MarketService ms;
    /// @formatter:on    

    @BeforeClass
    public static void setUpBeforeClass() throws Exception
    {
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception
    {
    }

    @Before
    public void setUp() throws Exception
    {
    }

    @After
    public void tearDown() throws Exception
    {
    }

    @Test
    @Category(
    { TransactionManagementTests.class })
    public void testBeforeAll()
    {
        // Assert.assertNotNull(ts);
        //
        // List<TTransaction> all = ts.findAll();
        // for (TTransaction t : all)
        // ts.remove(t);
    }

    @Test
    @Category(
    { TransactionManagementTests.class })
    public void testFindAllTChargeAccountTClusterTMarket()
    {
        // fail("Not yet implemented");
    }

    @Test
    @Category(
    { TransactionManagementTests.class })
    public void testFindLastPivotBeforeDateTChargeAccount()
    {
        // fail("Not yet implemented");
    }

    @Test
    @Category(
    { TransactionManagementTests.class })
    public void testFindFirstPivotAfterDateTChargeAccount()
    {
        // fail("Not yet implemented");
    }

    @Test
    @Category(
    { TransactionManagementTests.class })
    public void testFindElementsBetween()
    {
        // fail("Not yet implemented");
    }

    @Test
    @Category(
    { TransactionManagementTests.class })
    public void testFindFirstSimpleTransactionBefore()
    {
        // fail("Not yet implemented");
    }

}
