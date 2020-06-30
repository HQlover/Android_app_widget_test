package com.example.calendarwidget;

import android.content.Context;
import android.text.format.DateFormat;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtil {
    public static final String TAG="DateUtil";
    private Context context;
    // 记录系统时间格式(分为12小时制和24小时制)
    private SimpleDateFormat formatter;
    private static DateUtil instance;

    /**
     * 生成单例模型并初始化系统时间格式
     *
     * @param context
     */
    private DateUtil(Context context) {
        this.context = context;
        formatter = new SimpleDateFormat("MM/dd");
    }

    public boolean is24(){
        return DateFormat.is24HourFormat( context);
    }


    /**
     * 获取时间的小时数，默认为24小时制
     *
     * @return
     */
    public int getHour() {
        Calendar c = Calendar.getInstance();
        return c.get(Calendar.HOUR_OF_DAY);
    }

    /**
     * 获取时间的分钟数
     *
     * @return
     */
    public int getMinute() {
        Calendar c = Calendar.getInstance();
        return c.get(Calendar.MINUTE);
    }

    /**
     * 获取当前年份
     *
     * @return
     */
    public int getYear() {
        Calendar c = Calendar.getInstance();
        return c.get(Calendar.YEAR);
    }

    /**
     * 获取当前月份
     *
     * @return
     */
    public int getMonth() {
        Calendar c = Calendar.getInstance();
        return c.get(Calendar.MONTH);
    }

    /**
     * 获取所在月的天数
     *
     * @return
     */
    public int getDayOfMonth() {
        Calendar c = Calendar.getInstance();
        return c.get(Calendar.DAY_OF_MONTH);
    }

    /**
     * 重新设置系统时间格式
     */
    public void setDateFormat(SimpleDateFormat formatter) {
        this.formatter = formatter;
    }

    public static DateUtil getInstance(Context context) {
        if (instance == null) {
            instance = new DateUtil(context);
            return instance;
        } else {
            return instance;
        }
    }

    /**
     * 获取系统日期
     *
     * @return 返回格式如2014/6/24
     */
    public String getDate() {
        Date curDate = new Date(System.currentTimeMillis());
        String date = formatter.format(curDate);
        return date;
    }


    /**
     * @return 当前日期是星期几
     */
    public String getWeekDay() {
        String weekDay = "";
        Calendar c = Calendar.getInstance();

        int day = c.get(Calendar.DAY_OF_WEEK);
        Log.d(TAG,"week day"+day);
        String weekdays[] = context.getResources().getStringArray(R.array.weekdays);
        switch (day) {
            case 1:
                weekDay = weekdays[0];
                break;
            case 2:
                weekDay = weekdays[1];
                break;
            case 3:
                weekDay = weekdays[2];
                break;
            case 4:
                weekDay = weekdays[3];
                break;
            case 5:
                weekDay = weekdays[4];
                break;
            case 6:
                weekDay = weekdays[5];
                break;
            case 7:
                weekDay = weekdays[6];
                break;
            default:

        }

        return weekDay;
    }
}