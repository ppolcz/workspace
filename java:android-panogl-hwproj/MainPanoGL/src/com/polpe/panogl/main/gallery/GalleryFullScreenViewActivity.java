package com.polpe.panogl.main.gallery;

import java.io.File;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.Window;
import android.view.WindowManager;

import com.polpe.panogl.R;
import com.polpe.panogl.main.gallery.adapter.ImageFullScreenPagerAdapter;
import com.polpe.panogl.util.GlobalConstants;

public class GalleryFullScreenViewActivity extends Activity {

    private ImageFullScreenPagerAdapter adapter;
    private ViewPager pw;

    private String dirname;
    private int position;

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // _____________________________________________________________
        // Set some window parameters: make the application fullScreen
        //
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // Window flag: as long as this window is visible to the user, keep the device's screen turned on and bright.
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        // _____________________________________________________________
        // 
        setContentView(R.layout.gallery_fullscreen_view);
        pw = (ViewPager) findViewById(R.id.pager);

        Intent i = getIntent();
        position = i.getIntExtra(GalleryListActivity.KEY_IMAGE_NR, 0);
        dirname = i.getStringExtra(GalleryListActivity.KEY_PANORAMA_DIRNAME);

        File dir = new File(dirname);
        if (!dir.isDirectory()) {
            setResult(RESULT_CANCELED);
            finish();
        }

        ArrayList<String> filepaths = new ArrayList<String>();
        for (File child : dir.listFiles()) {
            String filename = child.getAbsolutePath().toString();
            if (filename.endsWith("." + GlobalConstants.FILE_IMAGE_EXTENSION)) {
                filepaths.add(filename);
            }
        }

        adapter = new ImageFullScreenPagerAdapter(GalleryFullScreenViewActivity.this, filepaths);
        pw.setAdapter(adapter);

        // displaying selected image first
        pw.setCurrentItem(position);
    }
}
