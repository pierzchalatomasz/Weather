package com.example.u410.weather;

import android.Manifest;
import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;

import static android.content.ContentValues.TAG;

/**
 * The configuration screen for the {@link WeatherWidget WeatherWidget} AppWidget.
 */
public class NewAppWidgetConfigureActivity extends Activity {

    private static final String PREFS_NAME = "com.example.u410.weather.WeatherWidget";
    private static final String CITY_KEY = "city_";
    private static final String DEVICE_LOCATION = "device_location_";
    private static final int TAG_CODE_PERMISSION_LOCATION = 0;
    int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;

    private String cityName_;
    private boolean deviceLocation_ = false;

    private PlaceAutocompleteFragment placeAutocompleteFragment;

    private View.OnClickListener setCityClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            createNewWidget();
        }
    };

    private View.OnClickListener setUseDeviceLocationClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            ActivityCompat.requestPermissions(NewAppWidgetConfigureActivity.this, new String[] {
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION },
                    TAG_CODE_PERMISSION_LOCATION);

            deviceLocation_ = true;
            createNewWidget();
        }
    };

    public NewAppWidgetConfigureActivity() {
        super();
    }

    private void createNewWidget() {
        final Context context = NewAppWidgetConfigureActivity.this;

        // When the button is clicked, store the string locally
        savePrefs(context, mAppWidgetId, cityName_, deviceLocation_);

        // It is the responsibility of the configuration activity to update the app widget
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        WeatherWidget.updateAppWidget(context, appWidgetManager, mAppWidgetId);

        // Make sure we pass back the original appWidgetId
        Intent resultValue = new Intent();
        resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
        setResult(RESULT_OK, resultValue);
        finish();
    }

    // Write the prefix to the SharedPreferences object for this widget
    static void savePrefs(Context context, int appWidgetId, String city, boolean deviceLocation) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.putString(CITY_KEY + appWidgetId, city);
        prefs.putBoolean(DEVICE_LOCATION + appWidgetId, deviceLocation);
        prefs.apply();
    }

    // Read the prefix from the SharedPreferences object for this widget.
    // If there is no preference saved, get the default from a resource
    static String loadCityNamePref(Context context, int appWidgetId) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        String titleValue = prefs.getString(CITY_KEY + appWidgetId, null);

        return titleValue;
    }

    static boolean loadUseDeviceLocationPref(Context context, int appWidgetId) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        boolean value = prefs.getBoolean(DEVICE_LOCATION + appWidgetId, false);

        return value;
    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        // Set the result to CANCELED.  This will cause the widget host to cancel
        // out of the widget placement if the user presses the back button.
        setResult(RESULT_CANCELED);

        setContentView(R.layout.new_app_widget_configure);
        findViewById(R.id.useDeviceLocation).setOnClickListener(setUseDeviceLocationClickListener);
        findViewById(R.id.setTheCity).setOnClickListener(setCityClickListener);

        initPlaceAutocompleteFragment();

        // Find the widget id from the intent.
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            mAppWidgetId = extras.getInt(
                    AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        }

        // If this activity was started with an intent without an app widget ID, finish with an error.
        if (mAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish();
            return;
        }
    }

    private void initPlaceAutocompleteFragment() {
        placeAutocompleteFragment = ((PlaceAutocompleteFragment) getFragmentManager()
                .findFragmentById(R.id.place_autocomplete_fragment));
        placeAutocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                findViewById(R.id.setTheCity).setEnabled(true);
                cityName_ = place.getName().toString();
            }

            @Override
            public void onError(Status status) {
                findViewById(R.id.setTheCity).setEnabled(false);
            }
        });
    }
}

