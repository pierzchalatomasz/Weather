package com.example.u410.weather;

import java.util.ArrayList;

/**
 * Created by U410 on 2016-12-20.
 */

public class CurrentWeather {
    private String name;

    private ArrayList<Weather> weather;

    private Main main;

    public String getName() {
        return name;
    }

    public ArrayList<Weather> getWeather() {
        return weather;
    }

    public Main getMain() {
        return main;
    }

    public class Main {
        private int temp_min;

        public int getTemp_min() {
            return temp_min;
        }
    }

    public class Weather {
        private String main;

        public String getMain() {
            return main;
        }
    }
}
