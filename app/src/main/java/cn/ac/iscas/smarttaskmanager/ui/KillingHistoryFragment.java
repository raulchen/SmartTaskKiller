package cn.ac.iscas.smarttaskmanager.ui;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.baoyz.widget.PullRefreshLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import cn.ac.iscas.smarttaskmanager.R;

/**
 * Created by chenh on 3/27/16.
 */
public class KillingHistoryFragment extends Fragment {

    private static final String LOG_TAG = KillingHistoryFragment.class.getSimpleName();

    private KillingHistoryListAdapter adapter;
    private ActivityManager am;
    private PackageManager pm;

    private List<AppDisplayInfo> getListItems() {
        Random random = new Random();
        long time = System.currentTimeMillis() - random.nextInt(3600);

        List<AppDisplayInfo> result = new ArrayList<>();
        for(ActivityManager.RunningAppProcessInfo pInfo: am.getRunningAppProcesses()){
            String processName = pInfo.processName;
            AppDisplayInfo info = AppDisplayInfo.createForKillingHistoryView(
                    pm, processName, time);
            time -= random.nextInt(3600);
            if(info != null){
                result.add(info);
            }
        }
        return result;
    }

    private void refreshList(){
        if(isVisible()) {
            adapter.clear();
            adapter.addAll(getListItems());
            Log.d(LOG_TAG, "Refresh list");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        am = (ActivityManager)getContext().getSystemService(Context.ACTIVITY_SERVICE);
        pm = getContext().getPackageManager();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.killing_history_fragment, container, false);

        final PullRefreshLayout layout = (PullRefreshLayout) rootView.findViewById(R.id.killing_app_refresh_layout);
        layout.setOnRefreshListener(new PullRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshList();
                layout.setRefreshing(false);
            }
        });

        SwipeMenuListView listView = (SwipeMenuListView) rootView.findViewById(R.id.killing_history_list);
        adapter = new KillingHistoryListAdapter(getContext(), getListItems());
        listView.setAdapter(adapter);

        return rootView;
    }

}
