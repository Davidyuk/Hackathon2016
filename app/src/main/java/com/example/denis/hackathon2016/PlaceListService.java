package com.example.denis.hackathon2016;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Locale;

class PlaceListService implements LocationListener {
    static class Place {
        Place(String _name, float _lat, float _lon) {
            name = _name;
            lat = _lat;
            lon = _lon;
        }
        String name;
        float lat, lon;
    }

    private Listener mListener;
    private Location mLocation;

    interface Listener {
        void onPlacesGet(Place[] places);
    }

    PlaceListService(Context context, Listener listener) throws Exception {
        mListener = listener;

        if (Build.VERSION.SDK_INT >= 23 && !(context.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                || context.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)) {
            throw new Exception("No location permission");
        }
        LocationManager locationManager = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 50, 0, this);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 50, 0, this);
        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (location == null)
            location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        if (location != null)
            onLocationChanged(location);
    }

    private String apiUrlTemplate = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=%.5f,%.5f&radius=500&key=AIzaSyCa62qfg6M2rZE9TxHvLTaGetaPzEjSLOU";

    @Override
    public void onLocationChanged(Location location) {
        mLocation = location;
        (new GetDataTask()).execute(String.format(Locale.US, apiUrlTemplate, location.getLatitude(), location.getLongitude()));
    }

    public Location getLocation() {
        return mLocation;
    }

    private class GetDataTask extends AsyncTask<String, Void, String> {
        String lastUrl = "", lastResponse;

        @Override
        protected String doInBackground(String... _url) {
            String response = "";
            if (lastUrl.equals(_url[0])) return lastResponse;
            lastUrl = _url[0];
            try {
                URL url = new URL(_url[0]);

                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line);
                }
                response = buffer.toString();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return lastResponse = response;
        }

        @Override
        protected void onPostExecute(String strJson) {
            super.onPostExecute(strJson);
            try {
                JSONObject dataJsonObj = new JSONObject(strJson);
                JSONArray results = dataJsonObj.getJSONArray("results");

                Place[] places = new Place[results.length()];
                for (int i = 0; i < results.length(); i++) {
                    JSONObject place = results.getJSONObject(i);
                    JSONObject location = place.getJSONObject("geometry").getJSONObject("location");
                    places[i] = new Place(place.getString("name"), (float)location.getDouble("lat"), (float)location.getDouble("lng"));
                }
                mListener.onPlacesGet(places);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onProviderDisabled(String provider) {}
    @Override
    public void onProviderEnabled(String provider) {}
    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {}
}
