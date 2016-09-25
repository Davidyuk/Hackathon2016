package com.example.denis.hackathon2016;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import java.util.Locale;

public class PlaceListActivity extends AppCompatActivity implements PlaceListService.Listener {
    private TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_list);

        mTextView = (TextView)findViewById(R.id.textView);
        getSystemService(Context.LOCATION_SERVICE);
        try {
            new PlaceListService(this, this);
        } catch (Exception e) {
            mTextView.setText("Error");
        }
    }

    public void onPlacesGet(PlaceListService.Place[] places) {
        for (PlaceListService.Place place : places)
            mTextView.append(String.format(Locale.getDefault(), "%s %f %f\n", place.name, place.lat, place.lon));
    }
}
