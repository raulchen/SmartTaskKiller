package cn.ac.iscas.smarttaskmanager;

import android.app.ActivityManager;
import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by chenh on 3/7/16.
 */
public class TaskManagerRunnable implements Runnable {

    private static final String LOG_TAG = TaskManagerRunnable.class.getSimpleName();

    private static final int CHECK_INTERVAL = 3000;

    private static final double LOW_MEMORY_THRESHOLD_RATIO = 7;

    private ActivityManager am;

    private RunningApps currentRunningApps = null;

    private Record lastestRecord = null;

    private Map<String, Counter> jointCnt = new HashMap<>();

    private Map<String, Counter> lastAppCnt = new HashMap<>();

    private static final long WINDOW_SIZE = 7 * 24 * 60 * 60;

    private List<Record> history = new LinkedList<>();

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
            RunningApps ra = getRunningApps();
            if(!skipApp(ra.foreground) && !ra.equals(currentRunningApps)){
                String preApp = currentRunningApps == null? null : currentRunningApps.foreground;
                currentRunningApps = ra;
                Log.d(LOG_TAG, "new record: " + preApp + " -> " + currentRunningApps.foreground);
                lastestRecord = new Record(currentRunningApps.foreground, preApp, System.currentTimeMillis());
                updateCounters(lastestRecord);
            }
            if(checkMemoryUsage()){
                List<String> inMemory = getRunningApps().inMemory;
                Log.d(LOG_TAG, inMemory.toString());
                String victim = new VictimSelector(jointCnt, lastAppCnt).getVictimApp(inMemory, lastestRecord);
                Log.i(LOG_TAG, "gonna kill " + victim);
                kill(victim);
            }
        } catch (Exception e) {
            Log.e(LOG_TAG, "Unknown error", e);
        }
    }

    private void kill(String victim) {
        am.killBackgroundProcesses(victim);
    }

    /**
     * @return true if we think memory is low and need killing apps
     */
    private boolean checkMemoryUsage(){
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        this.am.getMemoryInfo(memoryInfo);
        //TODO save
        Log.d(LOG_TAG, String.format("Available: %d, Threshold: %d", memoryInfo.availMem, (int)(memoryInfo.threshold * LOW_MEMORY_THRESHOLD_RATIO)));
        return memoryInfo.availMem < memoryInfo.threshold * LOW_MEMORY_THRESHOLD_RATIO;
    }

    private RunningApps getRunningApps(){
        List<ActivityManager.RunningAppProcessInfo> infos = this.am.getRunningAppProcesses();
        if(infos == null || infos.isEmpty()){
            throw new RuntimeException("Cannot getRunningAppProcesses");
        }
        RunningApps ra = new RunningApps();
        ra.foreground = infos.get(0).processName;
        for(ActivityManager.RunningAppProcessInfo info : infos){
            if(skipApp(info.processName)){
                continue;
            }
            if(!info.processName.equals(ra.foreground)){
                ra.inMemory.add(info.processName);
            }
        }
        return ra;
    }

    private boolean skipApp(String app){
        return app.toLowerCase().contains("launcher") || isService(app);
    }

    private boolean isService(String processName){
        for(ActivityManager.RunningServiceInfo info : am.getRunningServices(999)){
            if(info.process.equals(processName)){
                return true;
            }
        }
        return false;
    }

    private boolean isOldHistory(Record first, Record current){
        long firstTime = first.time;
        long currentTime = current.time;
        return (currentTime - firstTime) > WINDOW_SIZE;
    }

    private void increase(Record record, String key, Map<String, Counter> cnt){
        if(key != null){
            Counter counter = cnt.get(key);
            if(counter == null){
                counter = new Counter();
                cnt.put(key, counter);
            }
            Integer num = counter.appCnt.get(record.app);
            if(num == null){
                num = 0;
            }
            counter.appCnt.put(record.app, num+1);
            counter.totalNum++;
        }
    }

    private void increaseCounters(Record record){
        String key = record.preApp;
        increase(record, key, lastAppCnt);

        String jointKey = VictimSelector.joinString(record.dayOfWeek, record.timeOfDay);
        increase(record, jointKey, jointCnt);
    }


    private void decrease(Record record, String key, Map<String, Counter> cnt){
        if(key != null){
            Counter counter = cnt.get(key);
            Integer num = counter.appCnt.get(record.app);
            if(num == 1){
                counter.appCnt.remove(record.app);
            }else{
                counter.appCnt.put(record.app, num-1);
            }
            if(counter.appCnt.size() == 0){
                cnt.remove(key);
            }
            counter.totalNum--;
        }
    }


    private void decreaseCounters(Record record){
        String key = record.preApp;
        decrease(record, key, lastAppCnt);

        String jointKey = VictimSelector.joinString(record.dayOfWeek, record.timeOfDay);
        decrease(record, jointKey, jointCnt);
    }


    /**
     * Whenever there comes a new record, we need to insert the new record into
     * history list and increase the counters. Meanwhile, we also need to remove
     * records early than the given WINDOW_SIZE and decrease the counters.
     * @param record
     */
    private void updateCounters(Record record){

        increaseCounters(record);

        history.add(record);

        while(isOldHistory(history.get(0), record)) {
            Record current = history.get(0);
            decreaseCounters(current);
            history.remove(0);
        }
    }

    private static class RunningApps {
        String foreground = null;
        List<String> inMemory = new ArrayList<>();

        @Override
        public boolean equals(Object o) {
            if(o == null || !(o instanceof RunningApps)){
                return false;
            }
            RunningApps that = (RunningApps)o;
            return this.foreground.equals(that.foreground) && this.inMemory.equals(that.inMemory);
        }
    }
}
