package com.bupt.hammad.behave;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class StartActivity extends AppCompatActivity implements SensorEventListener {

    ///////////////////////
    // Declaration space //
    ///////////////////////
    ///////////////////////
    // Create objects /////
    ///////////////////////
    // TimeConverter Class
    TimeConverter timeConverter;
    // Bundle
    Bundle bundle;
    // Adds GPS functionality
    LocationManager locationManager;
    // Adds Accelerometer functionality
    SensorManager sensorManager;
    //
    ///////////////////////
    // Variable declaration space
    ///////////////////////
    // Longs
    //
    //Trip start time, trip end time and total drive time
    private long startTime;
    private long endTime;
    private long driveTime;
    // Strings //
    //
    //Convert drive time into string
    private String stringDriveTime;
    //
    ///////////////////////

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        ////////////////////////////
        // Initialize objects here//
        ////////////////////////////
        //
        // Initializing TimerConverter Class
        timeConverter = new TimeConverter();
        // Initializing Bundle
        bundle = new Bundle();
        // Initializing location and sensor objects
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        //
        // Initializing TextView and Button
        TextView analyzeView = (TextView) findViewById(R.id.analyzeView);
        Button finishButton = (Button) findViewById(R.id.finishButton);
        ////////////////////////////



        // Assign the current time to startTime
        startTime = System.currentTimeMillis();

        // Permission check for GPS
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        // Getting last known location from GPS
        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        // Adding functionality to the finish button
        finishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Saving current system time into end time
                endTime = System.currentTimeMillis();
                // Calculating drive time
                driveTime = endTime - startTime;
                // Converting time from milliseconds format to 00:00:00 and saving into string
                stringDriveTime = timeConverter.formatLongToString(driveTime);
                // Creating an Intent to start result activity
                Intent iResultActivity = new Intent(StartActivity.this, ResultActivity.class);
                // Packaging bundle with data
                bundle.putString("DRIVE_TIME",stringDriveTime);
                // Sending bundle to result activity
                iResultActivity.putExtras(bundle);
                // Starting result activity
                startActivity(iResultActivity);
            }
        });
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {


    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}
