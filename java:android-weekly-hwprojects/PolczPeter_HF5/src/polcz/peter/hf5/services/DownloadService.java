package polcz.peter.hf5.services;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.app.Activity;
import android.app.IntentService;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

public class DownloadService extends IntentService {

    private static final String TAG = DownloadService.class.getSimpleName();

    private int result = Activity.RESULT_CANCELED;
    public static final String KEY_URL = "urlpath";
    public static final String KEY_FILENAME = "filename";
    public static final String KEY_FILEPATH = "filepath";
    public static final String KEY_RESULT = "result";
    public static final String KEY_BITMAP = "bitmap";
    public static final String NOTIFICATION = "polcz.peter.hf5.MainActivity.BroadcastReceiver";

    private int previewresolution = 100;

    private Bitmap myBitmap;

    public DownloadService() {
        super("DownloadService");
    }

    // Will be called asynchronously by Android
    @Override
    protected void onHandleIntent (Intent intent) {
        String urlPath = intent.getStringExtra(KEY_URL);
        String fileName = intent.getStringExtra(KEY_FILENAME);
        File output = new File(Environment.getExternalStorageDirectory(), fileName);
        if (output.exists()) {
            output.delete();
        }

        InputStream stream = null;
        FileOutputStream fos = null;
        try {
            
            Log.v(TAG, "onHandleIntent - try-on belul");

            URL url = new URL(urlPath);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            myBitmap = BitmapFactory.decodeStream(input);

            // Successful finished
            result = Activity.RESULT_OK;

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        publishResults(output.getAbsolutePath(), result);
    }

    private void publishResults (String outputPath, int result) {
        Intent intent = new Intent(NOTIFICATION);
        intent.putExtra(KEY_FILEPATH, outputPath);
        intent.putExtra(KEY_RESULT, result);
        intent.putExtra(KEY_BITMAP, myBitmap);

        sendBroadcast(intent);
    }

    public Bitmap getPreview (String fileName) {
        Toast.makeText(this, fileName, Toast.LENGTH_LONG).show();
        File image = new File(fileName);

        BitmapFactory.Options bounds = new BitmapFactory.Options();
        bounds.inJustDecodeBounds = true;

        Log.v(TAG, image.getPath());

        BitmapFactory.decodeFile(image.getPath(), bounds);

        if ((bounds.outWidth == -1) || (bounds.outHeight == -1)) {
            Log.v(TAG, String.format("width = %d  height = %d", bounds.outWidth, bounds.outHeight));
            return null;
        }
        int originalSize = (bounds.outHeight > bounds.outWidth) ? bounds.outHeight : bounds.outWidth;
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inSampleSize = originalSize / previewresolution;
        return BitmapFactory.decodeFile(image.getPath(), opts);
    }
}
