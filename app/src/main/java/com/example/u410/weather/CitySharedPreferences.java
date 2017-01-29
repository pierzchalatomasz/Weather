package com.example.u410.weather;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by U410 on 2017-01-28.
 */

public class CitySharedPreferences {
    public static void saveWidgetCity(Context context, int widgetId, String city) {
        SharedPreferences settings = context.getSharedPreferences(IntentExtras.CITY, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(widgetId + "", city);

        editor.commit();
    }

    public static void deleteWidgetCity(Context context, int widgetId) {
        SharedPreferences settings = context.getSharedPreferences(IntentExtras.CITY, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.remove(widgetId + "");

        editor.commit();
    }

    public static String getWidgetCity(Context context, int widgetId) {
        SharedPreferences settings = context.getSharedPreferences(IntentExtras.CITY, 0);
        return settings.getString(widgetId + "", null);
    }
}
