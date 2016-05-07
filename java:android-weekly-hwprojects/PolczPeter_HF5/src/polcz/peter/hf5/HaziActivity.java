package polcz.peter.hf5;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

public class HaziActivity extends Activity {
    
    @SuppressWarnings("unused")
    private static final String TAG = HaziActivity.class.getSimpleName();

    private static final int PB_COUNT_MAX = 5;

    public static final int[] PB_IDS = {
            R.id.pb_0, R.id.pb_1, R.id.pb_2, R.id.pb_3, R.id.pb_4,
            R.id.pb_5, R.id.pb_6, R.id.pb_7, R.id.pb_8, R.id.pb_9
    };

    RelativeLayout rlMain;
    LinearLayout container;
    EditText etUrl;
    Button btnDownload;
    Button btnSave;
    ProgressBar pbDownload;

    BlockingQueue<ProgressBar> pbQueue = new LinkedBlockingQueue<ProgressBar>();

    int pbCount = 0;

    private LinearLayout.LayoutParams llChildParams;

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hazi);

        rlMain = (RelativeLayout) findViewById(R.id.rl_main);
        etUrl = (EditText) findViewById(R.id.et_url);
        btnDownload = (Button) findViewById(R.id.btn_download);
        btnSave = (Button) findViewById(R.id.btn_save);

        llChildParams = new LinearLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        llChildParams.setMargins(0, 5, 0, 0);

        container = new LinearLayout(this);
        container.setId(R.id.ll_container);
        container.setOrientation(LinearLayout.VERTICAL);
        RelativeLayout.LayoutParams container_lparams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        container_lparams.addRule(RelativeLayout.BELOW, btnSave.getId());
        container_lparams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        container_lparams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        container_lparams.setMargins(0, 10, 0, 0);
        rlMain.addView(container, container_lparams);

        setButtonListener();
        
        // addProgressBar();
        // addProgressBar();
        // ProgressBar pb1 = addProgressBar();
        // addProgressBar();
        // ProgressBar pb2 = addProgressBar();
        // addProgressBar();
        // ProgressBar pb3 = addProgressBar();
        //
        // pb3.setMax(10);
        // pb3.setProgress(5);
        // removeProgressBar(pb1);
        // removeProgressBar(pb2);
    }

    private void setButtonListener() {
        btnDownload.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick (View v) {
                
                while (downloadAvailable()) {
                    pbQueue.add(addProgressBar());
                }
                
                new Thread(new Runnable() {
                    
                    @Override
                    public void run () {
                        
                        
                    }
                }).start();;
            }
        });
    }
    
    public boolean downloadAvailable () {
        return pbCount < PB_COUNT_MAX;
    }

    public ProgressBar addProgressBar () {
        ProgressBar pb = new ProgressBar(this, null, android.R.attr.progressBarStyleHorizontal);
        container.addView(pb, llChildParams);
        pbCount++;
        return pb;
    }

    public void removeProgressBar (ProgressBar pb) {
        container.removeView(pb);
        pbCount--;
    }

}
