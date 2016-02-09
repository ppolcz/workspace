package polcz.peter.hf5.masodik;

import java.net.URL;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

public class MediaPlayerActivity extends Activity {

    LinearLayout ll;
    private LayoutParams llChildParams;

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ll = new LinearLayout(this);
        ll.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        setContentView(ll);

        llChildParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        llChildParams.setMargins(0, 5, 0, 0);

        Bundle bundle = getIntent().getExtras();
        ArrayListParcelable mediaTypes = bundle.getParcelable(MainActivity.ID_MEDIA_TYPE);
        ArrayListParcelable mediaUrls = bundle.getParcelable(MainActivity.ID_MEDIA_URL);

        for (int i = 0; i < mediaTypes.size(); ++i) {
            String type = mediaTypes.get(i);
            String url = mediaUrls.get(i);
            switch (MainActivity.MimeTypes.valueOf(type)) {
                case IMAGE:
                    break;

                case AUDIO:
                    break;

                case VIDEO:
                    break;
            }
        }
    }

    private class DownloadFilesTask extends AsyncTask<URL, Integer, Long> {
        protected Long doInBackground (URL ... urls) {
            int count = urls.length;
            long totalSize = 0;
            for (int i = 0; i < count; i++) {
                // totalSize += Downloader.downloadFile(urls[i]);
                publishProgress((int) ((i / (float) count) * 100));
                // Escape early if cancel() is called
                if (isCancelled()) break;
            }
            return totalSize;
        }

        protected void onProgressUpdate (Integer ... progress) {
            // setProgressPercent(progress[0]);
        }

        protected void onPostExecute (Long result) {
            // showDialog("Downloaded " + result + " bytes");
        }
    }

}
