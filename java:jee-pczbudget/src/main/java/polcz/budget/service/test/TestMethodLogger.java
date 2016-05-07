package polcz.budget.service.test;

import java.io.Serializable;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;

import polcz.util.MethodLogger;
import polcz.util.annotations.Logged;

@Logged
@Interceptor
public class TestMethodLogger implements MethodLogger, Serializable
{
    private static final long serialVersionUID = 1L;

    public static final Logger LOGGER = Logger.getLogger("PPOLCZ_" + TestMethodLogger.class.getSimpleName());

    public TestMethodLogger()
    {}

    // @AroundInvoke
    public Object logMethodEntry(InvocationContext ic) throws Exception
    {
        LOGGER.log(Level.INFO, "Entering method: {0} in class {1} at Time {2}.", new Object[]
        {
            ic.getMethod().getName(),
            ic.getMethod().getDeclaringClass().getName(),
            new Date()
        });
        return ic.proceed();
    }

    @AroundInvoke
    public Object logMethodInvocation(InvocationContext ic) throws Exception
    {
        LOGGER.log(Level.INFO, "PPOLCZ_ ============================ " +
            ic.getClass().getSimpleName() + "::" + ic.getMethod().getName() +
            " ============================ ");
        // System.out.println("Invoking method: " + ic.getMethod());
        // System.out.println("Parameters: " +
        // Arrays.toString(ic.getParameters()));
        return ic.proceed();
    }
}
