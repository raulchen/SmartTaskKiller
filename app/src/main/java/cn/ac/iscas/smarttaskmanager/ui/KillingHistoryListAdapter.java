package cn.ac.iscas.smarttaskmanager.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;

import cn.ac.iscas.smarttaskmanager.R;

/**
 * Created by chenh on 3/27/16.
 */
public class KillingHistoryListAdapter extends ArrayAdapter<AppDisplayInfo> {

    private static DateFormat dateFormat = DateFormat.getDateTimeInstance();

    public KillingHistoryListAdapter(Context context, List<AppDisplayInfo> objects) {
        super(context, 0, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        AppDisplayInfo info = getItem(position);
        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.killing_history_list_item, parent, false);
        }
        ImageView iconView = (ImageView) convertView.findViewById(R.id.kh_app_icon);
        TextView nameView = (TextView) convertView.findViewById(R.id.kh_app_name);
        TextView timeView = (TextView) convertView.findViewById(R.id.kh_time);
        iconView.setImageDrawable(info.icon);
        nameView.setText(info.label);
        timeView.setText(dateFormat.format(new Date(info.time)));
        return convertView;
    }
}
