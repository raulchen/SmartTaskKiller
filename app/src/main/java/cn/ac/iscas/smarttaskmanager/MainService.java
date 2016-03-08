package cn.ac.iscas.smarttaskmanager;

import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class MainService extends Service {

    private static final String LOG_TAG = MainService.class.getSimpleName();

    public MainService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(LOG_TAG, "service started.");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        ActivityManager am = (ActivityManager)getSystemService(Context.ACTIVITY_SERVICE);
        new Thread(new TaskManagerRunnable(am)).start();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
