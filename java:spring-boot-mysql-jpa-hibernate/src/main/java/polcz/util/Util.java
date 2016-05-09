package polcz.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class Util
{
    public static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH); 
    
    public static String str(Date d)
    {
        return SIMPLE_DATE_FORMAT.format(d);
    }
    
    public static Date dayBefore(Date d)
    {
        Calendar c = Calendar.getInstance();
        c.setTime(d);
        c.add(Calendar.DATE, -1);
        return c.getTime();
    }

    public static Date dayAfter(Date d)
    {
        Calendar c = Calendar.getInstance();
        c.setTime(d);
        c.add(Calendar.DATE, 1);
        return c.getTime();
    }

    public static boolean iff(boolean a, boolean b)
    {
        return (!a || b) && (!b || a);
    }
    
    private Util()
    {
    }
}
