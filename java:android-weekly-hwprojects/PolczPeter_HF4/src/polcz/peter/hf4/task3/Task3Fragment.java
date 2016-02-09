package polcz.peter.hf4.task3;

import java.io.File;

import polcz.peter.hf4.R;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

@TargetApi(Build.VERSION_CODES.GINGERBREAD)
public class Task3Fragment extends Fragment {
    private static final int REQUEST_GALLERY = 123;
    private static final int REQUEST_CAMERA = 125;
    private static final String SAVE_BITMAP = "savebitmap";

    private ImageView iw;
    private Button gb;
    private Button cb;
    private int resolution = 100;

    @Override
    public void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_task_3, container, false);
        
        iw = (ImageView) view.findViewById(R.id.iw);
        gb = (Button) view.findViewById(R.id.gallery);
        cb = (Button) view.findViewById(R.id.camera);

        gb.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick (View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                
                // intent.setAction(Intent.ACTION_PICK);
                intent.setAction(Intent.ACTION_GET_CONTENT);

                startActivityForResult(intent, REQUEST_GALLERY);
            }
        });

        cb.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick (View v) {
                // create Intent to take a picture and return control to the calling application
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, REQUEST_CAMERA);
            }
        });
        
        return view;
    }
    
    @Override
    public void onActivityResult (int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK && data != null) {
            switch (requestCode) {
                case 0:
                    break;

                // Receive data from gallery
                // --
                case REQUEST_GALLERY: {
                    try { // for safety's sake
                        Uri uri = data.getData();
                        String filemanagerPath = uri.getPath();
                        String selectedImagePath = getPath(uri);
                        Bitmap photo = getPreview(selectedImagePath != null ? selectedImagePath : filemanagerPath);
                        iw.setImageBitmap(photo);
                    } catch (Exception e) {
                        System.out.println("Image capturing failed");
                    }
                    break;
                }

                // Capture bitmap from camera
                // --
                case REQUEST_CAMERA: {
                    try { // for safety's sake
                        Bitmap capture = data.getParcelableExtra("data");
                        iw.setImageBitmap(capture);
                    } catch (Exception e) {
                        System.out.println("Image capturing failed");
                    }
                    break;
                }
            }
        } /* end-if RESULT_OK */
    }

    @Override
    public void onSaveInstanceState (Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);

        try {
            savedInstanceState.putParcelable(SAVE_BITMAP, ((BitmapDrawable) iw.getDrawable()).getBitmap());
        } catch (NullPointerException e) {}
    }

    @Override
    public void onActivityCreated (Bundle savedInstanceState) {
        /* Restore UI state from the savedInstanceState.
         * This bundle has also been passed to onCreate. */

        if (savedInstanceState != null) {
            Bitmap bitmap = savedInstanceState.getParcelable(SAVE_BITMAP);
            Log.d("STATE-RESTORE", "bitmap created");
            iw.setImageBitmap(bitmap);
            Log.d("RESTORING...", "onRestoreInstanceState()");
        } else {
            Log.d("SavedInstanceState", "null");
        }

        super.onActivityCreated(savedInstanceState);
    }
    
    public String getPath (Uri uri) {
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = getActivity().getContentResolver().query(uri, projection, null, null, null);
        if (cursor != null) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } else return null;
    }

    public Bitmap getPreview (String fileName) {
        Toast.makeText(getActivity(), fileName, Toast.LENGTH_LONG).show();
        File image = new File(fileName);

        BitmapFactory.Options bounds = new BitmapFactory.Options();
        bounds.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(image.getPath(), bounds);

        if ((bounds.outWidth == -1) || (bounds.outHeight == -1)) { return null; }
        int originalSize = (bounds.outHeight > bounds.outWidth) ? bounds.outHeight : bounds.outWidth;
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inSampleSize = originalSize / resolution ;
        return BitmapFactory.decodeFile(image.getPath(), opts);
    }
}