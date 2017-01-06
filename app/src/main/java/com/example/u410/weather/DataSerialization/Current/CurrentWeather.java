package com.example.u410.weather.DataSerialization.Current;

import org.parceler.Parcel;

import java.util.ArrayList;

/**
 * Created by U410 on 2016-12-20.
 */

@Parcel
public class CurrentWeather {
    String name;

    ArrayList<Weather> weather;

    Main main;

    Wind wind;

    public String getName() {
        return name;
    }

    public ArrayList<Weather> getWeather() {
        return weather;
    }

    public Main getMain() {
        return main;
    }

    public Wind getWind() { return wind; }
}

