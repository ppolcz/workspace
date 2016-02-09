package polcz.util;

import javax.interceptor.AroundInvoke;
import javax.interceptor.InvocationContext;

public interface MethodLogger
{
    @AroundInvoke
    Object logMethodInvocation(InvocationContext ic) throws Exception;
}
