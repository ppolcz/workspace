package com.polpe.panogl.main.camera;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

import org.openpanodroid.panoutils.android.CubicPanoNative;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.widget.Toast;

import com.polpe.panogl.R;
import com.polpe.panogl.main.gallery.GalleryListActivity;
import com.polpe.panogl.util.GlobalConstants;
import com.polpe.panogl.util.Util;

@TargetApi(Build.VERSION_CODES.GINGERBREAD)
public class PanoGLCameraActivity extends Activity implements OnClickListener, SurfaceHolder.Callback {
    protected static final String TAG = PanoGLCameraActivity.class.getSimpleName();
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
    private PanoGLSurfaceView glw;
    private SurfaceView sw;
    private View btnView;

    private PanoGLVortexRenderer renderer;

    private CubicPanoNative cubicPano;
    Bitmap front, back, top, bottom, left, right;

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
        @Override
        public void run () {
            synchronized (this) {
                try {
                    pictureTakenLock.block(); // Samsung
                    wait(TIMER_DELAY_MSEC);
                    handler.sendEmptyMessage(MSG_START_PREVIEW_REQUESTED);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    pictureTakenLock.close();
                }
            }
        }
    }

    public PanoGLCameraActivity() {
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

        // kezdetben mind a 6 kep ures (atlatszo)
        try {
            front = BitmapFactory.decodeResource(getResources(), R.drawable.empty_side_boundaries_small);
            back = BitmapFactory.decodeResource(getResources(), R.drawable.empty_side_boundaries_small);
            top = BitmapFactory.decodeResource(getResources(), R.drawable.empty_side_boundaries_small);
            bottom = BitmapFactory.decodeResource(getResources(), R.drawable.empty_side_boundaries_small);
            left = BitmapFactory.decodeResource(getResources(), R.drawable.empty_side_boundaries_small);
            right = BitmapFactory.decodeResource(getResources(), R.drawable.empty_side_boundaries_small);
            cubicPano = new CubicPanoNative(front, back, top, bottom, left, right);
        } catch (OutOfMemoryError e) {
            Toast.makeText(getApplicationContext(), "Memory full, please try again a few seconds later", Toast.LENGTH_LONG).show();
            System.gc();
            finish();
        }

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

        // _____________________________________________________________
        // Creating a translucent OpenGL ES SurfaceView
        glw = new PanoGLSurfaceView(this, true);
        glw.setZOrderMediaOverlay(true);

        // We want an 8888 pixel format because that's required for a translucent window.
        // And we want a depth buffer.
        // IMPORTANT - This method can only be called before setRenderer() is called.
        glw.setEGLConfigChooser(8, 8, 8, 8, 16, 0);
        // glw.setEGLConfigChooser(false);

        renderer = new PanoGLVortexRenderer(glw, cubicPano);

        // Tell the cube renderer that we want to render a translucent version of the cube:
        glw.setRenderer(renderer);
        // glw.setRenderer(new CubeRenderer(this, true));

        // Setting how frequent is the onDrawFrame() method called.
        // RENDERMODE_CONTINUOUSLY - call continuously with a specific time delay between calls.
        // RENDERMODE_WHEN_DIRTY - the renderer only renders when the surface is created, or when requestRender() is called.
        // IMPORTANT - This method can only be called after setRenderer() is called.
        glw.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);

        // Use a surface format with an Alpha channel:
        glw.getHolder().setFormat(PixelFormat.RGBA_8888);
        glw.getHolder().setFormat(PixelFormat.TRANSLUCENT);

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
        fl.addView(sw);
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
    }

    @Override
    protected void onResume () {
        Util.debug();

        super.onResume();
        glw.onResume();
        // ----------------
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            camera = Camera.open(CAMERA_ID);
        } else {
            camera = Camera.open();
        }
        cameraState = CameraState.AVAILABLE;
    }

    @Override
    protected void onPause () {
        super.onPause();

        Util.debug();

        glw.onPause();

        if (camera != null) {
            // camera: Null
            camera.release();
            camera = null;
        }
    }

    @Override
    protected void onDestroy () {
        Util.debug();

        if (cubicPano != null) cubicPano.onDestroy();

        if (front != null && !front.isRecycled()) front.recycle();
        if (back != null && !back.isRecycled()) back.recycle();
        if (top != null && !top.isRecycled()) top.recycle();
        if (bottom != null && !bottom.isRecycled()) bottom.recycle();
        if (left != null && !left.isRecycled()) left.recycle();
        if (right != null && !right.isRecycled()) right.recycle();
        System.gc();

        super.onDestroy();
    }

    @Override
    // OnClickListener.~
    public void onClick (View view) {
        switch (view.getId())
        {
            case R.id.btn_album:
                finish();
                break;

            case R.id.btn_shoot:
                switch (cameraState)
                {
                    case AVAILABLE:
                        // from now on the user cannot push the btnShoot
                        btnShoot.setEnabled(false);

                        // float lat = renderer.getRotationLatitudeDeg();
                        // float lon = renderer.getRotationLongitudeDeg();
                        // float azimuth = renderer.getRotationZDeg();
                        // Util.debug("(" + lat + ", " + lon + ")  " + azimuth);

                        cameraState = CameraState.FROZEN;
                        camera.takePicture(shutterCallback, rawCallback, jpegCallback);
                        new TimerThread().start();
                        break;

                    case FROZEN:
                        break;

                    default:
                        break;
                }
                break;
                
            case R.id.btn_settings:
                renderer.setAutoRotate(!renderer.getAutoRotate());
                glw.sensorNeeded(renderer.getAutoRotate());
                break;

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
            camera.setPreviewDisplay(holder);
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
        Util.debug();
        camera.startPreview();
    }

    @Override
    public void surfaceDestroyed (SurfaceHolder holder) {
        Util.debug();

        try {
            camera.stopPreview();
            camera.release();
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
        public void onPictureTaken (byte[] data, Camera camera) {
            // Bitmap bmp = BitmapFactory.decodeByteArray(data, 0, data.length);
            // ImageView iw = new ImageView(PanoGLCameraActivity.this);
            // iw.setImageBitmap(bmp);
            // iw.setLayoutParams(new LayoutParams(MATCH_PARENT, MATCH_PARENT));
            // fl.addView(iw);
            //
            // renderer.onPictureTaken(bmp);
            // TODO
        }
    };

    /** Handles data for jpeg picture */
    PictureCallback jpegCallback = new PictureCallback() {
        public void onPictureTaken (byte[] data, Camera camera) {
            try {
                String fname = createNewImage();

                // write to sdcard
                FileOutputStream outStream = new FileOutputStream(fname);
                outStream.write(data);
                outStream.close();

                renderer.onPictureTaken(fname);

                float[] coords = renderer.getScreenCoordinates();

                PrintWriter pw = new PrintWriter(fname.replace(GlobalConstants.FILE_IMAGE_EXTENSION, GlobalConstants.FILE_TEXT_EXTENSION));
                for (int i = 0; i < coords.length; ++i) {
                    pw.write(coords[i] + "\n");
                }
                pw.close();

                {
                    float coordinates[] = new float[16];
                    BufferedReader reader = null;

                    try {
                        reader = new BufferedReader(new FileReader(fname.replace(GlobalConstants.FILE_IMAGE_EXTENSION, GlobalConstants.FILE_TEXT_EXTENSION)));
                        String text = null;

                        int i = 0;
                        while ((text = reader.readLine()) != null) {
                            coordinates[i++] = Float.parseFloat(text);
                        }
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        try {
                            if (reader != null) {
                                reader.close();
                            }
                        } catch (IOException e) {}
                    }

                    Util.debug(coordinates[4] + " " + coordinates[14] + " " + coordinates[15]);
                }

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

    CameraState getCameraState () {
        return cameraState;
    }

    void restartPreview () {
        camera.startPreview();
        Util.debug("camera.startPreview()");
        cameraState = CameraState.AVAILABLE;
        btnShoot.setEnabled(true);
    }
}

class MainHandler extends Handler {

    PanoGLCameraActivity context;

    protected MainHandler(PanoGLCameraActivity context) {
        super();
        this.context = context;
    }

    @Override
    public void handleMessage (Message msg) {

        switch (msg.what)
        {
            case PanoGLCameraActivity.MSG_START_PREVIEW_REQUESTED:
                // Preview is frozen - need to restart preview
                Util.assertation(context.getCameraState() == PanoGLCameraActivity.CameraState.FROZEN);
                context.restartPreview();
                break;

            default:
                super.handleMessage(msg);
        }
    };
}
