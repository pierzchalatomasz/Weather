package com.example.u410.weather.DataSerialization.Current;

import org.parceler.Parcel;

/**
 * Created by U410 on 2016-12-25.
 */
@Parcel
public class Main {

    int temp_min;

    int pressure;

    int humidity;

    public int getTemp_min() {
        return temp_min;
    }

    public int getPressure() { return pressure; }

    public int getHumidity() { return humidity; }

}
