package com.example.calendarwidget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.widget.RemoteViews;

public class CalendarWidgetProvider extends AppWidgetProvider {

    // 系统日期改变接收的广播的action
    private static final String DATE_CHANGED = Intent.ACTION_DATE_CHANGED;
    // 系统时间改变接收的广播的action
    private static final String TIME_CHANGED = Intent.ACTION_TIME_CHANGED;
    // 接收UpdateService转发的TIME_TICK的广播
    private static final String TIME_TICK = "com.nwd.android.calendarwidget.timetick";

    public static String TAG = "CalendarWidgetProvider";
    private RemoteViews mRv;
    public static final boolean DEBUG = true;


    @Override
    public void onEnabled(Context context) {
        // TODO Auto-generated method stub
        super.onEnabled(context);
        Log.d(TAG, "CalendarWidgetProvider.onEnabled()");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(new Intent(context, UpdateService.class));
        } else {
            context.startService(new Intent(context, UpdateService.class));
        }
    }

    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);
        Log.d(TAG, "CalendarWidgetProvider.onDisabled()");
        context.stopService(new Intent(context, UpdateService.class));
    }

    /**
     * 接收到广播根据相应的action转换相应的View
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO Auto-generated method stub
        super.onReceive(context, intent);
        String action = intent.getAction();
        Log.d(TAG, "CalendarWidgetProvider.onReceive() " + action);

        if (mRv == null) {
            mRv = new RemoteViews(context.getPackageName(), R.layout.main);
        }

        DateUtil util = DateUtil.getInstance(context);
        // 获取日期字符串
        String date = util.getDate();
        // 获取星期几
        String weekDay = util.getWeekDay();
        // 接收到DATE_CHANGED的系统广播
        if (DATE_CHANGED.equals(action)) {
            Log.d(TAG, "CalendarWidgetProvider.onReceive() " + date);
            updateDate(mRv, weekDay, date);
            Log.d(TAG, date + "");
        }
        // 接收到TIME_CHANGED的系统广播
        if (TIME_CHANGED.equals(action)) {
            Log.d(TAG, "CalendarWidgetProvider.onReceive() " + date);
            updateDate(mRv, weekDay, date);
            updateTime(context, mRv);
            Log.d(TAG, date + "");
        }
        // 接收到TIME_TICK的系统广播
        if (TIME_TICK.equals(action)) {
            updateTime(context, mRv);
            updateDate(mRv, weekDay, date);
        }
        AppWidgetManager appWidgetManager = AppWidgetManager
                .getInstance(context);
        int[] appIds = appWidgetManager.getAppWidgetIds(new ComponentName(
                context, CalendarWidgetProvider.class));
        appWidgetManager.updateAppWidget(appIds, mRv);
        Log.d(TAG, date + "");
    }

    /**
     * 更新widget上面所有的view
     */
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager,
                         int[] appWidgetIds) {
        // TODO Auto-generated method stub
        super.onUpdate(context, appWidgetManager, appWidgetIds);
        for (int i = 0; i < appWidgetIds.length; i++) {

            if (mRv == null) {
                mRv = new RemoteViews(context.getPackageName(), R.layout.main);
            }
            DateUtil util = DateUtil.getInstance(context);
            String date = util.getDate();
            String weekDay = util.getWeekDay();
            updateTime(context, mRv);
            updateDate(mRv, weekDay, date);

            //设置widget的点击事件，当点击时进入Calendar
            Intent LaunchCalendarIntent = new Intent(context,MainActivity.class);
            PendingIntent luanchPendingIntent = PendingIntent.getActivity(
                    context, 0, LaunchCalendarIntent, 0);
            mRv.setOnClickPendingIntent(R.id.time_linearlayout,
                    luanchPendingIntent);
            appWidgetManager.updateAppWidget(appWidgetIds, mRv);
            Log.d(TAG, date + "");
        }
    }

    // 更新widget上面的日期
    public void updateDate(RemoteViews rv, String weekDay, String date) {
        rv.setTextViewText(R.id.date, "" + date);
        rv.setTextViewText(R.id.week, "" + weekDay);
    }

    // 更新widget上面的时间
    public void updateTime(Context context, RemoteViews rv) {

        DateUtil util = DateUtil.getInstance(context);
        int hour = util.getHour();
        int minute = util.getMinute();
        if (!util.is24()) {
            if (hour > 12) {
                hour -= 12;
            }
            if (hour == 0) {
                hour = 12;
            }
        }
        if (minute < 10) {
            rv.setTextViewText(R.id.minute, "0" + minute);
        } else {
            rv.setTextViewText(R.id.minute, "" + minute);
        }
        rv.setTextViewText(R.id.hour, "" + hour);

    }

}