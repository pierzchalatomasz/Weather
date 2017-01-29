package com.example.u410.weather;

import android.app.IntentService;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Parcelable;
import android.util.Log;
import android.widget.Toast;

import com.example.u410.weather.DataSerialization.Current.CurrentWeather;
import com.example.u410.weather.DataSerialization.Forecast.WeatherForecast;
import com.google.gson.Gson;

import org.apache.http.Header;
import org.apache.http.HeaderIterator;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ProtocolVersion;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpParams;
import org.parceler.Parcels;

import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.util.Locale;


/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 */
public class DataService extends IntentService {
    private Location location;

    public DataService() {
        super("DataService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent == null) {
            return;
        }

        String cityName = intent.getStringExtra(IntentExtras.CITY);

        // XXX
        // Use city name from location if cityName is null
        // Occurs when user adds widget with internet connection closed
        if (cityName == null) {
            location = new Location(getApplicationContext());
            location.assignPlace();
            cityName = location.getCityName();
        }

        int widgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, 0);

        CurrentWeather currentWeather = getCurrentWeather(cityName);
        WeatherForecast weatherForecast = getWeatherForecast(cityName);

        if (currentWeather != null && weatherForecast != null) {
            broadcastData(Parcels.wrap(currentWeather), Parcels.wrap(weatherForecast), widgetId);
        }
    }

    private CurrentWeather getCurrentWeather(String cityName) {
        CurrentWeather currentWeather = null;

        try {
            URL address = new URL(endpoint_ + "/weather?q=" + cityName + "&units=metric&APPID=" + API_KEY);
            HttpResponse response = getResponse(address);
            Reader reader = new InputStreamReader(response.getEntity().getContent());
            currentWeather = gson_.fromJson(reader, CurrentWeather.class);
        }
        catch (Exception e) {
            handleError(e);
        }

        return currentWeather;
    }

    private WeatherForecast getWeatherForecast(String cityName) {
        WeatherForecast weatherForecast = null;

        try {
            URL address = new URL(endpoint_ + "/forecast/daily?q=" + cityName + "&units=metric&cnt=" + forecastDaysNumber_ + "&APPID=" + API_KEY);
            HttpResponse response = getResponse(address);
            Reader reader = new InputStreamReader(response.getEntity().getContent());
            weatherForecast = gson_.fromJson(reader, WeatherForecast.class);
        }
        catch (Exception e) {
            handleError(e);
        }

        return weatherForecast;
    }

    private HttpResponse getResponse(URL address) {
        HttpResponse response = null;

        try {
            HttpClient httpClient = new DefaultHttpClient();
            HttpGet httpget = new HttpGet(address.toString());
            response = httpClient.execute(httpget);
        }
        catch (Exception e) {
            handleError(e);
        }

        return response;
    }

    private void broadcastData(Parcelable currentWeather, Parcelable weatherForecast, int widgetId) {
        Intent intent = new Intent(this, WeatherWidget.class);
        intent.setAction(IntentExtras.WEATHER_UPDATE);

        int ids[] = AppWidgetManager.getInstance(getApplication()).getAppWidgetIds(new ComponentName(getApplication(), WeatherWidget.class));
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);

        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId);

        intent.putExtra(IntentExtras.CURRENT_WEATHER, currentWeather);
        intent.putExtra(IntentExtras.WEATHER_FORECAST, weatherForecast);
        sendBroadcast(intent);
    }

    private void handleError(Exception e) {
        Toast.makeText(getApplicationContext(), "Nieudana próba połączenia!", Toast.LENGTH_SHORT).show();
        e.printStackTrace();
    }

    private String endpoint_ = "http://api.openweathermap.org/data/2.5";

    private String API_KEY = "41fae9dcd05e0fcbf176197ce26531e2";

    private String forecastDaysNumber_ = "4";

    private Gson gson_ = new Gson();
}
