package com.example.u410.weather;


import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.widget.Toast;

import java.util.List;
import java.util.Locale;
import static android.content.Context.LOCATION_SERVICE;

public class Location
{
    private Context ctx;
    private String cityName, addressName, countryName, postalCode;
    private LocationManager mLocationManager;
    private android.location.Location lastKnownLocation;
    private Double latitude, longitude;

    public Location(Context context)
    {
        ctx = context;
        mLocationManager = (LocationManager) ctx.getSystemService(LOCATION_SERVICE);
    }

    public String getCityName(){ return cityName; }
    public String getAddressName(){ return addressName; }
    public String getCountryName() { return countryName; }
    public String getPostalCode() { return postalCode; }

    public void assignPlace()
    {
        findCoordinates();
        findPlace();
    }

    public void findCoordinates()
    {
        try
        {
            lastKnownLocation = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            if(lastKnownLocation!=null)
            {
                latitude = lastKnownLocation.getLatitude();
                longitude = lastKnownLocation.getLongitude();
            }
            else
            {
                latitude = 0.0;
                longitude = 0.0;
            }
        }
       catch(SecurityException e)
       {
            e.printStackTrace();
       }
    }

    public void findPlace()
    {
        try
        {
            Geocoder geocoder = new Geocoder(ctx, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);

            cityName = addresses.get(0).getLocality();
            addressName = addresses.get(0).getAddressLine(0);
            countryName = addresses.get(0).getCountryName();
            postalCode = addresses.get(0).getPostalCode();
        }
        catch(Exception e)
        {
            Toast.makeText(ctx, "Próba ustalenia lokalizacji urządzenia zakończona niepowodzeniem!", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    public void destroyContext()
    {
        ctx = null;
    }
}
