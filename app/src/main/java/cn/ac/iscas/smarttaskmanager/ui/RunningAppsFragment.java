package cn.ac.iscas.smarttaskmanager.ui;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import cn.ac.iscas.smarttaskmanager.R;

/**
 * Created by chenh on 3/27/16.
 */
public class RunningAppsFragment extends Fragment {

    private static final String LOG_TAG = RunningAppsFragment.class.getSimpleName();
    private static final int REFRESH_INTERVAL = 5000;

    ListView runningAppListView;
    AppListAdapter appListAdapter;

    private List<AppDisplayInfo> getAppDisplayInfoList(Context context){
        ActivityManager am = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
        PackageManager pm = context.getPackageManager();
        List<AppDisplayInfo> result = new ArrayList<>();
        for(ActivityManager.RunningAppProcessInfo pInfo: am.getRunningAppProcesses()){
            String processName = pInfo.processName;
            AppDisplayInfo info = AppDisplayInfo.create(processName, pm);
            if(info != null){
                result.add(info);
            }
        }
        return result;
    }

    private void refreshList(){
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Context context = getContext();
                if(context != null) {
                    appListAdapter.clear();
                    appListAdapter.addAll(getAppDisplayInfoList(context));
                    Log.d(LOG_TAG, "Refresh list");
                }
                refreshList();
            }
        }, REFRESH_INTERVAL);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.running_apps_fragment, container, false);
        runningAppListView = (ListView) rootView.findViewById(R.id.running_app_list);
        appListAdapter = new AppListAdapter(getContext(), getAppDisplayInfoList(getContext()));
        runningAppListView.setAdapter(appListAdapter);
        refreshList();
        return rootView;
    }

}
