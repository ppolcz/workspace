package polcz.budget.service.helper;

import org.junit.Assert;

import polcz.budget.global.R;
import polcz.budget.model.TTransaction;

public class TransactionArguments
{
    /// @formatter:off
    private TTransaction acttr;
    private TTransaction oldtr;
    private int type;
    /// @formatter:on

    public TransactionArguments(TTransaction acttr, TTransaction oldtr, int type)
    {
        this.acttr = acttr;
        this.oldtr = oldtr;
        this.type = type;
    }
    
    public void validate ()
    {
        if (oldtr != null)
        {
            Assert.assertTrue(type == R.TR_UPDATE);
        }
    }
    
    public TTransaction getActtr()
    {
        return acttr;
    }

    public void setActtr(TTransaction acttr)
    {
        this.acttr = acttr;
    }

    public TTransaction getOldtr()
    {
        return oldtr;
    }

    public void setOldtr(TTransaction oldtr)
    {
        this.oldtr = oldtr;
    }

    public int getType()
    {
        return type;
    }

    public void setType(int type)
    {
        this.type = type;
    }

}
