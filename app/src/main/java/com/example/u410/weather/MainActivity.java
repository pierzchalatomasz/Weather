package com.example.u410.weather;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onDestroy() {
        super.onDestroy();
        //if(location_object !=null)
            //location_object.destroyContext();
    }
}
