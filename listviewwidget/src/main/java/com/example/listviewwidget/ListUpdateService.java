package com.example.listviewwidget;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

public class ListUpdateService extends Service {
    private static final String TAG = "ListUpdateService";
    // 更新 list widget 的广播对应的action
    private final String ACTION_UPDATE_LiST = "com.example.listviewwidget.LIST_UPDATE";
    private Context mContext;
    // 周期性更新 widget 的周期
    private static final int UPDATE_TIME = 5000;
    // 更新周期的计数
    private int count = 0;


    private UpdateThread mUpdateThread;  // 周期性更新 widget 的线程
    private static final String CHANNEL_ID = "ListService";

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onCreate() {
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

        // 创建并开启线程UpdateThread
        mUpdateThread = new UpdateThread();
        mUpdateThread.start();

        mContext = this.getApplicationContext();
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        // 中断线程，即结束线程。
        if (mUpdateThread != null) {
            mUpdateThread.interrupt();
        }
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /*
     * 服务开始时，即调用startService()时，onStartCommand()被执行。
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand");
        super.onStartCommand(intent, flags, startId);
        //服务被系统kill后重启
        return START_STICKY;
    }

    private class UpdateThread extends Thread {

        @Override
        public void run() {
            super.run();
            try {
                count = 0;
                while (true) {
                    Log.d(TAG, "run ... count:" + count);
                    count++;

                    Intent updateIntent = new Intent(ACTION_UPDATE_LiST);
                    //安卓8.0必须添加
                    updateIntent.setComponent(new ComponentName(mContext, ListWidgetProvider.class));
                    mContext.sendBroadcast(updateIntent);
                    Log.d(TAG, "发送广播-----");
                    Thread.sleep(UPDATE_TIME);
                }

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
