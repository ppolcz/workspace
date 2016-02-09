package com.napol.koltsegvetes;

import static com.napol.koltsegvetes.db.EColumnNames.CA_ID;
import static com.napol.koltsegvetes.db.EColumnNames.CA_NAME;
import static com.napol.koltsegvetes.db.EColumnNames.CL_DIRECTION;
import static com.napol.koltsegvetes.db.EColumnNames.CL_NAME;
import static com.napol.koltsegvetes.db.EColumnNames.MK_ID;
import static com.napol.koltsegvetes.db.EColumnNames.MK_NAME;
import static com.napol.koltsegvetes.db.EColumnNames.PI_ACID;
import static com.napol.koltsegvetes.db.EColumnNames.PI_AMOUNT;
import static com.napol.koltsegvetes.db.EColumnNames.PI_CAID;
import static com.napol.koltsegvetes.db.EColumnNames.PI_CLNAME;
import static com.napol.koltsegvetes.db.EColumnNames.PI_DATE;
import static com.napol.koltsegvetes.db.EColumnNames.PI_ID;
import static com.napol.koltsegvetes.db.EColumnNames.PI_MKID;
import static com.napol.koltsegvetes.db.EColumnNames.PI_TRID;
import static com.napol.koltsegvetes.db.EColumnNames.PI_WHAT;
import static com.napol.koltsegvetes.db.EColumnNames.QR_DATE;
import static com.napol.koltsegvetes.db.EColumnNames.TR_AMOUNT;
import static com.napol.koltsegvetes.db.EColumnNames.TR_CAID;
import static com.napol.koltsegvetes.db.EColumnNames.TR_CLNAME;
import static com.napol.koltsegvetes.db.EColumnNames.TR_DATE;
import static com.napol.koltsegvetes.db.EColumnNames.TR_MKNAME;
import static com.napol.koltsegvetes.db.EColumnNames.TR_NEWBALANCE;
import static com.napol.koltsegvetes.db.EColumnNames.TR_REMARK;
import static com.napol.koltsegvetes.util.Util.debug;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.napol.koltsegvetes.MainActivity2;
import com.napol.koltsegvetes.db.AbstractQuery;
import com.napol.koltsegvetes.db.DataStore;
import com.napol.koltsegvetes.db.EColumnNames;
import com.napol.koltsegvetes.db.ETableNames;
import com.napol.koltsegvetes.db.IColumn;
import com.napol.koltsegvetes.db.ParcelableQuery;
import com.napol.koltsegvetes.db.QueryBuilder;
import com.napol.koltsegvetes.util.Util;

/**
 * @author Polcz Péter <ppolcz@gmail.com>
 * 
 * Created on Oct 13, 2014 7:25:54 PM
 */
public class TrFormActivity extends Activity
{
	interface Operator
	{
		String displayString(Object[] row);

		boolean isInitial(Object[] row);
	}

	private static final Locale locale = Locale.getDefault();
	private static final SimpleDateFormat F_YEAR = new SimpleDateFormat("yyyy", locale);
	private static final SimpleDateFormat F_MONTH = new SimpleDateFormat("MM", locale);
	private static final SimpleDateFormat F_DAY = new SimpleDateFormat("dd", locale);

	private String initialCa = "pkez";
	private String initialCl = "Elelem";
	private String initialMk = "none";

	private DataStore db;

	private DatePicker dp;
	private Spinner caid;
	private Spinner cl;
	private Spinner mk;
	private EditText etr;
	private EditText eta;
	private LinearLayout pilist;

	private ParcelableQuery q;
	private Object[] r;

	private AbstractQuery clusters;

	// private static final Calendar D_CALENDAR = Calendar.getInstance();
	// private static final int D_YEAR = D_CALENDAR.get(Calendar.YEAR);
	// private static final int D_MONTH = D_CALENDAR.get(Calendar.MONTH);
	// private static final int D_DAY = D_CALENDAR.get(Calendar.DAY_OF_MONTH);

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_trform2);

		db = DataStore.instance();
		db.setContext(this);
		db.onOpen();

		clusters = db.select(CL_NAME, CL_DIRECTION);

		eta = (EditText) findViewById(R.id.trform_amount);
		etr = (EditText) findViewById(R.id.trform_remark);
		cl = (Spinner) findViewById(R.id.trform_cluster);
		mk = (Spinner) findViewById(R.id.trform_mkname);
		caid = (Spinner) findViewById(R.id.trform_caid);
		dp = (DatePicker) findViewById(R.id.trform_date);
		pilist = (LinearLayout) findViewById(R.id.trform_pilist);

		// @formatter:off
		try
		{
			q = getIntent().getExtras().getParcelable(MainActivity2.KEY_TR_QUERY);
			r = q.getFirst();
			eta.setText(r[q.getPosition(TR_AMOUNT)].toString());
			etr.setText(r[q.getPosition(TR_REMARK)].toString());
			initialCa = r[q.getPosition(TR_CAID)].toString();
			initialCl = r[q.getPosition(TR_CLNAME)].toString();
			initialMk = r[q.getPosition(TR_MKNAME)].toString();

			try
			{
				Date date = (Date) r[q.getPosition(TR_DATE)];
				dp.init(Integer.parseInt(F_YEAR.format(date)), Integer.parseInt(F_MONTH.format(date)), Integer.parseInt(F_DAY.format(date)), null);
			} catch (Exception e)
			{}

			debug("q is received from " + MainActivity.class.getCanonicalName());
		} catch (NullPointerException e)
		{
			q = (ParcelableQuery) QueryBuilder.create(new ParcelableQuery(), EColumnNames.getColumns(ETableNames.TRANZACTIONS));
			q.newRecord();
			r = q.getFirst();
			debug("q is null");
		}
		// @formatter:on

		setupSpinner(cl, clusters, new Operator()
		{
			@Override
			public boolean isInitial(Object[] row)
			{
				return row[0].toString().startsWith(initialCl);
			}

			@Override
			public String displayString(Object[] row)
			{
				return row[0] + " (" + ((Integer) row[1] < 0 ? "KI" : "BE") + ")";
			}
		});

		setupSpinner(caid, db.select(CA_NAME, CA_ID), new Operator()
		{
			@Override
			public boolean isInitial(Object[] row)
			{
				return row[1].toString().equalsIgnoreCase(initialCa);
			}

			@Override
			public String displayString(Object[] row)
			{
				return row[0] + " (" + row[1] + ")";
			}
		});

		setupSpinner(mk, db.select(MK_ID, MK_NAME), new Operator()
		{
			@Override
			public boolean isInitial(Object[] row)
			{
				return row[0].toString().equalsIgnoreCase(initialMk);
			}

			@Override
			public String displayString(Object[] row)
			{
				return (String) row[0];
			}
		});

		// dp.setOnLongClickListener(new OnLongClickListener()
		// {
		// @Override
		// public boolean onLongClick(View v)
		// {
		// dp.init(D_YEAR, D_MONTH, D_DAY, null);
		// return false;
		// }
		// });

		Button btnNewPi = (Button) findViewById(R.id.trform_newpi);
		btnNewPi.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				addNewPiForm();
			}
		});

		Button btn = (Button) findViewById(R.id.trform_submit);
		btn.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				finishAndReturn();
			}
		});
	}

	protected void setupSpinner(Spinner spinner, AbstractQuery query, Operator op)
	{
		int icaid = 0, j = 0;
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.simple_spinned_dropdown_item);
		for (Object[] row : query)
		{
			adapter.add(op.displayString(row));
			if (op.isInitial(row)) icaid = j;
			++j;
		}
		spinner.setAdapter(adapter);
		spinner.setSelection(icaid);
	}

	// @formatter:off
	private String getDateFromDatePicker()
	{
		return QR_DATE.toDisplayString(new GregorianCalendar(dp.getYear(), dp.getMonth(), dp.getDayOfMonth()).getTime());
	}

	private int getInt(EditText et)
	{
		try
		{
			return Integer.parseInt(et.getText().toString());
		} catch (Exception e)
		{
			return 0;
		}
	}

	private String getString(EditText et)
	{
		try
		{
			return et.getText().toString();
		} catch (Exception e)
		{
			return "";
		}
	}

	private String getString(Spinner sp)
	{
		try
		{
			return ((String) sp.getSelectedItem()).split("\\(")[0].trim();
		} catch (NullPointerException e)
		{
			return null;
		}
	}

	private void finishAndReturn()
	{
		Object date = getDateFromDatePicker();
		String selectedCaid = getString(caid);
		String selectedClname = getString(cl);
		Object selectedMkname = mk.getSelectedItem();

		// setup transaction data (i.e. group item data)
		q.setValue(TR_AMOUNT, 0, getInt(eta));
		q.setValue(TR_CAID, 0, selectedCaid);
		q.setValue(TR_CLNAME, 0, selectedClname);
		q.setValue(TR_DATE, 0, date);
		q.setValue(TR_REMARK, 0, etr.getText().toString());
		q.setValue(TR_MKNAME, 0, selectedMkname);
		q.setValue(TR_NEWBALANCE, 0, -1);

		// setup children items - create a table, in which all children are stored and sent to the host activity
		ParcelableQuery c = (ParcelableQuery) QueryBuilder.create(new ParcelableQuery(), EColumnNames.getColumns(ETableNames.PRODUCT_INFO));
		
		// PI_ID, PI_TRID, PI_ACID, PI_CAID, PI_AMOUNT, PI_CLNAME, PI_DATE, PI_MKID, PI_WHAT);
		IColumn[] insertTypes = { PI_CAID, PI_AMOUNT, PI_CLNAME, PI_DATE, PI_MKID, PI_WHAT };
		for (int i = 0; i < pilist.getChildCount(); ++i)
		{
			debug("pilist.getChildAt(i).id = " + pilist.getChildAt(i).getId());
			PiFormViewHolder holder = (PiFormViewHolder) pilist.getChildAt(i).getTag();
			if (holder != null) c.appendRecord(insertTypes, selectedCaid, getInt(holder.amount), getString(holder.cl), date, selectedMkname, getString(holder.what));
		}

		// ParcelableQuery q = new ParcelableQuery(TR_ID, TR_AMOUNT, TR_CAID, TR_CLNAME, TR_DATE, TR_REMARK);
		// q.addRecord(null,
		// Integer.parseInt(eta.getText().toString()),
		// ((String) caid.getSelectedItem()).split("\\(")[0].trim(),
		// ((String) cl.getSelectedItem()).split("\\(")[0].trim(),
		// getDateFromDatePicker(),
		// etr.getText().toString());

		Bundle b = new Bundle();
		b.putParcelable(MainActivity2.KEY_TR_QUERY, q);
		b.putParcelable(MainActivity2.KEY_PI_QUERY, c);
		try
		{
			b.putInt(MainActivity2.KEY_LADAPTER_INDEX, getIntent().getExtras().getInt(MainActivity2.KEY_LADAPTER_INDEX));
		} catch (NullPointerException e)
		{
			e.printStackTrace();
		}

		Intent i = new Intent();
		i.putExtras(b);
		setResult(RESULT_OK, i);
		finish();
	}

	// @formatter:on

	private class PiFormViewHolder
	{
		Spinner cl;
		Button del;
		EditText amount;
		EditText what;
	}

	/**
	 * Add new product information form list item.
	 */
	private void addNewPiForm()
	{
		LayoutInflater inflater = LayoutInflater.from(this);
		final View line = inflater.inflate(R.layout.hline, pilist, false);
		final View form = inflater.inflate(R.layout.pi_input_item, pilist, false);

		PiFormViewHolder holder = new PiFormViewHolder();
		holder.cl = (Spinner) form.findViewById(R.id.pi_clname);
		holder.del = (Button) form.findViewById(R.id.pi_remove);
		holder.amount = (EditText) form.findViewById(R.id.pi_amount);
		holder.what = (EditText) form.findViewById(R.id.pi_what);
		form.setTag(holder);

		// @formatter:off
		setupSpinner(holder.cl, clusters, new Operator()
		{
			@Override
			public boolean isInitial(Object[] row)
			{
				return row[0].toString().startsWith(getString(cl));
			}

			@Override
			public String displayString(Object[] row)
			{
				return row[0] + " (" + ((Integer) row[1] < 0 ? "KI" : "BE") + ")";
			}
		});
		// @formatter:on

		holder.del.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				Util.debug("remove: " + v.getId());
				pilist.removeView(form);
				pilist.removeView(line);
			}
		});

		pilist.addView(form);
		pilist.addView(line);

		debug("form.id = " + form.getId());
	}
}
