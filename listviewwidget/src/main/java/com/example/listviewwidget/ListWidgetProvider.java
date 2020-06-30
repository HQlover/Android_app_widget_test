package com.example.listviewwidget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import java.util.Arrays;

public class ListWidgetProvider extends AppWidgetProvider {
    public static final String TAG = "ListWidgetProvider";

    public static final String COLLECTION_VIEW_ACTION = "com.oitsme.COLLECTION_VIEW_ACTION";
    public static final String COLLECTION_VIEW_EXTRA = "com.oitsme.COLLECTION_VIEW_EXTRA";

    // 更新 list widget 的广播对应的action
    private final String ACTION_UPDATE_LiST = "com.example.listviewwidget.LIST_UPDATE";
    private RemoteViews mRv;

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        Log.d(TAG, "OnReceive:Action: " + action);
        if (action.equals(COLLECTION_VIEW_ACTION)) {
            // 接受“ListView”的点击事件的广播
            Log.d(TAG, "列表项被点击");
            int appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID);
            String showTest = intent.getStringExtra(COLLECTION_VIEW_EXTRA);
            Toast.makeText(context, showTest, Toast.LENGTH_SHORT).show();
        } else if (action.equals(ACTION_UPDATE_LiST)) {
            //接收更新列表数据广播
            final ComponentName cn = new ComponentName(context, ListWidgetProvider.class);
            String[] foods = FoodUtil.getFoods(context);
            ListRemoteViewsService.MyWidgetFactory.refresh(Arrays.asList(foods));
            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetManager.getAppWidgetIds(cn), R.id.list);
        }
        super.onReceive(context, intent);
    }


    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appId : appWidgetIds) {

            if (mRv == null) {
                mRv = new RemoteViews(context.getPackageName(), R.layout.list_appwidget);
            }

            Intent remoteIntent = new Intent(context, ListRemoteViewsService.class);

            String[] foods = FoodUtil.getFoods(context);
            Bundle bundle = new Bundle();
            bundle.putStringArray("foods", foods);

            remoteIntent.putExtras(bundle);

            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.list_appwidget);
            views.setRemoteAdapter(R.id.list, remoteIntent);

            // 设置响应 “ListView” 的intent模板
            // 说明：“集合控件(如GridView、ListView、StackView等)”中包含很多子元素，如GridView包含很多格子。
            //     它们不能像普通的按钮一样通过 setOnClickPendingIntent 设置点击事件，必须先通过两步。
            //        (01) 通过 setPendingIntentTemplate 设置 “intent模板”，这是比不可少的！
            //        (02) 然后在处理该“集合控件”的RemoteViewsFactory类的getViewAt()接口中 通过 setOnClickFillInIntent 设置“集合控件的某一项的数据”
            Intent gridIntent = new Intent();
            gridIntent.setAction(COLLECTION_VIEW_ACTION);

            //安卓8.0必须添加
            gridIntent.setComponent(new ComponentName(context, ListWidgetProvider.class));
            gridIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appId);
            remoteIntent.setData(Uri.parse(remoteIntent.toUri(Intent.URI_INTENT_SCHEME)));
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, gridIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            // 设置intent模板
            views.setPendingIntentTemplate(R.id.list, pendingIntent);

            appWidgetManager.updateAppWidget(appId, views);

        }
        Log.d(TAG, "onUpdate");
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }


    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        Log.d(TAG, "onDeleted(): appWidgetIds.length=" + appWidgetIds.length);
        super.onDeleted(context, appWidgetIds);
    }

    @Override
    public void onEnabled(Context context) {
        Log.d(TAG, "onEnabled");
        // 在第一个 widget 被创建时，开启服务
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(new Intent(context, ListUpdateService.class));
        } else {
            context.startService(new Intent(context, ListUpdateService.class));
        }
        super.onEnabled(context);
    }

    @Override
    public void onDisabled(Context context) {
        Log.d(TAG, "onDisabled");

        // 在最后一个 widget 被删除时，终止服务
        context.stopService(new Intent(context, ListUpdateService.class));
        super.onDisabled(context);
    }
}
