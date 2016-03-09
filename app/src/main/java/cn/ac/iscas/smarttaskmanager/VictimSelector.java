package cn.ac.iscas.smarttaskmanager;

import java.util.List;
import java.util.Map;
import cn.ac.iscas.smarttaskmanager.Record.TimeOfDay;  //这个不能自动引用么?

/**
 * Created by HWQ on 16/3/8.
 * When there is no enough memory, VictimSelector will
 * choose the app with lowest score which indicates the probability
 * that the given app will be launched in the near future from all the background
 * apps.
 */
public class VictimSelector {

    private static final double SCORE_LOWER_BOUND = 0.001;


    /**
     * P(A|D,T) = P(A,D,T) / P(D,T)
     * the key of jointCnt is D+","+T, the value is an object of Counter class.
     */
    private Map<String, Counter> jointCnt;

    private Map<String, Counter> lastAppCnt;

    public VictimSelector(Map<String, Counter> jointCnt, Map<String, Counter> lastAppCnt){
        this.jointCnt = jointCnt;
        this.lastAppCnt = lastAppCnt;
    }

    private static String joinString(String dayOfWeek, TimeOfDay timeOfDay){
        return dayOfWeek + "," + timeOfDay;
    }


    /**
     * Compute scores for all background apps,
     * return the app with the lowest score.
     * @return name of victim app
     */
    public String getVictimApp(List<String> inMemoryApps, Record record){
        String victim = null;
        double minScore = Double.MIN_VALUE;
        for(String app : inMemoryApps){
            double score = computeScore(app, record.app, record.timeOfDay, record.dayOfWeek);
            if(score < minScore){
                minScore = score;
                victim = app;
            }
        }
        return victim;
    }


    /**
     * Compute conditional probability such
     * as P(A|A') or P(A|D,T).
     * @param app
     * @param key
     * @param cnt
     * @return
     */
    private double computeCondProbability(String app, String key, Map<String, Counter> cnt){
        Counter counter = cnt.get(key);
        if(counter == null){
            return 0.0;  //no concurrence for key
        }
        Integer num = counter.appCnt.get(app);
        if(num == null || num == 0){
            return 0.0;  //no concurrence for key and app
        }
        return 1.0 * num / counter.totalNum;
    }


    /**
     * Compute the score for a given app.
     * @param app
     * @return
     */
    private double computeScore(String app, String preApp, TimeOfDay timeOfDay, String dayOfWeek){

        String lastAppKey = preApp;
        double lastAppScore = computeCondProbability(app, lastAppKey, lastAppCnt);
        if(lastAppScore == 0.0){
            lastAppScore = SCORE_LOWER_BOUND;
        }

        String joinKey = joinString(dayOfWeek, timeOfDay);
        double joinScore = computeCondProbability(app, joinKey, jointCnt);

        return lastAppScore * joinScore;

    }


}
