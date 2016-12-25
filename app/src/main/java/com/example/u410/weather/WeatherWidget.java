package com.example.u410.weather;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.widget.RemoteViews;

/**
 * Implementation of App Widget functionality.
 */
public class WeatherWidget extends AppWidgetProvider {

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        CharSequence widgetText = context.getString(R.string.appwidget_text);
        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.weather_widget);
//        views.setTextViewText(R.id.appwidget_text, widgetText);

//        Bitmap drawable = ((BitmapDrawable) context.getResources().getDrawable(R.drawable.weather)).getBitmap();
//        views.setBitmap();

        views.setImageViewResource(R.id.currentWeatherIcon, R.drawable.weather);
        views.setImageViewResource(R.id.forecast1Icon, R.drawable.weather);
        views.setImageViewResource(R.id.forecast2Icon, R.drawable.weather);
        views.setImageViewResource(R.id.forecast3Icon, R.drawable.weather);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

