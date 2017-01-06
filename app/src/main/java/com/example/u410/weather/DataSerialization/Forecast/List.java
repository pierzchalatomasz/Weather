package com.example.u410.weather.DataSerialization.Forecast;

import org.parceler.Parcel;

/**
 * Created by kryguu on 06.01.2017.
 */

@Parcel
public class List {

    private Temp temp;

    private int dt;

    public Temp getTemp() { return temp; }

    public int getDt() { return dt; }

}
