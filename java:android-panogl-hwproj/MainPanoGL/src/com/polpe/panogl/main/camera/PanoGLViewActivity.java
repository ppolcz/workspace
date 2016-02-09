package com.polpe.panogl.main.camera;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

import java.io.File;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.opengl.GLSurfaceView;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;

import com.polpe.panogl.R;
import com.polpe.panogl.main.gallery.GalleryListActivity;
import com.polpe.panogl.util.GlobalConstants;
import com.polpe.panogl.util.Util;

@TargetApi(Build.VERSION_CODES.GINGERBREAD)
public class PanoGLViewActivity extends Activity implements OnClickListener {
    protected static final String TAG = PanoGLViewActivity.class.getSimpleName();

    // handler messages
    public static final int MSG_START_PREVIEW_REQUESTED = 1;

    enum CameraState {
        FROZEN,
        AVAILABLE
    };

    private boolean activityCreated = false;

    private PanoGLVortexRenderer renderer;

    private FrameLayout fl;
    private PanoGLSurfaceView glw;
    private View btnView;

    // Buttons
    private Button btnShoot;
    private Button btnSettings;
    private Button btnAlbum;

    // Storage variables
    private String dirname;

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        dirname = intent.getStringExtra(GalleryListActivity.KEY_PANORAMA_DIRNAME);

        // _____________________________________________________________
        // Creating a translucent OpenGL ES SurfaceView
        glw = new PanoGLSurfaceView(this, false);
        glw.setZOrderMediaOverlay(true);

        // We want an 8888 pixel format because that's required for a translucent window.
        // And we want a depth buffer.
        // IMPORTANT - This method can only be called before setRenderer() is called.
        glw.setEGLConfigChooser(8, 8, 8, 8, 16, 0);
        // glw.setEGLConfigChooser(false);

        renderer = new PanoGLVortexRenderer(glw);

        // Tell the cube renderer that we want to render a translucent version of the cube:
        glw.setRenderer(renderer);
        // glw.setRenderer(new CubeRenderer(this, true));

        // Setting how frequent is the onDrawFrame() method called.
        // RENDERMODE_CONTINUOUSLY - call continuously with a specific time delay between calls.
        // RENDERMODE_WHEN_DIRTY - the renderer only renders when the surface is created, or when requestRender() is called.
        // IMPORTANT - This method can only be called after setRenderer() is called.
        glw.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);

        // Use a surface format with an Alpha channel:
        // glw.getHolder().setFormat(PixelFormat.RGBA_8888);
        // glw.getHolder().setFormat(PixelFormat.TRANSLUCENT);

        // setting layout parameters
        glw.setLayoutParams(new LayoutParams(MATCH_PARENT, MATCH_PARENT));

        // _____________________________________________________________
        // Inflating camera_buttons.xml
        btnView = getLayoutInflater().inflate(R.layout.camera_buttons, null);

        // _____________________________________________________________
        // Set some window parameters
        // make the application fullScreen
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // Window flag: as long as this window is visible to the user, keep the device's screen turned on and bright.
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        // _____________________________________________________________
        // Building the final FrameLayout
        fl = new FrameLayout(this);
        fl.addView(glw);
        fl.addView(btnView);
        setContentView(fl);

        // ===========================================================================================
        // SETTING LISTENERS

        btnAlbum = (Button) btnView.findViewById(R.id.btn_album);
        btnSettings = (Button) btnView.findViewById(R.id.btn_settings);
        btnShoot = (Button) btnView.findViewById(R.id.btn_shoot);

        btnAlbum.setOnClickListener(this);
        btnSettings.setOnClickListener(this);
        btnShoot.setOnClickListener(this);

        activityCreated = true;
    }

    @Override
    protected void onResume () {
        super.onResume();
        glw.onResume();
        
        for (File child : new File(dirname).listFiles()) {
            String filename = child.getAbsolutePath().toString();
            if (filename.endsWith("." + GlobalConstants.FILE_IMAGE_EXTENSION)) {
                renderer.addImage(filename);
                Util.debug("NEW IMAGE FILE FOUND");
            }
        }
    }

    @Override
    protected void onPause () {
        super.onPause();
        glw.onPause();
    }

    @Override
    protected void onDestroy () {
        super.onDestroy();
    }

    @Override
    // OnClickListener.~
    public void onClick (View view) {
        switch (view.getId())
        {
            case R.id.btn_album:
            {
                finish();
                break;
            }
            case R.id.btn_shoot:
            {
                Intent intent = new Intent();
                intent.putExtra(GalleryListActivity.KEY_CREATE_NEW_PANO, "OK");
                setResult(RESULT_OK, intent);
                finish();
                break;
            }
            case R.id.btn_settings:
            {
                break;
            }
            default:
                break;
        }
    }
}