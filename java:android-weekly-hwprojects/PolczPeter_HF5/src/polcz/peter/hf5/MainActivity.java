package polcz.peter.hf5;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import polcz.peter.hf5.accessories.ReadURL;
import polcz.peter.hf5.services.DownloadService;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class MainActivity extends Activity {

    private static final String PATTERN_STR = "<img.*?src=\"(.*?)\".*?/>";

    protected static final String TAG = MainActivity.class.getSimpleName();

    private EditText et;
    private Button btn;
    private LinearLayout ll;
    private LayoutParams llChildParams;

    private BroadcastReceiver receiver = new BroadcastReceiver() {

        @Override
        public void onReceive (Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                int resultCode = bundle.getInt(DownloadService.KEY_RESULT);
                Bitmap bitmap = bundle.getParcelable(DownloadService.KEY_BITMAP);

                if (resultCode == RESULT_OK && bitmap != null) {
                    ImageView iw = new ImageView(MainActivity.this);
                    iw.setImageBitmap(bitmap);
                    ll.addView(iw, llChildParams);
                }
            }
        }
    };

    private OnClickListener btnDownloadListener = new OnClickListener() {

        @Override
        public void onClick (View v) {
            parsehtml(et.getText().toString());
        }
    };

    @Override
    public void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ll = (LinearLayout) findViewById(R.id.ll_main);
        et = (EditText) findViewById(R.id.et_url);
        btn = (Button) findViewById(R.id.btn_download_bgr);

        llChildParams = new LinearLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        llChildParams.setMargins(0, 5, 0, 0);

        et.setText("http://index.hu");

        btn.setOnClickListener(btnDownloadListener);
    }

    protected void parsehtml (final String url) {
        new Thread(new Runnable() {

            @Override
            public void run () {
                try {
                    
                    // beolvasom 
                    String text = ReadURL.getText(url);
                    
                    Pattern imgPattern = Pattern.compile(PATTERN_STR);
                    Matcher imgMatcher = imgPattern.matcher(text);

                    Log.e(TAG, "parsehtml elott");
                    
                    for (int i = 0; i < 500 && imgMatcher.find(); ++i) {
                        Intent intent = new Intent(MainActivity.this, DownloadService.class);
                        intent.putExtra(DownloadService.KEY_FILENAME, "kep.jpg");
                        intent.putExtra(DownloadService.KEY_URL, imgMatcher.group(1));
                        startService(intent);
                    }
                    
                    Log.e(TAG, "parsehtml utan");

                    ll.post(new Runnable() {
                        
                        @Override
                        public void run () {
                            Toast.makeText(MainActivity.this, "Parszolast befejeztem", Toast.LENGTH_SHORT).show();
                        }
                    });
                } catch (Exception e) {
                     e.printStackTrace();
                }
            }
        }).start();

    }

    @Override
    protected void onResume () {
        super.onResume();
        registerReceiver(receiver, new IntentFilter(DownloadService.NOTIFICATION));
    }

    @Override
    protected void onPause () {
        super.onPause();
        unregisterReceiver(receiver);
    }
}
