package com.bupt.hammad.behave;

import android.content.Context;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class StartActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        //Adds GPS functionality
        LocationManager locationManager;
        //Adds Accelerometer functionality
        SensorManager sensorManager;

        //Instantiating location and sensor objects
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        Location loc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        //Instantiating TextView and Button
        TextView analyzeView = (TextView) findViewById(R.id.analyzeView);
        Button btnFinish = (Button) findViewById(R.id.btnFinish);

    }
}
