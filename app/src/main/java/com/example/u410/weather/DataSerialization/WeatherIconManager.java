package com.example.u410.weather.DataSerialization;

import com.example.u410.weather.R;

/**
 * Created by kryguu on 07.01.2017.
 */

public class WeatherIconManager {

    public static int getImageIdByWeatherId(int weatherId) {
        if (isBetween(weatherId,200,299)) return R.drawable.thunder;
        else if (isBetween(weatherId,300,399)) return R.drawable.rain;
        else if (isBetween(weatherId,500,599)) return R.drawable.rain;
        else if (isBetween(weatherId,600,699)) return R.drawable.snow;
        else if (isBetween(weatherId,700,799)) return R.drawable.mist;
        else if (weatherId == 800) return R.drawable.clear;
        else if (weatherId == 801) return R.drawable.clouds;
        else if (isBetween(weatherId,802,899)) return R.drawable.big_clouds;
        else return R.drawable.clouds;
    }

    private static boolean isBetween(int x, int lower, int upper) {
        return lower <= x && x <= upper;
    }

}
