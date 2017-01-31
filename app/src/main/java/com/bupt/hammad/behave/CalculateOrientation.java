package com.bupt.hammad.behave;

import android.hardware.SensorManager;

public class CalculateOrientation {

    // This variable stores the device orientation data in the form of
    // Azimuth z-axis rotation, Pitch x-axis rotation and Roll y-axis rotation
    private float[] orientation = new float[3];
    // This variable stores the Rotation matrix vector
    private float[] rotationMatrix = new float[9];
    // Accelerometer values
    private float[] accelerometerValues = new float[3];
    // Magnetometer values
    private float[] magnetometerValues = new float[3];

    public void Calculate(){
        // Calculate device orientation
        SensorManager.getRotationMatrix(rotationMatrix, null, accelerometerValues, magnetometerValues);
        SensorManager.getOrientation(rotationMatrix,orientation);
    }

    // Setting Values
    public void setAccelerometerValues(float[] accelerometerValues) {
        this.accelerometerValues = accelerometerValues;
    }

    public void setMagnetometerValues(float[] magnetometerValues) {
        this.magnetometerValues = magnetometerValues;
    }

    // Getting Values

    public float[] getOrientation() {
        return orientation;
    }
}
