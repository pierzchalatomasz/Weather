package com.example.u410.weather;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;

import com.example.u410.weather.DataSerialization.Current.CurrentWeather;
import com.example.u410.weather.DataSerialization.Forecast.WeatherForecast;

import org.parceler.Parcels;

/**
 * Implementation of App Widget functionality.
 */
public class WeatherWidget extends AppWidgetProvider {

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.weather_widget);

        views.setImageViewResource(R.id.currentWeatherIcon, R.drawable.weather);
        views.setImageViewResource(R.id.forecast1Icon, R.drawable.weather);
        views.setImageViewResource(R.id.forecast2Icon, R.drawable.weather);
        views.setImageViewResource(R.id.forecast3Icon, R.drawable.weather);

        // Get city name from configuration activity (null if user selected "Use Device Location")
        String cityName = NewAppWidgetConfigureActivity.loadCityNamePref(context, appWidgetId);
        if (cityName != null) {
            views.setTextViewText(R.id.city, cityName);
        }

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }

        Intent intent = new Intent(context, DataService.class);

        context.startService(intent);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        ComponentName name = new ComponentName(context.getPackageName(), WeatherWidget.class.getName());
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(name);

        if (intent != null) {
            if (intent.getAction() == "CURRENT_WEATHER") {
                updateCurrentWeather(context, intent, appWidgetManager, appWidgetIds);
            }
            else if (intent.getAction() == "WEATHER_FORECAST") {
                updateWeatherForecast(context, intent, appWidgetManager, appWidgetIds);
            }
        }

        super.onReceive(context, intent);
    }

    private void updateCurrentWeather(Context context, Intent intent, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            CurrentWeather currentWeather = Parcels.unwrap(intent.getParcelableExtra("CURRENT_WEATHER"));

            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.weather_widget);
            views.setTextViewText(R.id.city, currentWeather.getName());
            views.setTextViewText(R.id.currentTemperature, currentWeather.getMain().getTemp_min() + "°C");
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }

    private void updateWeatherForecast(Context context, Intent intent, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            WeatherForecast weatherForecast = Parcels.unwrap(intent.getParcelableExtra("WEATHER_FORECAST"));

            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.weather_widget);

            appWidgetManager.updateAppWidget(appWidgetId, views);
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

