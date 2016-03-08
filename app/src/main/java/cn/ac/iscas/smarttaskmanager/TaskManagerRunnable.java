package cn.ac.iscas.smarttaskmanager;

import android.app.ActivityManager;
import android.util.Log;

import java.util.List;

/**
 * Created by chenh on 3/7/16.
 */
public class TaskManagerRunnable implements Runnable {

    private static final String LOG_TAG = TaskManagerRunnable.class.getSimpleName();

    private static final int CHECK_INTERVAL = 1000;

    private static final double LOW_MEMORY_THRESHOLD_RATIO = 1.1;

    private ActivityManager am;

    public TaskManagerRunnable(ActivityManager am){
        this.am = am;
    }

    @Override
    public void run() {
        while(true){
            try {
                work();
                Thread.sleep(CHECK_INTERVAL);
            } catch (InterruptedException e) {
                Log.e(LOG_TAG, "", e);
                break;
            }
        }
    }

    private void work(){
        try {
            getAndSaveCurrentApps();
            if(shouldClearMemory()){
                //kill()
            }
        } catch (Exception e) {
            Log.e(LOG_TAG, "Unknown error", e);
        }
    }

    private boolean shouldClearMemory(){
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        this.am.getMemoryInfo(memoryInfo);
        Log.d(LOG_TAG, String.format("Available: %d, Threshold: %d", memoryInfo.availMem, memoryInfo.threshold));
        return memoryInfo.availMem < memoryInfo.threshold * LOW_MEMORY_THRESHOLD_RATIO;
    }

    private void getAndSaveCurrentApps(){
        List<ActivityManager.RunningAppProcessInfo> apps = this.am.getRunningAppProcesses();
    }
}
