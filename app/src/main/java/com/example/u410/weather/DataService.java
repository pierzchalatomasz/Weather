package com.example.u410.weather;

import android.app.IntentService;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Parcelable;
import android.util.Log;

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
    public DataService() {
        super("DataService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            String cityName = intent.getStringExtra("CITY");

            handleCurrentWeather(cityName);
            handleWeatherForecast(cityName);
        }
    }

    private void handleCurrentWeather(String cityName) {
        try {
            URL address = new URL(endpoint_ + "/weather?q=" + cityName + "&units=metric&APPID=" + API_KEY);
            HttpResponse response = getResponse(address);
            Reader reader = new InputStreamReader(response.getEntity().getContent());
            CurrentWeather currentWeather = gson_.fromJson(reader, CurrentWeather.class);

            broadcastData(IntentExtras.CURRENT_WEATHER, Parcels.wrap(currentWeather));
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleWeatherForecast(String cityName) {
        try {
            URL address = new URL(endpoint_ + "/forecast/daily?q=" + cityName + "&units=metric&cnt=" + forecastDaysNumber_ + "&APPID=" + API_KEY);
            HttpResponse response = getResponse(address);
            Reader reader = new InputStreamReader(response.getEntity().getContent());
            WeatherForecast weatherForecast = gson_.fromJson(reader, WeatherForecast.class);

            broadcastData(IntentExtras.WEATHER_FORECAST, Parcels.wrap(weatherForecast));
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private HttpResponse getResponse(URL address) {
        HttpResponse response = null;

        try {
            HttpClient httpClient = new DefaultHttpClient();
            HttpGet httpget = new HttpGet(address.toString());
            response = httpClient.execute(httpget);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return response;
    }

    private void broadcastData(String action, Parcelable parcel) {
        Intent intent = new Intent(this, WeatherWidget.class);
        intent.setAction(action);

        int ids[] = AppWidgetManager.getInstance(getApplication()).getAppWidgetIds(new ComponentName(getApplication(), WeatherWidget.class));
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);

        intent.putExtra(IntentExtras.CURRENT_WEATHER, parcel);
        intent.putExtra(IntentExtras.WEATHER_FORECAST, parcel);
        sendBroadcast(intent);
    }

    private String endpoint_ = "http://api.openweathermap.org/data/2.5";

    private String API_KEY = "41fae9dcd05e0fcbf176197ce26531e2";

    private String forecastDaysNumber_ = "4";

    private Gson gson_ = new Gson();
}
