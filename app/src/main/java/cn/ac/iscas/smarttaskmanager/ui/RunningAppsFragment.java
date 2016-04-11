package cn.ac.iscas.smarttaskmanager.ui;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.baoyz.widget.PullRefreshLayout;

import java.util.ArrayList;
import java.util.List;

import cn.ac.iscas.smarttaskmanager.R;

/**
 * Created by chenh on 3/27/16.
 */
public class RunningAppsFragment extends Fragment {

    private static final String LOG_TAG = RunningAppsFragment.class.getSimpleName();
    private static final int REFRESH_INTERVAL = 5000;

    private RunningAppListAdapter adapter;
    private ActivityManager am;
    private PackageManager pm;

    private List<AppDisplayInfo> getListItems(){
        List<AppDisplayInfo> result = new ArrayList<>();
        for(ActivityManager.RunningAppProcessInfo pInfo: am.getRunningAppProcesses()){
            String processName = pInfo.processName;
            AppDisplayInfo info = AppDisplayInfo.createForRunningAppView(
                    pm, processName);
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

    private void refreshListPeriodically(){
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                refreshList();
                refreshListPeriodically();
            }
        }, REFRESH_INTERVAL);
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
        View rootView = inflater.inflate(R.layout.running_apps_fragment, container, false);

        final PullRefreshLayout layout = (PullRefreshLayout) rootView.findViewById(R.id.running_app_refresh_layout);
        layout.setOnRefreshListener(new PullRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshList();
                layout.setRefreshing(false);
            }
        });

        SwipeMenuListView listView = (SwipeMenuListView) rootView.findViewById(R.id.running_app_list);
        adapter = new RunningAppListAdapter(getContext(), getListItems());
        listView.setAdapter(adapter);
        refreshList();

        SwipeMenuCreator creator = new SwipeMenuCreator() {

            @Override
            public void create(SwipeMenu menu) {
                // create "open" item
                SwipeMenuItem item = new SwipeMenuItem(getContext());
                // set item background
                item.setBackground(new ColorDrawable(Color.parseColor("#3F51B5")));
                // set item width
                item.setWidth(180);
                // set item title
                item.setTitle("KILL");
                // set item title fontsize
                item.setTitleSize(18);
                // set item title font color
                item.setTitleColor(Color.WHITE);
                // add to menu
                menu.addMenuItem(item);
            }
        };
        listView.setMenuCreator(creator);
        listView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                switch (index) {
                    case 0:
                        am.killBackgroundProcesses(adapter.getItem(position).processName);
                        refreshList();
                        break;
                }
                return false;
            }
        });


        return rootView;
    }

}
