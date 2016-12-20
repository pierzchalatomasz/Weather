package com.example.u410.weather;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.gson.Gson;

import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Gson gson = new Gson();

        try {
            endpoint_ = new URL("http://api.openweathermap.org/data/2.5/forecast/city?id=524901&APPID=" + API_KEY);
        }
        catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    private final String API_KEY = "41fae9dcd05e0fcbf176197ce26531e2";

    private URL endpoint_;
}
