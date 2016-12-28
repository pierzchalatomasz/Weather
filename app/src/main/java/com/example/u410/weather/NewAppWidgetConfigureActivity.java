package com.example.u410.weather;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

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
    private static final String PREF_PREFIX_KEY = "appwidget_";
    int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;

    private String cityName_;

    private PlaceAutocompleteFragment placeAutocompleteFragment;

    View.OnClickListener mOnClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            createNewWidget();
        }
    };

    public NewAppWidgetConfigureActivity() {
        super();
    }

    private void createNewWidget() {
        final Context context = NewAppWidgetConfigureActivity.this;

        // When the button is clicked, store the string locally
        saveCityNamePref(context, mAppWidgetId, cityName_);

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
    static void saveCityNamePref(Context context, int appWidgetId, String text) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.putString(PREF_PREFIX_KEY + appWidgetId, text);
        prefs.apply();
    }

    // Read the prefix from the SharedPreferences object for this widget.
    // If there is no preference saved, get the default from a resource
    static String loadCityNamePref(Context context, int appWidgetId) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        String titleValue = prefs.getString(PREF_PREFIX_KEY + appWidgetId, null);

        return titleValue;
    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        // Set the result to CANCELED.  This will cause the widget host to cancel
        // out of the widget placement if the user presses the back button.
        setResult(RESULT_CANCELED);

        setContentView(R.layout.new_app_widget_configure);
        findViewById(R.id.useDeviceLocation).setOnClickListener(mOnClickListener);
        findViewById(R.id.setTheCity).setOnClickListener(mOnClickListener);

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

