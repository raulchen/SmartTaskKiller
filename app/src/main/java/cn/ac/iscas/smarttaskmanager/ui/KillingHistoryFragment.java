package cn.ac.iscas.smarttaskmanager.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import cn.ac.iscas.smarttaskmanager.R;

/**
 * Created by chenh on 3/27/16.
 */
public class KillingHistoryFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.killing_history_fragment, container, false);
        return rootView;
    }
}
