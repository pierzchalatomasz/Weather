package com.example.u410.weather.DataSerialization.Forecast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * Created by kryguu on 06.01.2017.
 */

public class ForecastManager {

    private ArrayList<List> days;

    public ForecastManager(WeatherForecast weatherForecast) {

        this.days = weatherForecast.getList();

    }

    public String getTemperatureForDay(int nextDay) {

        try {
            Temp temperatures = days.get(nextDay).getTemp();
            int dayTemp = temperatures.getDayTemp();
            int nightTemp = temperatures.getNightTemp();
            return dayTemp+"/"+nightTemp;
        } catch (Exception e) {
            return "---";
        }

    }

    public String getDayName(int nextDay) {

        try {
            int dateInteger = days.get(nextDay).getDt();
            SimpleDateFormat dateFormat = new SimpleDateFormat("EEE", Locale.US);
            String date = dateFormat.format(new Date(dateInteger * 1000L));
            return date;
        } catch (Exception e) {
            return "---";
        }

    }

}
