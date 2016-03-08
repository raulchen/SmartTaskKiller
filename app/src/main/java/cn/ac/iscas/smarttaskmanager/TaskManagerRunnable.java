package cn.ac.iscas.smarttaskmanager;

import android.util.Log;

/**
 * Created by chenh on 3/7/16.
 */
public class TaskManagerRunnable implements Runnable {

    private static final String LOG_TAG = TaskManagerRunnable.class.getSimpleName();

    @Override
    public void run() {
        while(true){
            Log.d(LOG_TAG, "I Love U :) !");
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
