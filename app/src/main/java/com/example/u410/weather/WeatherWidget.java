package com.example.u410.weather;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.example.u410.weather.DataSerialization.Current.CurrentWeather;
import com.example.u410.weather.DataSerialization.Forecast.ForecastManager;
import com.example.u410.weather.DataSerialization.Forecast.WeatherForecast;
import com.example.u410.weather.DataSerialization.WeatherIconManager;

import org.parceler.Parcels;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Implementation of App Widget functionality.
 */
public class WeatherWidget extends AppWidgetProvider {
    private Handler updateWidgetHandler;
    private Runnable updateWidgetTask;
    private final static int INTERVAL = 1000 * 60 * 30;
    private Context context;
    private static CitySharedPreferences citySharedPreferences = new CitySharedPreferences();
    private static WeatherWidgetViewUpdater viewUpdater = new WeatherWidgetViewUpdater();
    private static Location location;
    public static String cityName;

    public WeatherWidget()
    {
        updateWidgetHandler = new Handler();
    }

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        cityName = getCityName(context, appWidgetId);
        viewUpdater.init(context, appWidgetManager, appWidgetId, cityName);
        citySharedPreferences.saveWidgetCity(context, appWidgetId, cityName);

        sendUpdateRequest(context, appWidgetId);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
        this.context = context;

        updateWidgetTask = new Runnable()
        {
            @Override
            public void run() {
                updateWidgetCyclic();
            }
        };
        startRepeatingTask();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        int appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, 0);

        if (intent != null && intent.getAction() == IntentExtras.WEATHER_UPDATE) {
            viewUpdater.update(context, intent, appWidgetManager, appWidgetId);
        }

        super.onReceive(context, intent);
    }

    private static void sendUpdateRequest(Context context, int appWidgetId) {
        Intent intent = new Intent(context, DataService.class);
        intent.putExtra(IntentExtras.CITY, cityName);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        context.startService(intent);
    }

    private static String getCityName(Context context, int appWidgetId) {
        // Get city name from configuration activity (null if user selected "Use Device Location")
        String cityName = citySharedPreferences.getWidgetCity(context, appWidgetId) != null ?
                citySharedPreferences.getWidgetCity(context, appWidgetId) :
                NewAppWidgetConfigureActivity.loadCityNamePref(context, appWidgetId);
        boolean useDeviceLocation = NewAppWidgetConfigureActivity.loadUseDeviceLocationPref(context, appWidgetId);

//        location = new Location(context);
//        location.assignPlace();
//
//        return useDeviceLocation ? location.getCityName() : cityName ;
        return cityName;
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        for (int i = 0; i < appWidgetIds.length; i++) {
            citySharedPreferences.deleteWidgetCity(context, appWidgetIds[i]);
        }

        super.onDeleted(context, appWidgetIds);
        this.context = null;
        stopRepeatingTask();
    }
    private void startRepeatingTask()
    {
        updateWidgetTask.run();
    }
    private void stopRepeatingTask()
    {
        updateWidgetHandler.removeCallbacksAndMessages(updateWidgetTask);
    }
    private void updateWidgetCyclic()
    {
        Intent intent = new Intent(context, DataService.class);
        intent.putExtra(IntentExtras.CITY, cityName);
        context.startService(intent);
        updateWidgetHandler.postDelayed(updateWidgetTask, INTERVAL);
    }
}

