package polpe.hf1;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

/**
 *
 * @BRIEF:
 * Ezt az Activity-t azert csinaltam, hogy lehessen valogatni melyik layoutot nyissa meg egy masik Activityben.
 * Azonban nem szerettem volna letrehozni meg KET Activity-t, azert dinamikusan probaltam beallitani az
 * {@link ActivityHomeWork} activitybe, hogy melyik layoutot valassza.
 * 
 * @PROBLEMA:
 * Ugy lattam activityknek nem lehet construktort definialni, ezert Intent-Bundle segitsegevel probaltam valahogy
 * infokat kozolni az Activity peldanynak. Sok bajom volt vele. Ha nem olyan ahogy azt TI profik csinaljatok, 
 * mutassatok meg hogy kell igazibol.
 * 
 * @POTENCIALIS_HIBALEHETOSEG:
 * Intent-Bundle : hosszas kinlodas utan vegre atadodtak az ertekek.
 * Oszinten szolva fogalmam sincs, hogy MOST miert megy, mikor 3 oraval ezelott kb. ugyanerre a kodra NEM ment at.
 * Azert raktam try-catch blokkba az {@link ActivityHomeWork}-ban a Bundle-atvevest, hogy tuti ne szalljon el...
 * HA nem mukodik ugy ahogy szeretnem, akkor kerlek a catch agban irjatok at az activity1 layoutot activity2-re.
 * 
 * @HA_NEM_JO_SZOLJATOK: 
 *  - meg sose irtam intent-bundle-t. Lehet, hogy nem irtam ugy ahogy azt irni kellene, de akkor szoljatok.
 *  Tovabba a manifestben nem tettem emlitest errol a bundle-es dologrol - kell-e egyaltalan?
 * 
 * @KERDES:
 * Miert hivjuk az objektum szintu valtozokat 'm' el kezdodo nevvel?
 * Talan 'main', 'my'?
 * Ez az 'm' mit is akar PONTOSAN takarni?
 * 
 */
public class MainActivity extends Activity {
	
	private class CallOnClickListener implements OnClickListener {

		int layoutId;
		int activityTitle;
		
		public CallOnClickListener(int layoutId, int activityTitle) {
			this.layoutId = layoutId;
			this.activityTitle = activityTitle;
		}
		
		@Override
		public void onClick(View v) {
			Intent intent = new Intent(MainActivity.this, ActivityHomeWork.class);
			Log.d("PolpeLog", String.format("CallOnClickListener.layoutId = %d", layoutId));
			intent.putExtra("layoutId", layoutId);
			intent.putExtra("nameId", activityTitle);
			startActivity(intent);
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		// itt jon az, hogy lehet valogatni a ket layout kozott
		Button mButton1 = (Button) findViewById(R.id.mbutton_choos_l1);
		mButton1.setOnClickListener(new CallOnClickListener(R.layout.activity1, R.string.app_name_1));
		
		Button mButton2 = (Button) findViewById(R.id.mbutton_choos_l2);
		mButton2.setOnClickListener(new CallOnClickListener(R.layout.activity2, R.string.app_name_2));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}




// 4 layoutra mar egy kicsit hosszu lenne:

//final OnClickListener clickListener1 = new OnClickListener() {
//	@Override
//	public void onClick(View v) {
//		Intent i = new Intent(MainActivity.this, ActivityHomeWork.class);
//		i.putExtra("R.layout.actual_layout", R.layout.activity1);
//		startActivityForResult(i, 0);
//	}
//};
//
//final OnClickListener clickListener2 = new OnClickListener() {
//	@Override
//	public void onClick(View v) {
//		Intent i = new Intent(MainActivity.this, ActivityHomeWork.class);
//		i.putExtra("R.layout.actual_layout", R.layout.activity2);
//		startActivityForResult(i, 0);
//	}
//};

