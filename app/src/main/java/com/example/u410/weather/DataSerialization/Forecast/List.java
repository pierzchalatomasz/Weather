package com.example.u410.weather.DataSerialization.Forecast;

import com.example.u410.weather.DataSerialization.Current.Weather;

import org.parceler.Parcel;

import java.util.ArrayList;

/**
 * Created by kryguu on 06.01.2017.
 */

@Parcel
public class List {

    private Temp temp;

    private int dt;

    private ArrayList<Weather> weather;

    public Temp getTemp() { return temp; }

    public int getDt() { return dt; }

    public ArrayList<Weather> getWeather() { return weather; }

}
