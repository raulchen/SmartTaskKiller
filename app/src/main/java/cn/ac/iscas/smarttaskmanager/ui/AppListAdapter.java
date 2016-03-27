package cn.ac.iscas.smarttaskmanager.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import cn.ac.iscas.smarttaskmanager.R;

/**
 * Created by chenh on 3/27/16.
 */
public class AppListAdapter extends ArrayAdapter<AppDisplayInfo> {

    public AppListAdapter(Context context, List<AppDisplayInfo> objects) {
        super(context, 0, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        AppDisplayInfo info = getItem(position);
        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item, parent, false);
        }
        ImageView iconView = (ImageView) convertView.findViewById(R.id.app_icon);
        TextView nameView = (TextView) convertView.findViewById(R.id.app_name);
        iconView.setImageDrawable(info.icon);
        nameView.setText(info.label);
        return convertView;
    }
}
