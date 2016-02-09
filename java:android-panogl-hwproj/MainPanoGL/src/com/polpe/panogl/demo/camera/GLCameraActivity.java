package com.polpe.panogl.demo.camera;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.opengl.GLSurfaceView;
import android.os.Build;
import android.os.Bundle;
import android.os.ConditionVariable;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
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
public class GLCameraActivity extends Activity implements OnClickListener, SurfaceHolder.Callback {
    protected static final String TAG = GLCameraActivity.class.getSimpleName();
    private static final int CAMERA_ID = 0;
    private static final int TIMER_DELAY_MSEC = 300;

    // handler messages
    public static final int MSG_START_PREVIEW_REQUESTED = 1;

    enum CameraState {
        FROZEN,
        AVAILABLE
    };

    ConditionVariable pictureTakenLock;

    private SurfaceHolder holder;
    private Camera camera;
    private CameraState cameraState;

    private FrameLayout fl;
    private GLPanoView glw;
    private SurfaceView sw;
    private View btnView;

    // Panodroid
    // private PolpePanoViewer panoViewer;
    // private GLPanoView panoGlw;
    // private Bitmap panoSampleBmp;

    // Buttons
    private Button btnShoot;
    private Button btnSettings;
    private Button btnAlbum;

    // Storage variables
    private String dirname;

    // Ourt main handler
    private MainHandler handler;

    // timer: when a photo is taken, the preview is frozen for a while.
    // This timer constitutes that "while".
    private class TimerThread extends Thread {
        // TODO if the wait is still ahead, and we push the shoot button again,
        // the application gets stuck (i.e. is not responding)
        @Override
        public void run () {
            synchronized (this) {
                try {
                    Util.debug("waiting for condVar");
                    pictureTakenLock.block();
                    Util.debug("waiting ...");
                    wait(TIMER_DELAY_MSEC);
                    Util.debug("handler.sendEmptyMessage(MSG_START_PREVIEW_REQUESTED)");
                    handler.sendEmptyMessage(MSG_START_PREVIEW_REQUESTED);
                    Util.debug("[sent] handler.sendEmptyMessage(MSG_START_PREVIEW_REQUESTED)");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    pictureTakenLock.close();
                }
            }
        }
    }

    public GLCameraActivity() {
        super();
        pictureTakenLock = new ConditionVariable();
    }

    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // initializing our main handler
        handler = new MainHandler(this);

        Intent intent = getIntent();
        dirname = intent.getStringExtra(GalleryListActivity.KEY_PANORAMA_DIRNAME);

        // ===========================================================================================
        // CREATING CAMERA LAYOUT
        // _____________________________________________________________
        // Camera preview using Android APIs
        sw = new SurfaceView(this);
        sw.setZOrderMediaOverlay(false);
        sw.setLayoutParams(new ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT));

        holder = sw.getHolder();
        holder.addCallback(this);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            // In Samsung Mini II this line is required, otherwise it gets stuck
            holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }
        Util.debug("holder settings");

        // _____________________________________________________________
        // Creating a translucent OpenGL ES SurfaceView
        glw = new GLPanoView(this);
        glw.setZOrderMediaOverlay(true);

        // We want an 8888 pixel format because that's required for a translucent window.
        // And we want a depth buffer.
        // TODO IMPORTANT - This method can only be called before setRenderer() is called.
        glw.setEGLConfigChooser(8, 8, 8, 8, 16, 0);
        // glw.setEGLConfigChooser(false);

        renderer = new GLPanoRenderer(this);

        // Tell the cube renderer that we want to render a translucent version of the cube:
        glw.setRenderer(renderer);
        // glw.setRenderer(new CubeRenderer(this, true));

        // Setting how frequent is the onDrawFrame() method called.
        // RENDERMODE_CONTINUOUSLY - call continuously with a specific time delay between calls.
        // RENDERMODE_WHEN_DIRTY - the renderer only renders when the surface is created, or when requestRender() is called.
        // TODO IMPORTANT - This method can only be called after setRenderer() is called.
        glw.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);

        // Use a surface format with an Alpha channel:
        glw.getHolder().setFormat(PixelFormat.RGBA_8888);
        glw.getHolder().setFormat(PixelFormat.TRANSLUCENT);

        // setting layout parameters
        glw.setLayoutParams(new LayoutParams(MATCH_PARENT, MATCH_PARENT));

        // ImageView iw = new ImageView(this);
        // iw.setImageDrawable(getResources().getDrawable(R.drawable.ic_launcher));
        // iw.setLayoutParams(new LayoutParams(MATCH_PARENT, MATCH_PARENT));

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
        fl.addView(sw);
        fl.addView(glw);
        // fl.addView(iw);
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
    }

    @Override
    protected void onResume () {
        Util.debug();

        super.onResume();
        // panoViewer.onViewerResume(); // TODO
        glw.onResume();
        // ----------------
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            camera = Camera.open(CAMERA_ID);
            Util.debug("camera = Camera.open(CAMERA_ID);");
        } else {
            camera = Camera.open();
            Util.debug("camera = Camera.open();");
        }
        cameraState = CameraState.AVAILABLE;
        // ----------------
        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_3, this, loaderCallback);
    }

    @Override
    protected void onPause () {
        super.onPause();

        Util.debug();

        // panoViewer.onViewerPause(); // TODO
        glw.onPause();

        if (camera != null) {
            // camera: Null
            camera.release();
            camera = null;
            Util.debug("camera.release(); camera = null");
        }
    }

    @Override
    protected void onDestroy () {
        Util.debug();

        // panoViewer.onViewerDestroy(); // TODO
        super.onDestroy();
    }

    /**
     * This callback handles the event when OpenCV Manager is not installed on the device.
     * TODO IMPORTANT - An elegant way to handle dependencies.
     */
    private BaseLoaderCallback loaderCallback = new BaseLoaderCallback(this) {

        @Override
        // BaseLoaderCallback.~
        public void onManagerConnected (int status) {
            switch (status)
            {
                case LoaderCallbackInterface.SUCCESS:
                // In case of OpenCV Manager is available and active on the target device
                {
                    Log.i(TAG, "OpenCV loaded successfully");
                    break;
                }
                default:
                // In case if OpenCV is not installed on the devices
                {
                    super.onManagerConnected(status);
                    break;
                }
            }
        }

        // This function is called when no OpenCV Manager is found on the device
        // --
        // public void LoaderCallbackInterface.onPackageInstall(int operation, org.opencv.android.InstallCallbackInterface callback);
    };

    @Override
    // OnClickListener.~
    public void onClick (View view) {
        switch (view.getId())
        {
            case R.id.btn_album:
                break;

            case R.id.btn_shoot:
            {
                switch (cameraState)
                {
                    case AVAILABLE:
                        Util.debug("btnShoot was pressed ============= ");
                        btnShoot.setEnabled(false);

                        cameraState = CameraState.FROZEN;
                        camera.takePicture(shutterCallback, rawCallback, jpegCallback);
                        Util.debug("camera.takePicture(...)");
                        new TimerThread().start();
                        break;

                    case FROZEN:
                        // restartPreview();
                        break;

                    default:
                        break;
                }
            }
            case R.id.btn_settings:
            {
                break;
            }
            default:
                break;
        }
    }

    @Override
    public void surfaceCreated (SurfaceHolder holder) {
        Util.debug();

        try {
            Parameters params = camera.getParameters();
            camera.setParameters(params);
            Util.debug("camera.setParameters(params)");
            camera.setPreviewDisplay(holder);
            Util.debug("camera.setPreviewDisplay(holder)");
        } catch (NullPointerException ex) {
            // if camera == null
            Log.e(TAG, "NullPointerException - Probably camera == null", ex);
            finish();
        } catch (IOException ex) {
            Log.e(TAG, "IOException - setPreviewDisplay()", ex);
        }
    }

    @Override
    public void surfaceChanged (SurfaceHolder holder, int format, int width, int height) {
        Util.debug("camera.startPreview()");
        camera.startPreview();
    }

    @Override
    public void surfaceDestroyed (SurfaceHolder holder) {
        Util.debug();

        try {
            camera.stopPreview();
            Util.debug("camera.stopPreview()");
            camera.release();
            Util.debug("camera.release()");
        } catch (NullPointerException ex) {
            Log.w(TAG, "surfaceDestroyed", ex);
        } catch (IllegalStateException ex) {
            Log.w(TAG, "surfaceDestroyed", ex);
        }
    }

    private String createNewImage () {
        return Util.createNonExistentFileWithPrefix(dirname, GlobalConstants.FILE_IMAGE_PREFIX, GlobalConstants.FILE_IMAGE_EXTENSION);
    }

    // ================================================================ Shoot Image Callbacks [BEGIN]
    /** Auxiliary callback object */
    ShutterCallback shutterCallback = new ShutterCallback() {
        public void onShutter () {}
    };

    /** Handles data for raw picture */
    PictureCallback rawCallback = new PictureCallback() {
        public void onPictureTaken (byte[] data, Camera camera) {}
    };

    /** Handles data for jpeg picture */
    PictureCallback jpegCallback = new PictureCallback() {
        public void onPictureTaken (byte[] data, Camera camera) {
            try {
                Util.debug(btnShoot.isEnabled() + "");

                String fname = createNewImage();
                // Toast.makeText(CreatePanoramaActivity.this, fname, Toast.LENGTH_LONG).show();
                File file = new File(fname);
                Util.debug(btnShoot.isEnabled() + "");

                // write to sdcard
                FileOutputStream outStream = new FileOutputStream(file.toString());
                outStream.write(data);
                outStream.close();
                Util.debug(btnShoot.isEnabled() + "");

                // finish();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                pictureTakenLock.open();
            }
        }
    };

    // ================================================================ Shoot Image Callbacks [END]

    private GLPanoRenderer renderer;

    Camera getCamera () {
        return camera;
    }

    CameraState getCameraState () {
        return cameraState;
    }

    void setCameraState (CameraState state) {
        cameraState = state;
    }

    void setBtnShootEnable (boolean enabled) {
        Util.assertation(btnShoot != null);
        btnShoot.setEnabled(enabled);
    }

    void restartPreview () {
        camera.startPreview();
        Util.debug("camera.startPreview()");
        cameraState = CameraState.AVAILABLE;
    }

    Button getBtnShoot () {
        return btnShoot;
    }
}

class MainHandler extends Handler {

    GLCameraActivity context;

    protected MainHandler(GLCameraActivity context) {
        super();
        this.context = context;
    }

    @Override
    public void handleMessage (Message msg) {

        switch (msg.what)
        {
            case GLCameraActivity.MSG_START_PREVIEW_REQUESTED:
            // Preview is frozen - need to restart preview
            {
                Util.debug(context.getBtnShoot().isEnabled() + "");
                Util.assertation(context.getCameraState() == GLCameraActivity.CameraState.FROZEN);
                Util.debug();

                // TODO - nezd meg hogy tenyleg elofordulhat az,
                // hogy a camera felszabaditasa utan ez meg elojon
                context.setBtnShootEnable(true);
                context.restartPreview();
                Util.debug(context.getBtnShoot().isEnabled() + "");
                break;
            }

            default:
                super.handleMessage(msg);
        }
    };
}
