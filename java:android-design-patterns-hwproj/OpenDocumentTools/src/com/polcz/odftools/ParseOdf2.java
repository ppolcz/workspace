package com.polcz.odftools;

import static com.napol.koltsegvetes.db.EColumnNames.*;
import static java.lang.Math.abs;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.odftoolkit.simple.SpreadsheetDocument;
import org.odftoolkit.simple.table.Row;
import org.odftoolkit.simple.table.Table;

import com.napol.koltsegvetes.db.AbstractDataStore;
import com.napol.koltsegvetes.db.AbstractQuery;
import com.napol.koltsegvetes.db.IColumn;
import com.napol.koltsegvetes.db.QueryBuilder;
import com.napol.koltsegvetes.util.Debug;

/**
 * 
 * @author Polcz Péter <ppolcz@gmail.com>
 * Created on Nov 14, 2014 7:27:54 PM
 */
public class ParseOdf2
{
	public static final String username = "ppolcz";

	public static final int IND_DATE = 0;
	public static final int IND_OTP_SZAMLA = 2;
	public static final int IND_OTP_BEVETEL = 3;
	public static final int IND_OTP_KIVETEL = 4;
	public static final int IND_OTP_KOLTSEG = 5;
	public static final int IND_KEZ_SZAMLA = 6;
	public static final int IND_KEZ_BEVETEL = 7;
	public static final int IND_KEZ_KOLTSEG = 8;
	public static final int IND_EXTRA_OSSZEG = 9;
	public static final int IND_EXTRA_HONNAN = 10;
	public static final int IND_EXTRA_KLASZTER = 11;
	public static final int IND_MARKET = 12;
	public static final int IND_REMARK = 13;
	public static final int IND_DORINAL = 14;

	private AbstractQuery trans;
	private AbstractQuery mks;
	private AbstractQuery addcaids;
	private Map<String, EClusters> clusters;
	private Map<String, ECaids> caids;
	private Set<String> markets;

	enum ECaids
	{
		pkez, potp, dkez, dotp, info;

		public final String str = name();
	}

	enum EClusters
	{
		Egyeb_Kiadas(-1),
		Egyeb_Bevetel(+1),
		Athelyezes_Innen(-1),
		Athelyezes_Ide(+1),
		Elelem(Egyeb_Kiadas),
		Ruhazkodas(Egyeb_Kiadas),
		Alberlet(Egyeb_Kiadas),
		Rezsi(Egyeb_Kiadas),
		Rezsi_Bkv(Rezsi),
		Rezsi_Gaz(Rezsi),
		Rezsi_Viz(Rezsi),
		Rezsi_Futes(Rezsi),
		Rezsi_Elmu(Rezsi),
		Rezsi_Kozosk(Rezsi),
		Rezsi_Upc(Rezsi),
		Kaucio(Egyeb_Kiadas),
		Luxus(Egyeb_Kiadas),
		Kaucio_Vissza(Egyeb_Bevetel),
		Otthon(Egyeb_Bevetel),
		JozsaOtthon(Egyeb_Bevetel),
		Osztondij(Egyeb_Bevetel),
		Fizetes(Egyeb_Bevetel),
		Bevetel_Otthon(Egyeb_Bevetel),
		Korrigalas(1),
		Szamolas(1),
		Napi_Szukseglet(1),
		Szorakozas_Bal(Egyeb_Kiadas),
		Szorakozas_Mozi(Egyeb_Kiadas),
		Szorakozas_Tanc(Egyeb_Kiadas),
		Gyogyszer(Egyeb_Kiadas),
		Elektr_Cikk(Egyeb_Kiadas);

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

	public ParseOdf2(String odfDocumentPath)
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
				String odfcaid = getString(row, 2);
				String sqlcaid = getString(row, 3);
				if (odfcaid.isEmpty() && odfcaid.isEmpty()) break;

				if (!sqlclname.isEmpty()) clusters.put(odfclname, EClusters.valueOf(sqlclname));
				if (!sqlcaid.isEmpty()) caids.put(odfcaid, ECaids.valueOf(sqlcaid));
			}

			System.out.println("Kivalasztom a fo tablat");
			Table table = data.getTableByName("Koltsegvetes");

			int count = table.getRowCount();
			System.out.println("Ennyi sor van a tablaban: " + count);

			int startIndex = 4;
			int otpSzamlaActual = getInteger(table.getRowByIndex(startIndex - 1), IND_OTP_SZAMLA);
			int kezSzamlaActual = getInteger(table.getRowByIndex(startIndex - 1), IND_KEZ_SZAMLA);

			IColumn[] cols = { TR_DATE, TR_CAID, TR_AMOUNT, TR_NEWBALANCE, TR_CLNAME, TR_REMARK, TR_MKNAME, TR_PIVOT /*, TR_ACID*/};
			IColumn[] colsE = { TR_DATE, TR_CAID, TR_AMOUNT, TR_NEWBALANCE, TR_CLNAME, TR_REMARK, TR_REMARK_EXTRA, TR_MKNAME, TR_PIVOT };
			IColumn[] colsca = { CA_ID, CA_NAME };
			IColumn[] colsmk = { MK_ID };
			trans = QueryBuilder.create(new AbstractQuery(), colsE);
			mks = QueryBuilder.create(new AbstractQuery(), colsmk);
			addcaids = QueryBuilder.create(new AbstractQuery(), colsca);
			for (int i = startIndex; i < count; i++)
			{
				Row row = table.getRowByIndex(i);
				Date date = getDate(row, IND_DATE);

				// extra transactions, extra infos
				int extraOsszeg = getInteger(row, IND_EXTRA_OSSZEG);
				String extraKlaszter = getString(row, IND_EXTRA_KLASZTER);
				String extraCaid = getString(row, IND_EXTRA_HONNAN);
				String extraOsszegFormula = getFormula(row, IND_EXTRA_OSSZEG);
				String extraOsszegNote = getNote(row, IND_EXTRA_OSSZEG);
				boolean clusterAssigned = false;

				// market
				String market = getString(row, IND_MARKET);
				boolean marketAssigned = false;

				// OTP
				int otpSzamla = getInteger(row, IND_OTP_SZAMLA);
				int otpBevetel = getInteger(row, IND_OTP_BEVETEL);
				int otpKivetel = getInteger(row, IND_OTP_KIVETEL);
				int otpKoltseg = getInteger(row, IND_OTP_KOLTSEG);
				String otpBevetelFormula = getFormula(row, IND_OTP_BEVETEL);
				String otpKivetelFormula = getFormula(row, IND_OTP_KIVETEL);
				String otpKoltsegFormula = getFormula(row, IND_OTP_KOLTSEG);

				// kezpenz
				int kezSzamla = getInteger(row, IND_KEZ_SZAMLA);
				int kezBevetel = getInteger(row, IND_KEZ_BEVETEL);
				int kezKoltseg = getInteger(row, IND_KEZ_KOLTSEG);
				int kezDorinal = getInteger(row, IND_DORINAL);
				String kezBevetelFormula = getFormula(row, IND_KEZ_BEVETEL);
				String kezKoltsegFormula = getFormula(row, IND_KEZ_KOLTSEG);

				if (otpSzamla == 0) break;

				// megjegyzes
				String remark = getString(row, IND_REMARK).trim();

				if (extraOsszegNote != null && extraOsszegNote.isEmpty()) extraOsszegNote = null;

				// register market
				if (market != null && market.isEmpty()) market = null;
				if (market != null && !markets.contains(market))
				{
					markets.add(market);
					mks.appendRecord(colsmk, market);
				}

				// register charge account ID
				if (extraCaid != null && extraCaid.isEmpty()) extraCaid = null;
				if (extraCaid != null && !caids.containsKey(extraCaid))
				{
					caids.put(extraCaid, ECaids.valueOf(extraCaid));
					addcaids.appendRecord(colsca, extraCaid, "Please check this out!");
				}

				EClusters klaszter = null;
				if (!extraKlaszter.isEmpty()) klaszter = clusters.get(extraKlaszter);

				// if extraOsszegNote != null

				if ((extraOsszeg != 0 || extraOsszegNote != null) && klaszter != EClusters.Korrigalas)
				{
					String formula = "";
					int actual = -1;

					ECaids caid = ECaids.info;
					// if (extraOsszeg == 0) caid = ECaids.pkez;
					if (extraCaid != null) caid = caids.get(extraCaid);

					if (caid == ECaids.pkez)
					{
						kezSzamlaActual -= extraOsszeg;
						actual = kezSzamla;
					}

					if (caid == ECaids.potp)
					{
						otpSzamlaActual -= extraOsszeg;
						actual = kezSzamla;
					}

					if (!extraOsszegFormula.isEmpty()) formula = " [" + extraOsszegFormula.substring(4) + "]";
					try
					{
						System.out.println(extraOsszegNote);
						trans.appendRecord(colsE, date,
							(caid == null) ? null : caid.str, extraOsszeg, actual, 
							(klaszter == null) ? null : klaszter.str, remark + formula, extraOsszegNote, null /* market *//*, username*/, false);
					} catch (Exception e)
					{
						System.out.println(date);
						System.out.println();
						System.out.println(extraOsszeg);
						System.out.println(actual);
						System.out.println(klaszter.str);
						System.out.println(extraOsszegNote);
					}
				}

				if (kezKoltseg != 0)
				{
					EClusters cls = EClusters.Egyeb_Kiadas;
					if (extraOsszeg == 0 && klaszter != null && klaszter.sign < 0 /* kiadas */)
					{
						cls = klaszter;
					}

					String formula = "";
					int amount = abs(kezKoltseg);
					kezSzamlaActual -= amount;
					if (!kezKoltsegFormula.isEmpty()) formula = " [" + kezKoltsegFormula.substring(4) + "]";
					String m = null;
					if (!marketAssigned)
					{
						marketAssigned = true;
						m = market;
					}
					trans.appendRecord(cols, date, ECaids.pkez.str, amount, kezSzamlaActual, cls.str, remark + formula, m /* market *//*, username*/, false);
				}

				if (otpBevetel != 0)
				{
					EClusters cls = EClusters.Egyeb_Bevetel;
					if (extraOsszeg == 0 && klaszter != null && klaszter.sign > 0 /* bevetel */)
					{
						cls = klaszter;
					}

					String formula = "";
					int amount = abs(otpBevetel);
					otpSzamlaActual += amount;
					if (!otpBevetelFormula.isEmpty()) formula = " [" + otpBevetelFormula.substring(4) + "]";
					trans.appendRecord(cols, date, ECaids.potp.str, amount, otpSzamlaActual, cls.str, remark + formula, null /* market *//*, username*/, false);
				}

				if (otpKoltseg != 0)
				{
					EClusters cls = EClusters.Egyeb_Kiadas;
					if (extraOsszeg == 0 && klaszter != null && klaszter.sign < 0 /* kiadas */)
					{
						cls = klaszter;
					}

					String formula = "";
					int amount = abs(otpKoltseg);
					otpSzamlaActual -= amount;
					if (!otpKoltsegFormula.isEmpty()) formula = " [" + otpKoltsegFormula.substring(4) + "]";
					String m = null;
					if (!marketAssigned)
					{
						marketAssigned = true;
						m = market;
					}
					trans.appendRecord(cols, date, ECaids.potp.str, amount, otpSzamlaActual, cls.str, remark + formula, m /* market *//*, username*/, false);
				}

				if (otpKivetel != 0)
				{
					String formula = "";
					otpSzamlaActual -= otpKivetel;
					kezSzamlaActual += otpKivetel;
					if (!otpKivetelFormula.isEmpty()) formula = " [" + otpKivetelFormula.substring(4) + "]";

					trans.appendRecord(cols, date, ECaids.potp.str, otpKivetel, otpSzamlaActual, EClusters.Athelyezes_Innen.str, remark + formula, null /* market *//*, username*/, false);
					trans.appendRecord(cols, date, ECaids.pkez.str, otpKivetel, kezSzamlaActual, EClusters.Athelyezes_Ide.str, remark + formula, null /* market *//*, username*/, false);
				}

				if (kezBevetel != 0)
				{
					EClusters cls = EClusters.Egyeb_Bevetel;
					if (extraOsszeg == 0 && klaszter != null && klaszter.sign > 0 /* bevetel */)
					{
						cls = klaszter;
					}

					String formula = "";
					int amount = abs(kezBevetel);
					kezSzamlaActual += amount;
					if (!kezBevetelFormula.isEmpty()) formula = " [" + kezBevetelFormula.substring(4) + "]";
					trans.appendRecord(cols, date, ECaids.pkez.str, amount, kezSzamlaActual, cls.str, remark + formula, null /* market *//*, username*/, false);
				}

				if (kezDorinal != 0)
				{
					kezSzamlaActual -= kezDorinal;
					trans.appendRecord(cols, date, ECaids.pkez.str, kezDorinal, kezSzamlaActual, EClusters.Egyeb_Kiadas.str, "Dorinal van", null /* market *//*, username*/, false);
				}

				// if (klaszter == EClusters.Korrigalas)
				if (klaszter != null)
				{
					if (otpSzamlaActual != otpSzamla)
					{
						trans.appendRecord(cols, date, ECaids.potp.str, otpSzamla - otpSzamlaActual, otpSzamla, EClusters.Korrigalas.str, remark, null/*, username*/, true);
						otpSzamlaActual = otpSzamla;
					}

					if (kezSzamlaActual != kezSzamla)
					{
						trans.appendRecord(cols, date, ECaids.pkez.str, kezSzamla - kezSzamlaActual, kezSzamla, EClusters.Korrigalas.str, remark, null/*, username*/, true);
						kezSzamlaActual = kezSzamla;
					}
				}

				if (otpSzamlaActual != otpSzamla)
				{
					System.out.println(extraKlaszter);
					System.out.println(clusters.get(extraKlaszter));
					System.out.println(klaszter.str);

					System.err.println("otpSzamlaActual != otpSzamla " + "At (row:" + (row.getRowIndex() + 1) + "," + (IND_EXTRA_HONNAN + 1) + ":col) "
						+ "\n\totp> calculated: " + otpSzamlaActual + " != " + otpSzamla + " :written in odf");
					otpSzamlaActual = otpSzamla;
				}
				if (kezSzamlaActual != kezSzamla)
				{
					System.out.println(extraKlaszter);
					System.out.println(clusters.get(extraKlaszter));
					System.out.println(klaszter.str);

					System.err.println("kezSzamlaActual != kezSzamla " + "At (row:" + (row.getRowIndex() + 1) + "," + (IND_EXTRA_HONNAN + 1) + ":col) "
						+ "\n\tkez> calculated: " + kezSzamlaActual + " != " + kezSzamla + " :written in odf");
					System.err.println("kezKoltseg = " + kezKoltseg);
					System.err.println("extraOsszeg = " + extraOsszeg + "  innen: " + extraCaid);
					kezSzamlaActual = kezSzamla;
				}

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
	// new ParseODF("/home/polpe/Dropbox/koltsegvetes_demo.ods");
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