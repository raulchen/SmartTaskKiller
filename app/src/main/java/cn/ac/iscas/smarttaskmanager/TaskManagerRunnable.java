package cn.ac.iscas.smarttaskmanager;

import android.app.ActivityManager;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by chenh on 3/7/16.
 */
public class TaskManagerRunnable implements Runnable {

    private static final String LOG_TAG = TaskManagerRunnable.class.getSimpleName();

    private static final int CHECK_INTERVAL = 10000;

    private static final double LOW_MEMORY_THRESHOLD_RATIO = 1.1;

    private ActivityManager am;

    private RunningApps preRunningApps = null;

    private RunningApps currentRunningApps = null;

    private Map<String, Counter> jointCnt = new HashMap<>();

    private Map<String, Counter> lastAppCnt = new HashMap<>();

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
            checkRunningApps();
            if(checkMemoryUsage()){
                //kill()
            }
        } catch (Exception e) {
            Log.e(LOG_TAG, "Unknown error", e);
        }
    }

    /**
     * @return true if we think memory is low and need killing apps
     */
    private boolean checkMemoryUsage(){
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        this.am.getMemoryInfo(memoryInfo);
        //TODO save
        Log.d(LOG_TAG, String.format("Available: %d, Threshold: %d", memoryInfo.availMem, memoryInfo.threshold));
        return memoryInfo.availMem < memoryInfo.threshold * LOW_MEMORY_THRESHOLD_RATIO;
    }

    private void checkRunningApps(){
        List<ActivityManager.RunningAppProcessInfo> infos = this.am.getRunningAppProcesses();
        if(infos == null || infos.isEmpty()){
            return;
        }
        RunningApps ra = new RunningApps();
        ra.foreground = infos.get(0).processName;
        if(skipApp(ra.foreground)){
            return;
        }
        for(ActivityManager.RunningAppProcessInfo info : infos){
//            if(isService(info.processName)){
//                continue;
//            }
            Log.d(LOG_TAG, info.processName + ", " + info.importance);
            if(!info.processName.equals(ra.foreground)){
                ra.inMemory.add(info.processName);
            }
        }

        if(!ra.equals(currentRunningApps)){
            preRunningApps = currentRunningApps;
            currentRunningApps = ra;
        }
    }

    private boolean skipApp(String app){
        return app.toLowerCase().contains("launcher");
    }

    private boolean isService(String processName){
        for(ActivityManager.RunningServiceInfo info : am.getRunningServices(999)){
            if(info.process.equals(processName)){
                return true;
            }
        }
        return false;
    }

    private void updateCounters(Record record){
        //todo 
    }

    private static class RunningApps {
        String foreground = null;
        List<String> inMemory = new ArrayList<>();

        @Override
        public boolean equals(Object o) {
            RunningApps that = (RunningApps)o;
            return this.foreground.equals(that.foreground) && this.inMemory.equals(that.inMemory);
        }
    }
}
