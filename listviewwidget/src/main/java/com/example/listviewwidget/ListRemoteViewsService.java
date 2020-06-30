package com.example.listviewwidget;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ListRemoteViewsService extends RemoteViewsService {
    public static final String TAG = "ListRemoteViewsService";

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        Log.d(TAG, "onGetViewFactory, intent=" + intent);
        return new MyWidgetFactory(getApplicationContext(), intent);
    }

    public static class MyWidgetFactory implements RemoteViewsService.RemoteViewsFactory {
        private Context mContext;
        private static List<String> foods;


        public MyWidgetFactory(Context context, Intent intent) {
            Log.d(TAG, " MyWidgetFactory");
            mContext = context;

            foods = new ArrayList<>();
            Bundle bundle = intent.getExtras();
            foods.addAll(Arrays.asList(bundle.getStringArray("foods")));
        }

        public static void refresh(List<String> myFoods) {
            foods=myFoods;
        }

        @Override
        public void onCreate() {
            Log.d(TAG, " onCreate");
        }

        @Override
        public void onDataSetChanged() {
            Log.d(TAG, " onDataSetChanged");
        }

        @Override
        public void onDestroy() {
            Log.d(TAG, " onDestroy");
        }

        @Override
        public int getCount() {
            return foods != null ? foods.size() : 0;
        }

        @Override
        public RemoteViews getViewAt(int i) {
            Log.d(TAG, " getViewAt" + i);

            if (i < 0 || i >= getCount()) {
                return null;
            }
            RemoteViews views = new RemoteViews(mContext.getPackageName(), R.layout.list_item);
            views.setTextViewText(R.id.item_text, foods.get(i));

            // 设置第position位的“视图”对应的响应事件
            Bundle extras = new Bundle();
            extras.putString(ListWidgetProvider.COLLECTION_VIEW_EXTRA, foods.get(i));
            Intent fillInIntent = new Intent();
            fillInIntent.putExtras(extras);

            views.setOnClickFillInIntent(R.id.ll_list_item, fillInIntent);

            return views;
        }

        @Override
        public RemoteViews getLoadingView() {
            return null;
        }

        @Override
        public int getViewTypeCount() {
            return 1;
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public boolean hasStableIds() {
//            Log.d(TAG, "hasStableIds");
            return true;
        }
    }


}
