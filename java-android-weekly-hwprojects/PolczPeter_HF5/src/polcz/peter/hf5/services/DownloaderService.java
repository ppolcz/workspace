package polcz.peter.hf5.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class DownloaderService extends Service {

    @Override
    public void onCreate () {
        // TODO Auto-generated method stub
        super.onCreate();
    }

    @Override
    public IBinder onBind (Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }
   
    @Override
    public int onStartCommand (Intent intent, int flags, int startId) {
        // TODO Auto-generated method stub
        return super.onStartCommand(intent, flags, startId);
    }

}
