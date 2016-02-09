package polpe.hf1;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

/**
 * --- UGYE NEM KELL IEEE standardnak megfelelo specifikacio --- ???
 * 
 * @author polcz
 *
 * Konkretan ez volt a hazi feladat.
 * 
 * @remark Nem tudom pontosan, hogy ertelmesebb a valtozokat megvalasztani.
 * Sok helyen lattam, hogy az mButton jellegu csak az onCreate-ben szukseges valtozok nincsenek objektum szinten deklaralva.
 * Ha szerintetek sem jo, akkor nyugodtan POFAZZATOK.
 * 
 */
@SuppressLint("ShowToast")
public class ActivityHomeWork extends Activity {

	// non-static fields [BEGIN]

	int mTitle;
	int mLayoutId;
	TextView mTextView1;
	EditText mEdit1;
	Button mButton;
	
	// non-static fields [END]
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		try {
			// atveszem a Boundle-bol a resource ID-kat, majd beallitom a layoutot es az activity nevet
			
			Intent intent = getIntent();
			mLayoutId = intent.getIntExtra("layoutId", 0);
			mTitle = intent.getIntExtra("nameId", 0);
			Log.d("PolpeLog", "ActivityHomeWork -> mLayoutId = " + String.valueOf(mLayoutId));
			Log.d("PolpeLog", "ActivityHomeWork -> mTitle = " + String.valueOf(mTitle));
			
			// setterek
			setContentView(mLayoutId);
			setTitle(getString(mTitle));
			
		} catch (Exception e) {
			// on the off-chance
			
			setContentView(R.layout.activity1);
			setTitle(getString(R.string.app_name_1));
			
			Log.e("PolpeLog", "HIBA: nem sikerult elkuldeni a resource ID-kat! ", e);
		}
		
		// hozzarendelem a layout-on levo widgeteket a fieldekhez
		mTextView1 = (TextView) findViewById(R.id.textview1);
		mEdit1 = (EditText) findViewById(R.id.edit_text1);
		mButton = (Button) findViewById(R.id.button1);

		// Copy buttonhoz listener irasa
		findViewById(R.id.button1).setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				mTextView1.setText(mEdit1.getText().toString());
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
}



// EGYEB PROBALKOZASOK

//Bundle bundle = getIntent().getExtras();
//if (bundle != null) {
//	 Log.d("PolpeLog", "bundle is not null");
//	 if(bundle.containsKey("R.layout.actual_layout")) {
//		 int asdf = bundle.getInt("R.layout.actual_layout", -1);
//		 Log.i("Activity2 Log", "asdf:"+String.valueOf(asdf));
//	 }
//} else {
//	 Log.i("Activity2 Log", "asdf is null");
//
//}
//
//try {
//	mLayoutId = (int) getIntent().getExtras().getLong("R.layout.actual_layout");
//
//	Log.d("PolpeLog", String.format("mLayoutId = %d", mLayoutId));
//	setContentView(mLayoutId);
//} catch (Exception e) {
//	Toast.makeText(this, "Passing bundle failed", Toast.LENGTH_SHORT);
//}

