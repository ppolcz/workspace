package com.polpe.panogl.demo.view;

import java.io.ByteArrayOutputStream;
import java.io.File;

import junit.framework.Assert;

import org.openpanodroid.panoutils.android.CubicPanoNative;
import org.openpanodroid.panoutils.android.CubicPanoNative.TextureFaces;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.opengl.GLSurfaceView;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.polpe.panogl.R;
import com.polpe.panogl.util.GlobalConstants;
import com.polpe.panogl.util.Util;

public class PanoViewerActivity extends Activity implements OnClickListener {
    protected static final String TAG = "panogl.shoot.PanoViewerActivity";

    private static final String FRONT_BITMAP_KEY = "frontBitmap";
    private static final String BACK_BITMAP_KEY = "backBitmap";
    private static final String TOP_BITMAP_KEY = "topBitmap";
    private static final String BOTTOM_BITMAP_KEY = "bottomBitmap";
    private static final String LEFT_BITMAP_KEY = "leftBitmap";
    private static final String RIGHT_BITMAP_KEY = "rightBitmap";

    private static int IMG_QUALITY = 85;

    private boolean activityCreated = false;
    private boolean stateSaved;

    private FrameLayout fl;
    private View btnView;
    private PanodroidGLView glView;

    // Panodroid
    private Bitmap panoSampleBmp;

    // Buttons
    private Button btnShoot;
    private Button btnSettings;
    private Button btnAlbum;

    protected Uri panoUri;

    private CubicPanoNative cubicPano;
    private PanoConversionTask panoConversionTask;

    private String dirname;

    private Bitmap loadBitmapFromFile (String filename) {
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            Bitmap bmp = BitmapFactory.decodeFile(filename, options);
            return bmp;
        } catch (Throwable ex) {
            ex.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        dirname = intent.getStringExtra(GlobalConstants.KEY_PANORAMA_NAME);

        // debug reasons
        if (dirname == null || dirname.length() < 2) {
            dirname = GlobalConstants.DIR_ROOT + "/demo";
            File dir = new File(dirname);
            if (!dir.exists()) dir.mkdirs();
        }

        try {
            panoSampleBmp = BitmapFactory.decodeResource(getResources(), R.drawable.panosample);
        } catch (OutOfMemoryError ex) {
            Toast.makeText(getApplicationContext(), "There is not enough memory, loading small image!", Toast.LENGTH_SHORT).show();
            System.gc();
            try {
                panoSampleBmp = BitmapFactory.decodeResource(getResources(), R.drawable.panosample_small);
            } catch (OutOfMemoryError ex2) {
                Toast.makeText(getApplicationContext(), "There is not enough memory, please try again later!", Toast.LENGTH_SHORT).show();
                System.gc();
                finish();
            }
        }
        // _____________________________________________________________
        // Set some window parameters
        // make the application fullScreen
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // Window flag: as long as this window is visible to the user, keep the device's screen turned on and bright.
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        // ===========================================================================================
        // CREATING VIEWER LAYOUT

        // _____________________________________________________________
        // Creating a translucent OpenGL ES SurfaceView - Panodroid

        // decoding resource - sample pano image
        // panoSampleBmp = BitmapFactory.decodeResource(getResources(), R.drawable.panosample);
        // panoSampleBmp = BitmapFactory.decodeResource(getResources(), R.drawable.launcher_pano);

        // constructor using fields
        // panoViewer = new PolpePanoViewer(this, panoSampleBmp);

        // if there is a save state
        // --- load the 6 saved facet images
        // --- setupOpenGLView()
        // --- |-- glView = new PanodroidGLView(context, cubicPano);
        // --- |-- --- setEGLConfigChooser(8, 8, 8, 8, 16, 0);
        // --- |-- --- setRenderer(new PanodroidVortexRenderer(this, pano))
        // --- |-- --- setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
        // --- |-- --- getHolder().setFormat(PixelFormat.RGBA_8888);
        // --- |-- --- getHolder().setFormat(PixelFormat.TRANSLUCENT);
        // --- |-- --- setZOrderMediaOverlay(false);
        // --- |-- --- setZOrderOnTop(true);
        // --- |-- --- setLayoutParams(new LayoutParams(MATCH_PARENT, MATCH_PARENT));
        // else
        // --- convertCubicPano()
        // --- --- new PanoConversionTask().execute(pano)
        // --- --- --- onPreExecute() - waitDialog
        // --- --- --- onPostExecute() - setupOpenGLView() ...

        // _____________________________________________________________
        // Inflating camera_buttons.xml
        btnView = getLayoutInflater().inflate(R.layout.camera_buttons, null);

        // _____________________________________________________________
        // Building a FrameLayout
        fl = new FrameLayout(this);
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
        onViewerCreate(savedInstanceState);
    }

    @Override
    protected void onResume () {
        super.onResume();

        if (glView != null) {
            glView.onResume();
        }
    }

    @Override
    protected void onPause () {
        super.onPause();

        if (glView != null) {
            glView.onPause();
        }
    }

    @Override
    protected void onDestroy () {
        if (panoConversionTask != null) {
            panoConversionTask.destroy();
        }

        // We might have used a lot of memory.
        // Explicitly free it now.

        if (cubicPano != null && !stateSaved) {
            cubicPano.getFace(TextureFaces.front).recycle();
            cubicPano.getFace(TextureFaces.back).recycle();
            cubicPano.getFace(TextureFaces.top).recycle();
            cubicPano.getFace(TextureFaces.bottom).recycle();
            cubicPano.getFace(TextureFaces.left).recycle();
            cubicPano.getFace(TextureFaces.right).recycle();
            cubicPano = null;
            System.gc();
        }

        if (panoSampleBmp != null && !panoSampleBmp.isRecycled()) {
            panoSampleBmp.recycle();
            System.gc();
        }

        super.onDestroy();
    }

    private void addGLSurfaceView (GLSurfaceView glSurfaceView) {
        fl.addView(glSurfaceView);
        fl.addView(btnView);
    }

    private boolean activityCreated () {
        return activityCreated;
    }

    @Override
    // OnClickListener.~
    public void onClick (View view) {
        switch (view.getId())
        {
            case R.id.btn_album:
            {
                break;
            }
            case R.id.btn_shoot:
            {
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

    private class ClickListenerErrorDialog implements DialogInterface.OnClickListener {
        @Override
        public void onClick (DialogInterface dialog, int which) {
            // After an (fatal) error dialog, the activity will be dismissed.
            PanoViewerActivity.this.finish();
        }
    }

    /**
     * This task converts the panorama image (2:1) into 6 images
     * corresponding to the 6 sides of the textured cube.
     */
    private class PanoConversionTask extends AsyncTask<Bitmap, Integer, CubicPanoNative> {

        private ProgressDialog waitDialog = null;
        private int textureSize;
        private boolean destroyed = false;

        public PanoConversionTask(int textureSize) {
            this.textureSize = textureSize;
        }

        @Override
        protected void onPreExecute () {
            waitDialog = new ProgressDialog(PanoViewerActivity.this);
            waitDialog.setMessage(PanoViewerActivity.this.getString(R.string.convertingPanoImage));
            waitDialog.setCancelable(false);
            waitDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            waitDialog.setButton(ProgressDialog.BUTTON_NEGATIVE, PanoViewerActivity.this.getText(R.string.cancel), new DialogInterface.OnClickListener() {
                public void onClick (DialogInterface dialog, int id) {
                    cancel(true);
                }
            });
            waitDialog.setMax(6);
            waitDialog.show();
        }

        @Override
        protected CubicPanoNative doInBackground (Bitmap ... params) {
            Bitmap bmp;

            if (isCancelled()) { return null; }

            Bitmap front;
            // front = loadBitmapFromFile(dirname + "/" + GlobalConstants.FILENAME_FRONT);
            // if (front == null) {
            bmp = CubicPanoNative.getCubeSide(panoSampleBmp, TextureFaces.front, textureSize);
            if (bmp == null) { return null; }
            front = createPurgableBitmap(bmp, dirname + "/" + GlobalConstants.FILENAME_FRONT);
            bmp.recycle();
            // }
            publishProgress(1);

            if (isCancelled()) { return null; }

            Bitmap back;
            // back = loadBitmapFromFile(dirname + "/" + GlobalConstants.FILENAME_BACK);
            // if (back == null) {
            bmp = CubicPanoNative.getCubeSide(panoSampleBmp, TextureFaces.back, textureSize);
            if (bmp == null) { return null; }
            back = createPurgableBitmap(bmp, dirname + "/" + GlobalConstants.FILENAME_BACK);
            bmp.recycle();
            // }
            publishProgress(2);

            if (isCancelled()) { return null; }

            Bitmap top;
            // top = loadBitmapFromFile(dirname + "/" + GlobalConstants.FILENAME_TOP);
            // if (top == null) {
            bmp = CubicPanoNative.getCubeSide(panoSampleBmp, TextureFaces.top, textureSize);
            if (bmp == null) { return null; }
            top = createPurgableBitmap(bmp, dirname + "/" + GlobalConstants.FILENAME_TOP);
            bmp.recycle();
            // }
            publishProgress(3);

            if (isCancelled()) { return null; }

            Bitmap bottom;
            // bottom = loadBitmapFromFile(dirname + "/" + GlobalConstants.FILENAME_BOTTOM);
            // if (bottom == null) {
            bmp = CubicPanoNative.getCubeSide(panoSampleBmp, TextureFaces.bottom, textureSize);
            if (bmp == null) { return null; }
            bottom = createPurgableBitmap(bmp, dirname + "/" + GlobalConstants.FILENAME_BOTTOM);
            bmp.recycle();
            // }
            publishProgress(4);

            if (isCancelled()) { return null; }

            Bitmap right;
            // right = loadBitmapFromFile(dirname + "/" + GlobalConstants.FILENAME_RIGHT);
            // if (right == null) {
            bmp = CubicPanoNative.getCubeSide(panoSampleBmp, TextureFaces.right, textureSize);
            if (bmp == null) { return null; }
            right = createPurgableBitmap(bmp, dirname + "/" + GlobalConstants.FILENAME_RIGHT);
            bmp.recycle();
            // }
            publishProgress(5);

            if (isCancelled()) { return null; }

            Bitmap left;
            // left = loadBitmapFromFile(dirname + "/" + GlobalConstants.FILENAME_LEFT);
            // if (left == null) {
            bmp = CubicPanoNative.getCubeSide(panoSampleBmp, TextureFaces.left, textureSize);
            if (bmp == null) { return null; }
            left = createPurgableBitmap(bmp, dirname + "/" + GlobalConstants.FILENAME_LEFT);
            bmp.recycle();
            // }
            publishProgress(6);

            CubicPanoNative cubic = new CubicPanoNative(front, back, top, bottom, left, right);

            return cubic;
        }

        // TODO - itt belepiszkaltam - itt menti le a kepet
        private Bitmap createPurgableBitmap (Bitmap original, String fname) {
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            original.compress(Bitmap.CompressFormat.JPEG, IMG_QUALITY, os);
            byte[] imgDataCompressed = os.toByteArray();

            // try {
            // FileOutputStream outStream;
            // outStream = new FileOutputStream(fname);
            // outStream.write(imgDataCompressed);
            // outStream.close();
            // } catch (FileNotFoundException e) {
            // e.printStackTrace();
            // } catch (IOException e) {
            // e.printStackTrace();
            // }

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inDither = false;
            // When we run out of memory, the decompressed bitmap can be purged.
            // If it is re-accessed, the byte array will be decompressed again.
            // This allows us to handle larger or more bitmaps.
            options.inPurgeable = true;
            // Original byte array will not be altered anymore. --> Can make a shallow reference.
            options.inInputShareable = true;
            Bitmap compressedBitmap = BitmapFactory.decodeByteArray(imgDataCompressed, 0, imgDataCompressed.length, options);

            return compressedBitmap;
        }

        synchronized boolean isDestroyed () {
            return destroyed;
        }

        synchronized void destroy () {
            destroyed = true;
            cancel(true);
        }

        @Override
        protected void onCancelled () {
            if (isDestroyed()) { return; }

            waitDialog.dismiss();
            panoSampleBmp.recycle();
            PanoViewerActivity.this.finish();
        }

        @Override
        protected void onPostExecute (CubicPanoNative result) {
            if (isDestroyed()) { return; }

            waitDialog.dismiss();
            panoSampleBmp.recycle();
            panoSampleBmp = null;

            if (result == null) {
                Util.showAlert(PanoViewerActivity.this, null, PanoViewerActivity.this.getString(R.string.convertingPanoImage), new ClickListenerErrorDialog());
            } else {
                cubicPano = result;
                setupOpenGLView();
                panoDisplaySetupFinished();
            }
        }

        @Override
        protected void onProgressUpdate (Integer ... progress) {
            Assert.assertTrue(progress.length > 0);
            int p = progress[0];
            waitDialog.setProgress(p);
        }
    }

    protected void panoDisplaySetupFinished () {}

    /** Called when the activity is first created. */
    public void onViewerCreate (Bundle savedInstanceState) {
        Log.i(TAG, "Creating");

        stateSaved = false;

        Bitmap front, back, top, bottom, left, right;
        front = back = top = bottom = left = right = null;

        if (savedInstanceState != null) {
            Parcelable parcelData;
            parcelData = savedInstanceState.getParcelable(FRONT_BITMAP_KEY);
            if (parcelData != null) {
                Assert.assertTrue(parcelData instanceof Bitmap);
                front = (Bitmap) parcelData;
            }

            parcelData = savedInstanceState.getParcelable(BACK_BITMAP_KEY);
            if (parcelData != null) {
                Assert.assertTrue(parcelData instanceof Bitmap);
                back = (Bitmap) parcelData;
            }

            parcelData = savedInstanceState.getParcelable(TOP_BITMAP_KEY);
            if (parcelData != null) {
                Assert.assertTrue(parcelData instanceof Bitmap);
                top = (Bitmap) parcelData;
            }

            parcelData = savedInstanceState.getParcelable(BOTTOM_BITMAP_KEY);
            if (parcelData != null) {
                Assert.assertTrue(parcelData instanceof Bitmap);
                bottom = (Bitmap) parcelData;
            }

            parcelData = savedInstanceState.getParcelable(LEFT_BITMAP_KEY);
            if (parcelData != null) {
                Assert.assertTrue(parcelData instanceof Bitmap);
                left = (Bitmap) parcelData;
            }

            parcelData = savedInstanceState.getParcelable(RIGHT_BITMAP_KEY);
            if (parcelData != null) {
                Assert.assertTrue(parcelData instanceof Bitmap);
                right = (Bitmap) parcelData;
            }
        }

        if (front == null || back == null || top == null || bottom == null || left == null || right == null) {

            if (front != null) {
                front.recycle();
                front = null;
            }
            if (back != null) {
                back.recycle();
                back = null;
            }
            if (top != null) {
                top.recycle();
                top = null;
            }
            if (bottom != null) {
                bottom.recycle();
                bottom = null;
            }
            if (left != null) {
                left.recycle();
                left = null;
            }
            if (right != null) {
                right.recycle();
                right = null;
            }

            convertCubicPano();
        } else {
            cubicPano = new CubicPanoNative(front, back, top, bottom, left, right);
            setupOpenGLView(); // Innen hivodik a setRenderer
        }
    }

    protected void setupOpenGLView () {
        Assert.assertTrue(cubicPano != null);
        glView = new PanodroidGLView(this, cubicPano);

        if (this.activityCreated()) {
            this.addGLSurfaceView(glView);
        }
    }

    /**
     * Innek hivodik meg a PanoConversionTask (AsyncTask),
     * ami meghivja a nativ fuggvenyeket
     */
    private void convertCubicPano () {
        Assert.assertTrue(panoSampleBmp != null);

        Log.i(TAG, "Converting panorama ...");

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String str = prefs.getString("textureSize", "");
        int maxTextureSize = GlobalConstants.DEFAULT_MAX_TEXTURE_SIZE;
        if (!str.equals("")) {
            try {
                maxTextureSize = Integer.parseInt(str);
            } catch (NumberFormatException ex) {
                maxTextureSize = GlobalConstants.DEFAULT_MAX_TEXTURE_SIZE;
            }
        }

        // On the one hand, we don't want to waste memory for textures whose resolution
        // is too large for the device. On the other hand, we want to have a resolution
        // that is high enough to give us good quality on any device. However, we don't
        // know the resolution of the GLView a priori, and it could be resized later.
        // Therefore, we use the display size to calculate the optimal texture size.
        Display display = ((WindowManager) this.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        int width = display.getWidth();
        int height = display.getHeight();
        int maxDisplaySize = width > height ? width : height;

        int optimalTextureSize = getOptimalFaceSize(maxDisplaySize, panoSampleBmp.getWidth(), GlobalConstants.DEFAULT_FOV_DEG);
        int textureSize = toPowerOfTwo(optimalTextureSize);
        textureSize = textureSize <= maxTextureSize ? textureSize : maxTextureSize;

        Log.i(TAG, "Texture size: " + textureSize + " (optimal size was " + optimalTextureSize + ")");

        panoConversionTask = new PanoConversionTask(textureSize);
        panoConversionTask.execute(panoSampleBmp);
    }

    private int toPowerOfTwo (int number) {
        int n_2 = 1;

        while (n_2 < number) {
            n_2 *= 2;
        }

        return n_2;
    }

    protected void onViewerSaveInstanceState (Bundle outState) {
        Log.i(TAG, "Saving instance state.");

        if (cubicPano != null) {
            outState.putParcelable(FRONT_BITMAP_KEY, cubicPano.getFace(TextureFaces.front));
            outState.putParcelable(BACK_BITMAP_KEY, cubicPano.getFace(TextureFaces.back));
            outState.putParcelable(TOP_BITMAP_KEY, cubicPano.getFace(TextureFaces.top));
            outState.putParcelable(BOTTOM_BITMAP_KEY, cubicPano.getFace(TextureFaces.bottom));
            outState.putParcelable(LEFT_BITMAP_KEY, cubicPano.getFace(TextureFaces.left));
            outState.putParcelable(RIGHT_BITMAP_KEY, cubicPano.getFace(TextureFaces.right));
            stateSaved = true;
        }
    }

    public static int getOptimalFaceSize (int screenSize, int equirectImgSize, double hfov) {
        // Maximum possible size with this equirectangular image.
        int maxFaceSize = (int) (0.25 * equirectImgSize * 90.0 / hfov + 0.5);

        // Optimal face size for this screen size.
        int optimalFaceSize = (int) (90.0 / hfov * screenSize + 0.5);

        return (optimalFaceSize < maxFaceSize ? optimalFaceSize : maxFaceSize);
    }

    public static int getOptimalEquirectSize (int screenSize, double hfov) {
        int optimalEquirectSize = (int) (360.0 / hfov * screenSize + 0.5);
        return optimalEquirectSize;
    }
}
