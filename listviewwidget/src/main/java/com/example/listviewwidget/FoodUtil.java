package com.example.listviewwidget;

import android.content.Context;

import java.util.Random;

public class FoodUtil {

    public static String[] getFoods(Context context) {
        String foodArray[] = context.getResources().getStringArray(R.array.foods);
        String[] foods = new String[foodArray.length];
        Random random = new Random();
        for (int i = 0; i < foodArray.length; i++) {
            int index = random.nextInt(10);
            foods[i] = foodArray[i] + index;
        }
        return foods;
    }
}
