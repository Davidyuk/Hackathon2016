package com.example.denis.hackathon2016;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.content.Intent;
import android.widget.TextView;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity implements SensorEventListener {
    private SensorManager mSensorManager;
    private Sensor mGravity, mGeomagnetic;
    private float[] mGravityValue, mGeomagneticValue;

    public void gotoFullscreen(View view) {
        Intent intent = new Intent(this, CameraActivity.class);
        startActivity(intent);
    }

    public void gotoPlaceList(View view) {
        Intent intent = new Intent(this, PlaceListActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mGravity = mSensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
        mGeomagnetic = mSensorManager.getDefaultSensor(Sensor.TYPE_GEOMAGNETIC_ROTATION_VECTOR);

        TextView t = (TextView)findViewById(R.id.textView);
        List<Sensor> deviceSensors = mSensorManager.getSensorList(Sensor.TYPE_ALL);
        t.setText(String.format("Sensors count: %d\n", deviceSensors.size()));
        for (Sensor s : deviceSensors) {
            t.append(s.getStringType() + ": " + s.getName() + "\n");
        }
    }

    private void renderMatrix() {
        TextView t = (TextView)findViewById(R.id.textView);
        t.setText(Arrays.toString(mGravityValue));
        t.append("\n\n");
        t.append(Arrays.toString(mGeomagneticValue));
        t.append("\n\n");

        if (mGravityValue != null && mGeomagneticValue != null) {
            float[] rotationMatrix = new float[16];
            float[] inclinationMatrix = new float[16];
            SensorManager.getRotationMatrix(rotationMatrix, inclinationMatrix, mGravityValue, mGeomagneticValue);
            t.append("\nrotationMatrix:\n");
            t.append(Arrays.toString(rotationMatrix));
            t.append("\ninclinationMatrix:\n");
            t.append(Arrays.toString(inclinationMatrix));
        }
    }

    @Override
    public final void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Do something here if sensor accuracy changes.
    }

    @Override
    public final void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_GEOMAGNETIC_ROTATION_VECTOR)
            mGeomagneticValue = event.values;
        if (event.sensor.getType() == Sensor.TYPE_GRAVITY)
            mGravityValue = event.values;
        renderMatrix();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mGravity, SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this, mGeomagnetic, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
