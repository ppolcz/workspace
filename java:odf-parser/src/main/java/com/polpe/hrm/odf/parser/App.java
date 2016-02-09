package com.polpe.hrm.odf.parser;

import static com.polpe.hrm.db.EColumnNames.*;
import static com.polpe.hrm.db.EColumnNames.CL_DIRECTION;
import static com.polpe.hrm.db.EColumnNames.CL_NAME;
import static com.polpe.hrm.db.EColumnNames.CL_PARENT;
import static com.polpe.hrm.db.IColumn.ope;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import com.polpe.hrm.db.AbstractDataStore;
import com.polpe.hrm.db.ETableNames;
import com.polpe.hrm.db.IColumn;
import com.polpe.hrm.db.ISQLiteHelper;
import com.polpe.hrm.db.MySQLDriverJDBC;
import com.polpe.hrm.odf.parser.ParseOdf2.ECaids;
import com.polpe.hrm.odf.parser.ParseOdf2.EClusters;

/**
 * Hello world!
 *
 */
public class App
{
	AbstractDataStore db = new AbstractDataStore()
	{
		@Override
		protected ISQLiteHelper getHelperInstance()
		{
			return MySQLDriverJDBC.instance("java", "javapass", "javadrafts_uj");
		}

		@Override
		protected boolean isCreated()
		{
			return false;
		}
	};

	public App()
	{
		int startIndex = 4; // the first row which effectively should be stored
		Date startDate = new GregorianCalendar(2013, Calendar.JULY, 8).getTime();

		int gobackNrDays = 40;

		boolean reset = false;
		db.onOpen();

		if (reset)
		{
			db.onDestroy();
			db.onOpen();
		}
		else
		{
			Calendar calendar = Calendar.getInstance(); // this would default to now
			calendar.add(Calendar.DAY_OF_MONTH, -gobackNrDays);

			startIndex += Math.max(0, (calendar.getTime().getTime() - startDate.getTime()) / (1000 * 60 * 60 * 24));
			startDate = calendar.getTime();

			System.out.println("Starting from row: " + startIndex + ", date: " + new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(startDate));
			System.out.println("This procedure will remove all items, which satisfy: " + TR_DATE.sqlwhere(startDate, opge));
		}

		/* Insert all charge account ID instances */
		{
			IColumn cols[] = { CA_ID };
			for (ECaids caid : ECaids.values())
			{
				db.insert(cols, caid.str);
			}
		}

		/* Insert all cluster instances */
		
		{
			IColumn cols[] = { CL_NAME, CL_DIRECTION, CL_PARENT };
			for (EClusters cluster : EClusters.values())
			{
				String parent = cluster.parent == null ? null : cluster.parent.str;
				db.insert(cols, cluster.str, cluster.sign, parent);
			}
		}

		/* Read ODF document */
		ParseOdf2 parser = new ParseOdf2("/home/polpe/Dropbox/koltsegvetes.ods", startIndex);
		
		db.delete(ETableNames.TRANSACTIONS, TR_DATE.sqlwhere(startDate, opge));
		db.insertAll(parser.getMarkets());
		db.insertAll(parser.getTranzactions());
	}

	
//public void act()
//
//    setLocation (getX() + 4, getX())
//    // Moves the Actor 4 cells to the right
//    setRtation (getRotation() + 2);
//    // Rotates the Actor 2 degrees clockwise
//}
//
//	private void setRotation(int i)
//{
//	// TODO Auto-generated method stub
//	
//}
//
//
//	private void setLocation(int i, int x)
//{
//	// TODO Auto-generated method stub
//	
//}
//
//
//	private int getRotation()
//{
//	// TODO Auto-generated method stub
//	return 0;
//}
//
//
//	private int getX()
//{
//	// TODO Auto-generated method stub
//	return 0;
//}


	public static void main(String[] args)
	{
		new App();
	}
}
