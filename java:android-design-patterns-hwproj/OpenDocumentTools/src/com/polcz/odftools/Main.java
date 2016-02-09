package com.polcz.odftools;

import static com.napol.koltsegvetes.db.EColumnNames.CA_ID;
import static com.napol.koltsegvetes.db.EColumnNames.CL_DIRECTION;
import static com.napol.koltsegvetes.db.EColumnNames.CL_NAME;
import static com.napol.koltsegvetes.db.EColumnNames.QR_INTEGER;
import static com.napol.koltsegvetes.db.EColumnNames.TR_AMOUNT;
import static com.napol.koltsegvetes.db.EColumnNames.TR_CAID;
import static com.napol.koltsegvetes.db.EColumnNames.TR_CLNAME;
import static com.napol.koltsegvetes.db.EColumnNames.TR_DATE;
import static com.napol.koltsegvetes.db.EColumnNames.TR_MKNAME;
import static com.napol.koltsegvetes.db.EColumnNames.TR_NEWBALANCE;
import static com.napol.koltsegvetes.db.EColumnNames.TR_REMARK;
import static com.napol.koltsegvetes.db.EColumnNames.TR_REMARK_EXTRA;

import com.napol.koltsegvetes.db.AbstractDataStore;
import com.napol.koltsegvetes.db.AbstractQuery;
import com.napol.koltsegvetes.db.ColumnDecorator;
import com.napol.koltsegvetes.db.EColumnNames;
import com.napol.koltsegvetes.db.IColumn;
import com.napol.koltsegvetes.dbdriver.ISQLiteHelper;
import com.napol.koltsegvetes.util.Debug;
import com.polcz.koltsegvetes.dbdriver.MySQLDriverJDBC;
import com.polcz.odftools.ParseOdf2.ECaids;
import com.polcz.odftools.ParseOdf2.EClusters;

/**
 * @author Polcz Péter <ppolcz@gmail.com>
 * Created on Nov 13, 2014 7:59:53 PM
 */
public class Main
{
	AbstractDataStore db = new AbstractDataStore()
	{
		@Override
		protected ISQLiteHelper getHelperInstance()
		{
			return MySQLDriverJDBC.INSTANCE;
		}

		@Override
		protected boolean isCreated()
		{
			return false;
		}
	};

	public Main()
	{
		System.out.println(EColumnNames.TR_PIVOT.toSqlString(true));

		boolean reset = true;
		db.onOpen();

		final ColumnDecorator oldBalance = new ColumnDecorator(QR_INTEGER)
		{
			@Override
			public String displayName()
			{
				return "Old Balance";
			}

			@Override
			public String sqlname()
			{
				return TR_NEWBALANCE.sqlname();
			}
		};

		{
			IColumn cols[] = { CA_ID };
			for (ECaids caid : ECaids.values())
			{
				db.insert(cols, caid.str);
			}
		}
		
		{
			IColumn cols[] = { CL_NAME, CL_DIRECTION };
			for (EClusters cluster : EClusters.values())
			{
				db.insert(cols, cluster.str, cluster.sign);
			}
		}

		// AbstractQuery query = db.select(TR_DATE, TR_CAID, TR_AMOUNT, TR_NEWBALANCE, TR_CLNAME, TR_REMARK, TR_MKNAME, TR_ACID);
		AbstractQuery query = db.select(TR_DATE, TR_CAID, TR_AMOUNT, TR_NEWBALANCE, TR_CLNAME, TR_REMARK, TR_MKNAME);
		if (query == null || query.isEmpty() || reset)
		{
			db.onDestroy();
			ParseOdf2 parser = new ParseOdf2("/home/polpe/Dropbox/koltsegvetes.ods");
			query = parser.getTranzactions();
			db.insertAll(parser.getMarkets());
			db.insertAll(parser.getAdditionalCaids());
			parser.getAdditionalCaids().print();
			db.insertAll(query);
		}

		// TODO
		// QueryDecorator decorator = new QueryDecorator(query, TR_DATE, TR_CAID, TR_CLNAME, oldBalance, TR_NEWBALANCE, TR_AMOUNT, TR_REMARK, TR_ACID)
		QueryDecorator decorator = new QueryDecorator(query, TR_DATE, TR_CAID, TR_CLNAME, oldBalance, TR_NEWBALANCE, TR_AMOUNT, TR_REMARK)
			.setVisitor(new QueryDecorator.Visitor()
			{
				@Override
				public Object transform(IColumn c, Object[] values)
				{
					try
					{
						if (c == oldBalance)
						{
							Debug.debug();
							return (Integer) getValue(TR_NEWBALANCE, values) - (Integer) getValue(TR_AMOUNT, values);
						}
					}
					catch (Exception e)
					{
						e.printStackTrace();
					}
					return getValue(c, values);
				}
			});

		// new TableView(decorator);
	}

	public static void main(String[] args)
	{
		new Main();
		Debug.closeLog();
		/**
		 * TODO ParseODF-ben, minden enum tipusu legyen enum objektumkent kezelve, es csak a legvegen hasznalni a .name()-et.
		 * A map-ek Stringbol kepezzenek enum tipusba.
		 */
	}
}

/*
        try
        {
            System.out.println("Elkezdtem olvasni a dokumentumot");
            SpreadsheetDocument data = SpreadsheetDocument.loadDocument("/home/polpe/Dropbox/koltsegvetes_demo.ods");

            OdfOfficeStyles styles = data.getDocumentStyles();
            System.out.println(styles.getLength());
            for (OdfNumberDateStyle dstyle : styles.getDateStyles()) {
                System.out.println(dstyle.getFormat());
                System.out.println(dstyle.getStyleDisplayNameAttribute());
                System.out.println(dstyle.getStyleNameAttribute());
                System.out.println(dstyle.getStyleVolatileAttribute());
                System.out.println(dstyle.getTagName());
                System.out.println(dstyle.getTextContent());
                System.out.println(dstyle.getTypeName());
                System.out.println(dstyle.getTypeNamespace());
//                System.out.println(dstyle.getLocalName());
//                System.out.println(dstyle.getNodeName());
//                System.out.println(dstyle.getNodeValue());
            }

            // System.out.println(data.getTableList().get(0).getTableName());
            Table table = data.getTableByName("Sheet4");
            // int count = table.getRowCount();
            // System.out.println(count);
            // int type1Count = 0, type2Count = 0, type3Count = 0;
            // for (int i = 1; i < count; i++)
            // {
            // Row row = table.getRowByIndex(i);
            // // System.out.println(row.getCellCount());
            // for (int j = 0; j < row.getCellCount(); j++)
            // {
            // System.out.println(row.getCellByIndex(j).getStyleName());
            // System.out.println(row.getCellByIndex(j).getCellStyleName());
            // // row.getCellByIndex(j).setFormula(formula);
            // row.getCellByIndex(j).getFrameContainerElement().setReadOnly(true, true);
            // // System.out.println(row.getCellByIndex(j).getFormula());
            // // // System.out.println(row.getCellByIndex(j).getStringValue() + " = " + row.getCellByIndex(j).getDisplayText());
            // }
            // }

            int count = table.getRowCount();
//            table.removeRowsByIndex(30, count);
//            count = table.getRowCount();
            System.out.println(count);
            for (int i = count - 1; i != 0; --i)
            {
                System.out.println(table.getRowByIndex(i).getCellByIndex(0).getStringValue());
                System.out.println(table.getRowByIndex(i).getCellByIndex(0).getStyleName());
            }

            for (int i = 0; i < 100; ++i)
            {
                Row row = table.appendRow();
                row.getCellByIndex(0).setDateTimeValue(Calendar.getInstance());
            }

            data.save("/home/polpe/Dropbox/koltsegvetes_demo2.ods");
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        System.out.println("Minden rendben, lefutottam");
 */
