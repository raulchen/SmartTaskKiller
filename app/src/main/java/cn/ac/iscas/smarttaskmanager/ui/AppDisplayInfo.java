package cn.ac.iscas.smarttaskmanager.ui;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by chenh on 3/27/16.
 */
public class AppDisplayInfo {

    private static Map<String, AppDisplayInfo> cache = new HashMap<>();
    private static final String LOG_TAG = AppDisplayInfo.class.getName();

    public final Drawable icon;
    public final String label;
    public final String processName;
    public final Long time;

    private AppDisplayInfo(Drawable icon, String label, String processName, Long time) {
        this.icon = icon;
        this.label = label;
        this.processName = processName;
        this.time = time;
    }

    public static AppDisplayInfo createForRunningAppView(
            PackageManager pm, String processName){
        return createForKillingHistoryView(pm, processName, null);
    }

    public static AppDisplayInfo createForKillingHistoryView(
            PackageManager pm,String processName, Long time){
        try {
            ApplicationInfo appInfo = pm.getApplicationInfo(
                    processName,
                    PackageManager.GET_META_DATA);
            return new AppDisplayInfo(
                    appInfo.loadIcon(pm),
                    appInfo.loadLabel(pm).toString(),
                    processName,
                    time);
        } catch (PackageManager.NameNotFoundException e) {
            Log.w(LOG_TAG, "failed to load ApplicationInfo from " + processName);
            return null;
        }
    }
}
