package com.example.u410.weather;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;

import com.example.u410.weather.DataSerialization.Current.CurrentWeather;
import com.example.u410.weather.DataSerialization.Forecast.ForecastManager;
import com.example.u410.weather.DataSerialization.Forecast.WeatherForecast;
import com.example.u410.weather.DataSerialization.WeatherIconManager;

import org.parceler.Parcels;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by U410 on 2017-01-28.
 */

public class WeatherWidgetViewUpdater {
    public void init(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId, String cityName) {

        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.weather_widget);

        views.setImageViewResource(R.id.currentWeatherIcon, R.drawable.clouds);
        views.setImageViewResource(R.id.forecast1Icon, R.drawable.clouds);
        views.setImageViewResource(R.id.forecast2Icon, R.drawable.clouds);
        views.setImageViewResource(R.id.forecast3Icon, R.drawable.clouds);
        views.setTextViewText(R.id.city, cityName);

        setOnClickWidgetUpdate(context, views, cityName, appWidgetId);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    public RemoteViews getViews(Context context) {
        return new RemoteViews(context.getPackageName(), R.layout.weather_widget);
    }

    public void update(Context context, Intent intent, AppWidgetManager appWidgetManager, int appWidgetId) {
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.weather_widget);

        updateCurrentWeather(intent, views);
        updateWeatherForecast(intent, views);

        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    private static void setOnClickWidgetUpdate(Context context, RemoteViews views, String cityName, int widgetId) {
        Intent intent = new Intent(context, DataService.class);
        intent.putExtra(IntentExtras.CITY, cityName);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId);

        PendingIntent pendingIntent = PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        views.setOnClickPendingIntent(R.id.mainContainer, pendingIntent);
    }

    private void updateWeatherForecast(Intent intent, RemoteViews views) {
        WeatherForecast weatherForecast = Parcels.unwrap(intent.getParcelableExtra(IntentExtras.WEATHER_FORECAST));
        ForecastManager forecastManager = new ForecastManager(weatherForecast);
        int weatherId, forecastedDay;

        forecastedDay = 1;
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
    }

    private void updateCurrentWeather(Intent intent, RemoteViews views) {
        CurrentWeather currentWeather = Parcels.unwrap(intent.getParcelableExtra(IntentExtras.CURRENT_WEATHER));

        views.setImageViewResource(R.id.currentWeatherIcon, WeatherIconManager.getImageIdByWeatherId(currentWeather.getWeather().get(0).getId()));
        views.setTextViewText(R.id.city, currentWeather.getName());
        views.setTextViewText(R.id.updated, "Update: " + getCurrentTime());
        views.setTextViewText(R.id.currentTemperature, currentWeather.getMain().getTemp_min() + "°C");
        views.setTextViewText(R.id.currentWeatherDescription, currentWeather.getWeather().get(0).getDesc());
        views.setTextViewText(R.id.currentWind, currentWeather.getWind().getSpeed() + " m/s");
        views.setTextViewText(R.id.currentHumidity, currentWeather.getMain().getHumidity() + "%");
        views.setTextViewText(R.id.currentPressure, currentWeather.getMain().getPressure() + " hPa");
    }

    private String getCurrentTime() {
        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
        String dateString = dateFormat.format(date);
        return dateString;
    }
}
