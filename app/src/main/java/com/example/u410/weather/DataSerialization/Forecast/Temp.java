package com.example.u410.weather.DataSerialization.Forecast;

import org.parceler.Parcel;

/**
 * Created by kryguu on 06.01.2017.
 */

@Parcel
public class Temp {

    private double day;

    private double night;

    public int getDayTemp() { return (int)day; }

    public int getNightTemp() { return (int)night; }

}
