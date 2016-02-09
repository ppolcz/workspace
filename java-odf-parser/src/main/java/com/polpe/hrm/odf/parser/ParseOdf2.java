package com.polpe.hrm.odf.parser;

import static com.polpe.hrm.db.EColumnNames.CA_ID;
import static com.polpe.hrm.db.EColumnNames.CA_NAME;
import static com.polpe.hrm.db.EColumnNames.MK_ID;
import static com.polpe.hrm.db.EColumnNames.TR_AMOUNT;
import static com.polpe.hrm.db.EColumnNames.TR_CAID;
import static com.polpe.hrm.db.EColumnNames.TR_CLNAME;
import static com.polpe.hrm.db.EColumnNames.TR_DATE;
import static com.polpe.hrm.db.EColumnNames.TR_MKNAME;
import static com.polpe.hrm.db.EColumnNames.TR_NEWBALANCE;
import static com.polpe.hrm.db.EColumnNames.TR_PIVOT;
import static com.polpe.hrm.db.EColumnNames.TR_REMARK;
import static com.polpe.hrm.db.EColumnNames.TR_REMARK_EXTRA;

import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.function.BinaryOperator;

import org.odftoolkit.simple.SpreadsheetDocument;
import org.odftoolkit.simple.table.Row;
import org.odftoolkit.simple.table.Table;

import com.polpe.hrm.db.AbstractQuery;
import com.polpe.hrm.db.EColumnNames;
import com.polpe.hrm.db.IColumn;
import com.polpe.hrm.db.QueryBuilder;

/**
 * 
 * @author Polcz Péter <ppolcz@gmail.com>
 * Created on Nov 14, 2014 7:27:54 PM
 */
public class ParseOdf2
{
	enum ECaids
	{
		pkez, potp, dkez, dotp, info(true), pinfo(true), none;

		public final String str = name();
		public final boolean isinfo;

		private ECaids()
		{
			this.isinfo = false;
		}

		private ECaids(boolean info)
		{
			this.isinfo = info;
		}
	}

	public static final String username = "ppolcz";

	public static final int IND_DATE = 0;

	public static final ECaids[] E_CAIDS = { ECaids.potp, ECaids.pkez, ECaids.dotp, ECaids.dkez };
	public static final int IND_CAIDS = 2;

	public static final EColumnNames[] E_TRCOLS = { TR_AMOUNT, TR_CAID, TR_CLNAME, TR_MKNAME, TR_REMARK };
	public static final int NR_TRS = 12;
	public static final int TR_OFFSET = 10;
	public static final int TR_LENGTH = E_TRCOLS.length;

	public static final int[] IND_TRS;// = { 6, 11, 16, 21, 26, 31 };
	static
	{
		IND_TRS = new int[NR_TRS];
		for (int i = 0; i < NR_TRS; ++i)
		{
			IND_TRS[i] = i * TR_LENGTH + TR_OFFSET;
		}
	}

	public static final int c = E_CAIDS.length;
	public static final int n = IND_TRS.length;
	public static final int l = E_TRCOLS.length;

	private AbstractQuery trans;
	private AbstractQuery mks;
	private AbstractQuery addcaids;
	private Map<String, EClusters> clusters;
	private Map<String, ECaids> caids;
	private Set<String> markets;

	private Map<ECaids, Integer> balance;

	private static final Date today = Calendar.getInstance(Locale.getDefault()).getTime();

	// private static final IColumn[] cols = { TR_DATE, TR_CAID, TR_AMOUNT, TR_NEWBALANCE, TR_CLNAME, TR_REMARK, TR_MKNAME, TR_PIVOT };
	private static final IColumn[] colsE = { TR_DATE, TR_CAID, TR_AMOUNT, TR_NEWBALANCE, TR_CLNAME, TR_REMARK, TR_REMARK_EXTRA, TR_MKNAME, TR_PIVOT };
	private static final IColumn[] colsca = { CA_ID, CA_NAME };
	private static final IColumn[] colsmk = { MK_ID };

	enum EClusters
	{
		// @formatter:off
		Egyeb_Kiadas(-1),
			Nem_Adott(Egyeb_Kiadas),
		
			Szukseges(Egyeb_Kiadas),
				Napi_Szukseglet(Szukseges),
					Elelem(Napi_Szukseglet),
				Extra_Szukseglet(Szukseges),
					Ruhazkodas(Extra_Szukseglet),
					Lakas_Berendezes(Extra_Szukseglet),
					Gyogyszer(Extra_Szukseglet),
					Szukseges_Rossz(Szukseges),
						Javitasok(Szukseges_Rossz),
							Rezsi(Szukseges_Rossz),
								Rezsi_Bkv(Rezsi),
								Rezsi_Gaz(Rezsi),
								Rezsi_Viz(Rezsi),
								Rezsi_Futes(Rezsi),
								Rezsi_Elmu(Rezsi),
								Rezsi_Kozosk(Rezsi),
								Rezsi_Upc(Rezsi),
								Rezsi_Otp(Rezsi),
						Alberlet(Szukseges_Rossz),
						Kaucio(Szukseges_Rossz),
						Telefon(Extra_Szukseglet),
							Mobil_Peti(Telefon),
							Mobil_Helga(Telefon),
							Mobil_Dori(Telefon),
						Hivatalos(Szukseges_Rossz),
				Munkaeszkozok(Szukseges),
					Konyv(Munkaeszkozok),
					
			Kellemes(Egyeb_Kiadas),
				Utazas(Kellemes),
				Eskuvo(Kellemes),
				Ajandek(Kellemes),
				Szorakozas(Kellemes),
					Sport(Szorakozas),
					Szorakozas_Bal(Szorakozas),
					Szorakozas_Mozi(Szorakozas),
					Szorakozas_Tanc(Szorakozas),
							
			Luxus(Egyeb_Kiadas),
				Ekszer(Luxus),
			Vatikani(Egyeb_Kiadas),
			Elektr_Cikk(Egyeb_Kiadas),
			Kolcson(Egyeb_Kiadas),
			
		Egyeb_Bevetel(+1),
			Otthon(Egyeb_Bevetel),
			JozsaOtthon(Otthon),
			Fizetes(Egyeb_Bevetel),
			Osztondij(Egyeb_Bevetel),
			Kaucio_Vissza(Egyeb_Bevetel),
			Maganora(Fizetes),
			VisszaVasarlas(Egyeb_Bevetel),

		Szamolas(1),
		Athelyezes_Innen(Egyeb_Kiadas),
		Athelyezes_Ide(Egyeb_Bevetel);
		// @formatter:on

		public final String str = name();
		public final int sign;
		public final EClusters parent;

		private EClusters(int sign)
		{
			this.sign = sign;
			this.parent = null;
		}

		private EClusters(EClusters parent)
		{
			this.sign = parent.sign;
			this.parent = parent;
		}

		public int sign()
		{
			return sign;
		}
	}

	public String str(EClusters e)
	{
		if (e == null) return null;
		return e.name();
	}

	class Swap<E>
	{
		E go(E a, E b)
		{
			return a;
		}
	}

	public void transaction(Row row, int t, Date date) throws Exception
	{
		int o = IND_TRS[t];
		int amount = getInteger(row, o + 0);
		String formula = getFormula(row, o + 0);
		String ca = getString(row, o + 1);
		String cl = getString(row, o + 2);
		String market = getString(row, o + 3);
		String remark = getString(row, o + 4);
		String rextra = "tr_" + Integer.toString(t + 1);

		// System.out.println("tr nr." + t + " " + remark + " " + ca + " " + market + " " + amount);

		ECaids caid = caids.get(ca);

		/* this transaction's form is not filled in */
		if (caid == null || (amount == 0 && !caid.isinfo))
		{
			if (!remark.isEmpty() || !market.isEmpty() || !cl.isEmpty())
				throw new Exception("amount == 0 && !caid.isinfo, BUT remark || market || cluster is NOT NULL");
			return;
		}

		if (market.contains("+")) new Exception("Warning:" + row.getRowIndex() + "" + row.getCellByIndex(o).getCellStyleName() + " mkname contains `+'").printStackTrace();

		if (market.isEmpty()) market = null;
		appendMarket(market);

		/* decorate the remark with the transaction's amount or its formula */
		if (!remark.isEmpty()) remark += " ";
		if (!formula.isEmpty()) remark += "[" + formula.substring(4) + "]";
		else remark += "[" + amount + "]";

		try
		{
			/* ATHELYEZES */

			ECaids dst_caid = caids.get(cl);
			if (dst_caid == null || dst_caid == ECaids.none || dst_caid == ECaids.info) throw new Exception();

			if (amount > 0)
			{
				caid = new Swap<ECaids>().go(dst_caid, dst_caid = caid);
				amount *= -1;
			}

			int newbal_from = balance.get(caid) + amount;
			int newbal_to = balance.get(dst_caid) - amount;
			balance.replace(caid, newbal_from);
			balance.replace(dst_caid, newbal_to);

			trans.appendRecord(colsE, date, caid.str, amount, newbal_from, EClusters.Athelyezes_Innen.str, remark, rextra, market, false);
			trans.appendRecord(colsE, date, dst_caid.str, -amount, newbal_to, EClusters.Athelyezes_Ide.str, remark, rextra, market, false);
		} // @formatter off
		catch (Exception e) // formatter on
		{
			EClusters cluster = clusters.get(cl);
			cl = str(cluster);

			if (caid == ECaids.info)
			{
				/* INFO TRANZAKCIO */
				trans.appendRecord(colsE, date, caid.str, 0, 0, cl, remark, rextra, market, false);
			}
			else
			{
				/* SIMA TRANZAKCIO v. SZAMOLAS */
				int newbal = balance.get(caid) + amount;
				balance.replace(caid, newbal);
				trans.appendRecord(colsE, date, caid.str, amount, newbal, cl, remark, rextra, market, false);
			}
		}
	}

	public void appendMarket(String mkname)
	{
		if (mkname == null || markets.contains(mkname) || mkname.isEmpty()) return;

		markets.add(mkname);
		mks.appendRecord(colsmk, mkname);
	}

	public void koltsegvetesUj(Table table, int startIndex)
	{
		startIndex -= 2;
		int count = table.getRowCount();
		System.out.println("Ennyi sor van a tablaban: " + count);

		balance = new HashMap<>();

		/* I read the very first row after the header, which contains the first balance */
		for (int i = 0; i < c; ++i)
		{
			balance.put(E_CAIDS[i], getInteger(table.getRowByIndex(startIndex), IND_CAIDS + i));
			System.out.println(balance.get(E_CAIDS[i]));
		}
		++startIndex;

		/* loop over the rows of the table */
		for (int i = startIndex; i < count; i++)
		{
			Row row = table.getRowByIndex(i);
			Date date = getDate(row, IND_DATE);

			/* stop when we have left the actual date */
			if (date.after(today)) break;

			/* run throw all transactions of this row */
			for (int t = 0; t < n; ++t)
			{
				try
				{
					transaction(row, t, date);
				} catch (Exception e)
				{
					System.out.println("Problem:" + (i + 1) + ", date: " + TR_DATE.toDisplayString(date) + ", Transaction nr. " + (t + 1));
					e.printStackTrace();
				}
			}

			/* check validity */
			for (int k = 0; k < c; ++k)
			{
				ECaids caid = E_CAIDS[k];
				int balance_original = getInteger(row, IND_CAIDS + k);
				int balance_calculated = balance.get(caid);

				if (balance_original != balance_calculated)
				{
					String msg = "Problem:" + (i + 1) + ", date: " + TR_DATE.toDisplayString(date) + " " + caid.str + "\n"
						+ "\tin odf = " + balance_original + " =/= " + balance_calculated + " = calculated, diff = " + (balance_calculated - balance_original);
					System.err.println(msg);
				}
			}
		}
	}

	public ParseOdf2(String odfDocumentPath, int startIndex)
	{
		try
		{
			System.out.println("Elkezdtem olvasni a dokumentumot");
			SpreadsheetDocument data = SpreadsheetDocument.loadDocument(odfDocumentPath);

			System.out.println("Kivalasztom a Validity tablat");
			Table valTable = data.getTableByName("Validity");
			final int valFirstRow = 1;

			clusters = new HashMap<String, EClusters>();
			caids = new HashMap<String, ECaids>();
			markets = new HashSet<String>();
			for (int i = valFirstRow; i < valTable.getRowCount(); ++i)
			{
				Row row = valTable.getRowByIndex(i);
				String odfclname = getString(row, 0);
				String sqlclname = getString(row, 1);
				String caid = getString(row, 3);
				if (odfclname.isEmpty() && sqlclname.isEmpty() && odfclname.isEmpty()) break;

				// System.out.println(odfclname + " " + sqlclname + " " + caid);
				try
				{
					ECaids.valueOf(sqlclname);
				} catch (Exception e)
				{
					if (!sqlclname.isEmpty()) clusters.put(odfclname, EClusters.valueOf(sqlclname));
				}

				if (!caid.isEmpty()) caids.put(caid, ECaids.valueOf(caid));
			}

			trans = QueryBuilder.create(new AbstractQuery(), colsE);
			mks = QueryBuilder.create(new AbstractQuery(), colsmk);
			addcaids = QueryBuilder.create(new AbstractQuery(), colsca);

			/* parse table Koltsegvetes_Uj */
			System.out.println("Kivalasztom a fo tablat");
			Table table = data.getTableByName("Koltsegvetes_Uj");
			koltsegvetesUj(table, startIndex);

			for (Map.Entry<String, ECaids> entry : caids.entrySet())
			{
				System.out.println(entry.getKey() + " -> " + entry.getValue().name());
			}

			// trans.print();
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		System.out.println("Minden rendben, lefutottam");
	}

	private Date getDate(Row row, int index)
	{
		try
		{
			return row.getCellByIndex(IND_DATE).getDateValue().getTime();
		} catch (Exception e)
		{
			System.out.println("Problem");
			return Calendar.getInstance(Locale.getDefault()).getTime();
		}
	}

	private int getInteger(Row row, int index)
	{
		try
		{
			return row.getCellByIndex(index).getDoubleValue().intValue();
		} catch (Exception e)
		{
			return 0;
		}
	}

	private int getIntegerOrThrow(Row row, int index)
	{
		return row.getCellByIndex(index).getDoubleValue().intValue();
	}

	@SuppressWarnings("unused")
	private double getDouble(Row row, int index)
	{
		try
		{
			return row.getCellByIndex(index).getDoubleValue();
		} catch (Exception e)
		{
			return 0;
		}
	}

	private String getString(Row row, int index)
	{
		String ret = row.getCellByIndex(index).getStringValue();
		if (ret == null) return "";
		return ret;
	}

	private String getFormula(Row row, int index)
	{
		String ret = row.getCellByIndex(index).getFormula();
		if (ret == null) return "";
		return ret;
	}

	private String getNote(Row row, int index)
	{
		return row.getCellByIndex(index).getNoteText();
	}

	public AbstractQuery getTranzactions()
	{
		return trans;
	}

	public AbstractQuery getMarkets()
	{
		return mks;
	}

	public AbstractQuery getAdditionalCaids()
	{
		return addcaids;
	}

	// public static void main(String[] args)
	// {
	// new ParseOdf2("/home/polpe/Dropbox/koltsegvetes.ods", 600);
	// }
}

/*

0 
1 
2 
3 
4 
5 
6 
7 
8 
9 
10
11
12
13
14
15
16
17
18
19
20
21
22
23
24
25
26
27
28
29

*/