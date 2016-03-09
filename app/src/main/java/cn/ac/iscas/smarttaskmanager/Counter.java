package cn.ac.iscas.smarttaskmanager;

import java.util.HashMap;

/**
 * Created by HWQ on 16/3/8.
 */
public class Counter {

    /* totalNum equals to the sum of all apps' count */
    int totalNum;

    /**
     * map from a given app to the times that the app appear
     * together with the corresponding key of Counter.
     */
    HashMap<String, Integer> appCnt;

    public Counter(){
        this.totalNum = 0;
        this.appCnt = new HashMap<>();
    }

}
