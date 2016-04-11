package cn.ac.iscas.smarttaskmanager.ui;

import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import cn.ac.iscas.smarttaskmanager.R;
import lecho.lib.hellocharts.model.Column;
import lecho.lib.hellocharts.model.ColumnChartData;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.SubcolumnValue;
import lecho.lib.hellocharts.model.ValueShape;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.view.ColumnChartView;
import lecho.lib.hellocharts.view.LineChartView;

/**
 * Created by chenh on 4/10/16.
 */
public class MemMonitorFragment extends Fragment {

    private static final int MAX_ITEM_SIZE = 10;
    private static final long REFRESH_INTERVAL = 1000;
    private ActivityManager am;

    private View rootView;
    private LineChartView chart;
    private LineChartData data;
    private int size;

    private void refreshChart() {
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        am.getMemoryInfo(memoryInfo);
        long value = memoryInfo.availMem;
        value *= (0.8 + Math.random() / 5);

//        int size = data.getLines().size();
        if(size >= MAX_ITEM_SIZE){
            for(int i = 0; i < size - 1; i++){
                PointValue cur = data.getLines().get(0).getValues().get(i);
                PointValue next = data.getLines().get(0).getValues().get(i + 1);
                cur.setTarget(cur.getX(), next.getY());
            }
            PointValue cur = data.getLines().get(0).getValues().get(size - 1);
            cur.setTarget(cur.getX(), value);
        }
        else {
//            data.getLines().get(0).getValues().add(new PointValue(size, value));
            data.getLines().get(0).getValues().get(size).setTarget(size, value);
            size++;
        }
//        chart.setBackgroundTintMode();
        Viewport v = chart.getCurrentViewport();
        v.set(0, v.top, v.right, 0);
        chart.setCurrentViewport(v);

        chart.startDataAnimation();
    }

    private void refreshChartPeriodically() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                refreshChart();
                refreshChartPeriodically();
            }
        }, REFRESH_INTERVAL);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        am = (ActivityManager) getContext().getSystemService(Context.ACTIVITY_SERVICE);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if(rootView != null){
            return rootView;
        }
        rootView = inflater.inflate(R.layout.mem_monitor_fragment, container, false);
        chart = (LineChartView) rootView.findViewById(R.id.mem_chart);
        Line line = new Line();
        for(int i = 0; i < MAX_ITEM_SIZE; i++){
            line.getValues().add(new PointValue(i, 0));
        }
        size = 0;
        line.setColor(Color.BLUE);
        line.setFilled(true);
        line.setCubic(true);
        line.setHasLabels(false);
        line.setHasLabelsOnlyForSelected(true);
        line.setStrokeWidth(1);
//        line.setHasPoints(false);
        line.setPointRadius(3);
        List<Line> lines = new ArrayList<>();
        lines.add(line);
        data = new LineChartData(lines);
        data.setAxisXBottom(null);
        data.setAxisYLeft(null);
        data.setBaseValue(0);
        chart.setZoomEnabled(true);
        chart.setLineChartData(data);
        refreshChartPeriodically();

        return rootView;
    }

}