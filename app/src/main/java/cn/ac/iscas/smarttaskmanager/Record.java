package cn.ac.iscas.smarttaskmanager;


import java.util.Date;

/**
 * Created by HWQ on 16/3/8.
 */
public class Record {

    enum TimeOfDay{

        MORNING,
        AFTERNOON,
        NIGHT;

        static TimeOfDay of(int hour){
            if(hour >= 5 && hour <=12){
                return MORNING;
            }else if(hour >= 12 && hour < 18){
                return AFTERNOON;
            }else{
                return NIGHT;
            }
        }
    }

    String app;
    long time;
    TimeOfDay timeOfDay;
    String dayOfWeek;
    String preApp;

    private static String isWeekDay(Date date){
        if(date.getDay() >= 5){
            return "Weekends";
        }
        return "Weekdays";
    }

    public Record(String app, String preApp, long time){
        super();
        this.app = app;
        this.preApp = preApp;
        this.time = time;
        Date date = new Date(time);
        this.timeOfDay = TimeOfDay.of(date.getHours());
        this.dayOfWeek = isWeekDay(date);
    }

}
