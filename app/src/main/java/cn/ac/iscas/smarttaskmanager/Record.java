package cn.ac.iscas.smarttaskmanager;


/**
 * Created by HWQ on 16/3/8.
 */
public class Record {

    static enum TimeOfDay{

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
    TimeOfDay timeOfDay;
    String dayOfWeek;
    String preApp;

    private static String isWeekDay(String day){
        if(day.equals("SATURDAY") || day.equals("SUNDAY")){
            return "Weekends";
        }
        return "Weekdays";
    }

    public Record(String app, int hour, String day, String preApp){
        super();
        this.app = app;
        this.timeOfDay = TimeOfDay.of(hour);
        this.dayOfWeek = isWeekDay(day);
        this.preApp = preApp;
    }

}
