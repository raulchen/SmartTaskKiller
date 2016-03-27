package cn.ac.iscas.smarttaskmanager.ui;

import android.content.Context;
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
    private static AppDisplayInfo none = new AppDisplayInfo(null, null, null);
    private static final String LOG_TAG = AppDisplayInfo.class.getName();

    public final Drawable icon;
    public final String label;
    public final String processName;

    private AppDisplayInfo(Drawable icon, String label, String processName) {
        this.icon = icon;
        this.label = label;
        this.processName = processName;
    }

    public static AppDisplayInfo create(String processName, PackageManager pm){
        AppDisplayInfo info = cache.get(processName);
        if(info == null) {
            ApplicationInfo appInfo = null;
            try {
                appInfo = pm.getApplicationInfo(
                        processName,
                        PackageManager.GET_META_DATA);
                info = new AppDisplayInfo(
                        appInfo.loadIcon(pm),
                        appInfo.loadLabel(pm).toString(),
                        processName);
            } catch (PackageManager.NameNotFoundException e) {
                Log.w(LOG_TAG, "failed to load ApplicationInfo from " + processName);
                info = none;
            }
            cache.put(processName, info);
        }
        return info == none? null: info;
    }
}
