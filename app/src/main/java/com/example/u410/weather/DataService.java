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
            double lat = 45.294492;
            double lon = 18.671380;

            handleCurrentWeather(lon, lat);
            handleWeatherForecast(lon, lat);
        }
    }

    private void handleCurrentWeather(double lon, double lat) {
        try {
            URL address = new URL(endpoint_ + "/weather?lon=" + lon + "&lat=" + lat + "&units=metric&APPID=" + API_KEY);
            HttpResponse response = getResponse(address);
            Reader reader = new InputStreamReader(response.getEntity().getContent());
            CurrentWeather currentWeather = gson_.fromJson(reader, CurrentWeather.class);

            broadcastData("CURRENT_WEATHER", Parcels.wrap(currentWeather));
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleWeatherForecast(double lon, double lat) {
        try {
            URL address = new URL(endpoint_ + "/forecast?lon=" + lon + "&lat=" + lat + "&units=metric&APPID=" + API_KEY);
            HttpResponse response = getResponse(address);
            Reader reader = new InputStreamReader(response.getEntity().getContent());
            WeatherForecast weatherForecast = gson_.fromJson(reader, WeatherForecast.class);

            broadcastData("WEATHER_FORECAST", Parcels.wrap(weatherForecast));
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private HttpResponse getResponse(URL address) {
        HttpResponse response = new HttpResponse() {
            @Override
            public StatusLine getStatusLine() {
                return null;
            }

            @Override
            public void setStatusLine(StatusLine statusLine) {

            }

            @Override
            public void setStatusLine(ProtocolVersion protocolVersion, int i) {

            }

            @Override
            public void setStatusLine(ProtocolVersion protocolVersion, int i, String s) {

            }

            @Override
            public void setStatusCode(int i) throws IllegalStateException {

            }

            @Override
            public void setReasonPhrase(String s) throws IllegalStateException {

            }

            @Override
            public HttpEntity getEntity() {
                return null;
            }

            @Override
            public void setEntity(HttpEntity httpEntity) {

            }

            @Override
            public Locale getLocale() {
                return null;
            }

            @Override
            public void setLocale(Locale locale) {

            }

            @Override
            public ProtocolVersion getProtocolVersion() {
                return null;
            }

            @Override
            public boolean containsHeader(String s) {
                return false;
            }

            @Override
            public Header[] getHeaders(String s) {
                return new Header[0];
            }

            @Override
            public Header getFirstHeader(String s) {
                return null;
            }

            @Override
            public Header getLastHeader(String s) {
                return null;
            }

            @Override
            public Header[] getAllHeaders() {
                return new Header[0];
            }

            @Override
            public void addHeader(Header header) {

            }

            @Override
            public void addHeader(String s, String s1) {

            }

            @Override
            public void setHeader(Header header) {

            }

            @Override
            public void setHeader(String s, String s1) {

            }

            @Override
            public void setHeaders(Header[] headers) {

            }

            @Override
            public void removeHeader(Header header) {

            }

            @Override
            public void removeHeaders(String s) {

            }

            @Override
            public HeaderIterator headerIterator() {
                return null;
            }

            @Override
            public HeaderIterator headerIterator(String s) {
                return null;
            }

            @Override
            public HttpParams getParams() {
                return null;
            }

            @Override
            public void setParams(HttpParams httpParams) {

            }
        };

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

        intent.putExtra("CURRENT_WEATHER", parcel);
        sendBroadcast(intent);
    }

    private String endpoint_ = "http://api.openweathermap.org/data/2.5";

    private String API_KEY = "41fae9dcd05e0fcbf176197ce26531e2";

    private Gson gson_ = new Gson();
}
