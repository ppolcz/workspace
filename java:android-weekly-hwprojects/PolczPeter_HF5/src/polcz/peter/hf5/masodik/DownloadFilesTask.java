package polcz.peter.hf5.masodik;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;

import android.os.AsyncTask;

public class DownloadFilesTask extends AsyncTask<URL, Integer, Long> {
    
    private long length;
    
    protected Long doInBackground (URL ... urls) {

        if (urls.length == 0) return (long) 0; 
        
        URL url = urls[0];

        try {
            URLConnection connection;
            connection = url.openConnection();
            final long length = connection.getContentLength();
            this.length = length;
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        return (long) length;
    }

    @Override
    protected void onPreExecute () {

        super.onPreExecute();
    }

    protected void onProgressUpdate (Integer ... progress) {
        // setProgressPercent(progress[0]);
    }

    protected void onPostExecute (Long result) {
    }
}
