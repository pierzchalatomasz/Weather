package com.example.u410.weather.DataSerialization.Current;

import org.parceler.Parcel;

/**
 * Created by U410 on 2016-12-25.
 */
@Parcel
public class Weather {

    private int id;

    private String main;

    public int getId() { return id; }

    public String getMain() {
        return main;
    }
}
