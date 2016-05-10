package polcz.peter.hf5;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import polcz.peter.hf5.services.ImageDownloader;
import polcz.peter.hf5.services.ImageDownloader.ImageLoaderListener;
import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class DowloaderActivity extends Activity implements View.OnClickListener {

    private Button download, downloadBG, save;
    private ImageView img;
    private ProgressBar pb;
    private EditText etUrl;
    private TextView percent;
    private ImageDownloader mDownloader;
    private static Bitmap bmp;
    private FileOutputStream fos;

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_downloader);
        initViews();

    }

    /*--- initialize layout components ---*/
    private void initViews () {

        download = (Button) findViewById(R.id.btnDownload);
        downloadBG = (Button) findViewById(R.id.btn_download);
        save = (Button) findViewById(R.id.btn_save);
        /*--- we are using 'this' because our class implements the OnClickListener ---*/
        download.setOnClickListener(this);
        downloadBG.setOnClickListener(this);
        save.setOnClickListener(this);
        save.setEnabled(false);
        img = (ImageView) findViewById(R.id.image);
        img.setScaleType(ScaleType.CENTER_CROP);
        pb = (ProgressBar) findViewById(R.id.pb_download);
        pb.setVisibility(View.INVISIBLE);
        etUrl = (EditText) findViewById(R.id.et_url);
        percent = (TextView) findViewById(R.id.tvPercent);
        percent.setVisibility(View.INVISIBLE);
        
        etUrl.setText("http://www.tutorial.hu/photoshop/3dhatasukepkivagas/kki_12.jpg");
    }

    @Override
    public void onClick (View v) {
        /*--- determine which button was clicked ---*/
        switch (v.getId()) {

            case R.id.btnDownload:

                /*--- we use trim() to remove whitespaces which could be entered ---*/
                if (etUrl.getText().toString().trim().length() > 0) {
                    bmp = ImageDownloader.getBitmapFromURL(etUrl
                            .getText().toString().trim());
                    img.setImageBitmap(bmp);
                    save.setEnabled(true);
                }

                break;

            case R.id.btn_download:

                /*--- check whether there is some Text entered ---*/
                if (etUrl.getText().toString().trim().length() > 0) {
                    /*--- instantiate our downloader passing it required components ---*/
                    mDownloader = new ImageDownloader(etUrl.getText().toString()
                            .trim(), pb, save, img, percent, DowloaderActivity.this, bmp, new ImageLoaderListener() {
                        @Override
                        public void onImageDownloaded (Bitmap bmp) {
                            DowloaderActivity.bmp = bmp;
                            /*--- here we assign the value of bmp field in our Loader class 
                             * to the bmp field of the current class ---*/
                        }
                    });

                    /*--- we need to call execute() since nothing will happen otherwise ---*/
                    mDownloader.execute();

                }

                break;

            case R.id.btn_save:
                /*--- we do not need to pass our bitmap to this method 
                 * since it's our class field and already initialized at this point*/

                saveImageToSD();

                break;

        }

    }

    private void saveImageToSD () {

        /*--- this method will save your downloaded image to SD card ---*/

        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        /*--- you can select your preferred CompressFormat and quality. 
         * I'm going to use JPEG and 100% quality ---*/
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        /*--- create a new file on SD card ---*/
        File file = new File(Environment.getExternalStorageDirectory()
                + File.separator + "myDownloadedImage.jpg");
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        /*--- create a new FileOutputStream and write bytes to file ---*/
        try {
            fos = new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            fos.write(bytes.toByteArray());
            fos.close();
            Toast.makeText(this, "Image saved", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
