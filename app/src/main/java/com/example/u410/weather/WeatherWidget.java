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
    public static String cityName = "Gliwice";

    public WeatherWidget()
    {
        updateWidgetHandler = new Handler();
    }

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.weather_widget);

        views.setImageViewResource(R.id.currentWeatherIcon, R.drawable.clouds);
        views.setImageViewResource(R.id.forecast1Icon, R.drawable.clouds);
        views.setImageViewResource(R.id.forecast2Icon, R.drawable.clouds);
        views.setImageViewResource(R.id.forecast3Icon, R.drawable.clouds);

        // Get city name from configuration activity (null if user selected "Use Device Location")
        String cityName = getWidgetCity(context, appWidgetId) != null ? getWidgetCity(context, appWidgetId) :
                NewAppWidgetConfigureActivity.loadCityNamePref(context, appWidgetId);

        WeatherWidget.cityName = cityName;
        views.setTextViewText(R.id.city, cityName);
        saveWidgetCity(context, appWidgetId, cityName);

        setOnClickWidgetUpdate(context, views, cityName, appWidgetId);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
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

        if (intent != null) {
            if (intent.getAction() == IntentExtras.CURRENT_WEATHER) {
                updateCurrentWeather(context, intent, appWidgetManager, appWidgetId);
            }
            else if (intent.getAction() == IntentExtras.WEATHER_FORECAST) {
                updateWeatherForecast(context, intent, appWidgetManager, appWidgetId);
            }
        }

        super.onReceive(context, intent);
    }

    private void updateCurrentWeather(Context context, Intent intent, AppWidgetManager appWidgetManager, int appWidgetId) {
        CurrentWeather currentWeather = Parcels.unwrap(intent.getParcelableExtra(IntentExtras.CURRENT_WEATHER));

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.weather_widget);
        views.setImageViewResource(R.id.currentWeatherIcon, WeatherIconManager.getImageIdByWeatherId(currentWeather.getWeather().get(0).getId()));
        views.setTextViewText(R.id.city, currentWeather.getName());
        views.setTextViewText(R.id.updated, "Update: " + getCurrentTime());
        views.setTextViewText(R.id.currentTemperature, currentWeather.getMain().getTemp_min() + "°C");
        views.setTextViewText(R.id.currentWeatherDescription, currentWeather.getWeather().get(0).getMain());
        views.setTextViewText(R.id.currentWind, currentWeather.getWind().getSpeed() + " m/s");
        views.setTextViewText(R.id.currentHumidity, currentWeather.getMain().getHumidity() + "%");
        views.setTextViewText(R.id.currentPressure, currentWeather.getMain().getPressure() + " hPa");
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    private String getCurrentTime() {
        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
        String dateString = dateFormat.format(date);
        return dateString;
    }

    private void updateWeatherForecast(Context context, Intent intent, AppWidgetManager appWidgetManager, int appWidgetId) {
        WeatherForecast weatherForecast = Parcels.unwrap(intent.getParcelableExtra(IntentExtras.WEATHER_FORECAST));
        ForecastManager forecastManager = new ForecastManager(weatherForecast);
        int weatherId, forecastedDay;

        forecastedDay = 1;
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.weather_widget);
        views.setTextViewText(R.id.forecast1Day, forecastManager.getDayName(forecastedDay));
        views.setTextViewText(R.id.forecast1Temperature, forecastManager.getTemperature(forecastedDay));
        weatherId = forecastManager.getWeatherId(forecastedDay);
        views.setImageViewResource(R.id.forecast1Icon, WeatherIconManager.getImageIdByWeatherId(weatherId));

        forecastedDay = 2;
        views.setTextViewText(R.id.forecast2Day, forecastManager.getDayName(forecastedDay));
        views.setTextViewText(R.id.forecast2Temperature, forecastManager.getTemperature(forecastedDay));
        weatherId = forecastManager.getWeatherId(forecastedDay);
        views.setImageViewResource(R.id.forecast2Icon, WeatherIconManager.getImageIdByWeatherId(weatherId));

        forecastedDay = 3;
        views.setTextViewText(R.id.forecast3Day, forecastManager.getDayName(forecastedDay));
        views.setTextViewText(R.id.forecast3Temperature, forecastManager.getTemperature(forecastedDay));
        weatherId = forecastManager.getWeatherId(forecastedDay);
        views.setImageViewResource(R.id.forecast3Icon, WeatherIconManager.getImageIdByWeatherId(weatherId));

        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    private static void setOnClickWidgetUpdate(Context context, RemoteViews views, String cityName, int widgetId) {
        Intent intent = new Intent(context, DataService.class);
        intent.putExtra(IntentExtras.CITY, cityName);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId);

        PendingIntent pendingIntent = PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        views.setOnClickPendingIntent(R.id.mainContainer, pendingIntent);
    }

    private static void saveWidgetCity(Context context, int widgetId, String city) {
        SharedPreferences settings = context.getSharedPreferences("CITY", 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(widgetId + "", city);

        editor.commit();
    }

    private static void deleteWidgetCity(Context context, int widgetId) {
        SharedPreferences settings = context.getSharedPreferences("CITY_NAME", 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.remove(widgetId + "");

        editor.commit();
    }

    private static String getWidgetCity(Context context, int widgetId) {
        SharedPreferences settings = context.getSharedPreferences("CITY_NAME", 0);
        return settings.getString(widgetId + "", null);
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
            deleteWidgetCity(context, appWidgetIds[i]);
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

