package com.example.calendarwidget;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.RequiresApi;

public class UpdateService extends Service {
    public static final boolean DEBUG = true;
    public static final String TAG = "UpdateService";
    private BroadcastReceiver receiver;
    private static final String CHANNEL_ID = "CalenService";

    @Override
    public IBinder onBind(Intent arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * 创建服务并动态注册系统的ACTION_TIME_TICK服务
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();
        Log.d(TAG, "UpdateService.onCreate()");

        //android8.0必须使用前台服务，前台服务需要创建前台通知
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        NotificationChannel Channel = new NotificationChannel(CHANNEL_ID, "主服务", NotificationManager.IMPORTANCE_HIGH);
        Channel.enableLights(true);//设置提示灯
        Channel.setLightColor(Color.RED);//设置提示灯颜色
        Channel.setShowBadge(true);//显示logo
        Channel.setDescription("ytzn");//设置描述
        Channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC); //设置锁屏可见 VISIBILITY_PUBLIC=可见
        manager.createNotificationChannel(Channel);

        Notification notification = new Notification.Builder(this)
                .setChannelId(CHANNEL_ID)
                .setContentTitle("主服务")//标题
                .setContentText("运行中...")//内容
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.mipmap.ic_launcher)//小图标一定需要设置,否则会报错(如果不设置它启动服务前台化不会报错,但是你会发现这个通知不会启动),如果是普通通知,不设置必然报错
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                .build();
        startForeground(1, notification);//服务前台化只能使用startForeground()方法,不能使用 notificationManager.notify(1,notification); 这个只是启动通知使用的,使用这个方法你只需要等待几秒就会发现报错了

        // 实例化接收系统ACTION_TIME_TICK的BroadcastReceiver
        receiver = new BroadcastReceiver() {
            private static final String ACTION_TIME_TICK = Intent.ACTION_TIME_TICK;

            @Override
            public void onReceive(Context context, Intent intent) {
                // TODO Auto-generated method stub
                Log.d(TAG, "UpdateService.onReceive()");

                if (intent.getAction().equals(ACTION_TIME_TICK)) {//注意：使用的是equals方法，不是"=="
                    Intent tmp = new Intent();
                    tmp.setAction("com.nwd.android.calendarwidget.timetick");
                    Log.d(TAG, "TIME_TICK");
                    //安卓8.0必须添加
                    tmp.setComponent(new ComponentName(context, CalendarWidgetProvider.class));
                    context.sendBroadcast(tmp);
                }
            }

        };
        //Intent.ACTION_TIME_TICK
        //Broadcast Action: The current time has changed. Sent every minute. You can not receive this through components declared in manifests,
        // only by exlicitly registering for it withContext.registerReceiver()
        this.registerReceiver(receiver, new IntentFilter(Intent.ACTION_TIME_TICK));
    }

    /**
     * 在onDestroy()方法里面重启UpdateService
     */
    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        this.unregisterReceiver(receiver);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // TODO Auto-generated method stub
        return START_STICKY;
    }

}