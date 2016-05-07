package polcz.util;

import java.io.FileNotFoundException;
import java.io.PrintWriter;

/**
 * @author Polcz Péter <ppolcz@gmail.com>
 * 
 * Created on Oct 13, 2014 7:28:15 PM
 */
public class Debug
{
	static PrintWriter out;

	static
	{
		try
		{
			out = new PrintWriter("opendoctools.log");
		} catch (FileNotFoundException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void closeLog()
	{
		out.close();
	}

	private static final String TAG = "pcz> ";

	public static void log(String format)
	{
		StackTraceElement traces[] = Thread.currentThread().getStackTrace();
		for (StackTraceElement trace : traces)
			out.println(" > " + "in " + trace.getClassName() + "#" + trace.getMethodName() + ":" + trace.getLineNumber());
		out.println(TAG + getCodeLocation(3) + " - " + format);
		// System.out.println(TAG + getCodeLocation(4) + " - " + format);
	}

	public static void debug(String format)
	{
		// out.println(TAG + getCodeLocation(4) + " - " + format);
		System.out.println(TAG + getCodeLocation(4) + " - " + format);
	}

	public static void debug(String format, Throwable ex)
	{
		out.println(TAG + getCodeLocation(4) + " - " + format + "\n\tMessage: " + ex.getMessage());
		System.out.println(TAG + getCodeLocation(4) + " - " + format + "\n\tMessage: " + ex.getMessage());
	}

	public static void debug(Throwable ex)
	{
		System.out.println(TAG + getCodeLocation(4) + " - " + ex.getMessage());
	}

	public static void debug(String format, Object... args)
	{
		out.println(TAG + getCodeLocation(4) + " - " + String.format(format, args));
		System.out.println(TAG + getCodeLocation(4) + " - " + String.format(format, args));
	}

	public static void debug()
	{
		System.out.println(TAG + getCodeLocation(3));
	}

	public static String getCodeLocation(int depth)
	{
		StackTraceElement trace = Thread.currentThread().getStackTrace()[depth];
		return "in " + trace.getClassName() + "#" + trace.getMethodName() + ":" + trace.getLineNumber();
	}
}
