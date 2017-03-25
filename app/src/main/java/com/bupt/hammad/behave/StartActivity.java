package com.bupt.hammad.behave;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.FileOutputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

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
    //
    Location location;
    // Adds Accelerometer functionality
    SensorManager sensorManager;
    //
    Sensor accelerometer;
    Sensor magnetometer;
    Sensor gyroscope;
    //
    // Moving averages
    MovingAverage movingAvgGyroYawRate;
    MovingAverage movingAvgDirection;
    MovingAverage movingAvgAcceleration;
    //
    CalculateOrientation findOrientation = new CalculateOrientation();
    ///////////////////////
    // Variable declaration space
    ///////////////////////
    // Longs
    //
    //Trip start time, trip end time and total ride time
    private long startTime;
    private long endTime;
    private long rideTime;
    private long gyroRoll;
    long modGyroRoll = 0;
    // Strings //
    //
    //Convert ride time into string
    private String stringRideTime;
    //
    // int //
    int leaningCounter = 0;
    int showFlag = 0;
    //
    // float //
    private float speed;
    private float speedLatter = 0;
    private float speedFormer;
    ///////////////////////

    // TextViews //
    TextView orientationRoll;
    TextView leanView;
    TextView hudView;
    // ImageViews //
    ImageView imageView;


    //Gets the Yaw rate from gyroscope
    public float gyroYawRate = (float) 0;
    public float gyroYawRate2DF;

    //////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////
    //NO BUMP state is when the car is riding straight
    final static int No_Bump = 0;
    //ONE BUMP state is when the car takes a left/right turn
    final static int One_Bump = 1;
    //WAITING FOR BUMP state is fulfilled when left/right lane change is about to happen
    final static int Waiting_for_Bump = 2;

    //delta L value for filtering out lower noise
    final static float deltaL = (float) 0.1;
    //delta H value for filtering out higher noise
    final static float deltaH = (float) 0.2;

    float z = (float) 1.0;
    final static float t = (float) 0.6;
    final static float T_NEXT_DELAY = (float) 2.0;

    float ACC_OF_BRAKE = (float) -2.0;
    float EMERGENCY = (float) -5.0;

    public float ALL_OF_BUMP = 0;
    public float BAD_TURN_1 = 0;
    public float BAD_TURN_2 = 0;
    public float BAD_TURN = 0;
    public float averageVelocity;
    public float averageVelocity2DF;

    // Acceleration from accelerometer
    public double accelerometerAcceleration;
    public double accelerometerAcceleration2DF;
    public double geoFrameAcceleration;//acceleration in the geo-frame
    public double averalinear1;
    public double averacc;
    public double averacc2DF;
    public double hpaveracc;
    public double hpaveracc2DF;
    public int FLAG_OF_BRAKE = 0;
    public float MAX_OF_BRAKE = 0;

    // Data collection variables
    public float numberOfLeftTurns = 0;
    public float numberOfRightTurns = 0;
    public float numberOfUTurns = 0;
    public float numberOfDangerousLeftLeans = 0;
    public float numberOfDangerousRightLeans = 0;
    public float numberOfBrakes = 0;
    public float numberOfEmergencyBrakes = 0;

    public int ShowFlag = 0;
    public static int start = 0;
    public static int begin= 0;
    public static int end, end2 = 0;
    public float max;
    public float max1;
    public float T_BUMP, T_BUMP2, T_dwell = 0;

    //To save NO BUMP state
    public int state = No_Bump;
    public int start_of_2nd_bump = 0;
    public float distance;
    public float ang;
    public float tDistance;
    public float calculateAngle;
    public float KalmanFilterSpeed;
    public double calculateOrientation;
    public float[] vs = new float[200];
    public float[] speeds = new float[200];
    public float[] velocitys = new float[200];

    Timer timer = new Timer();
    // Get the three dimensional values from accelerometer
    public float[] accelerometerValues = new float[3];

    // Get three-dimensional values from magnetometer
    public float[] magneticFieldValues = new float[3];

    // This variable stores the device orientation data in the form of
    // Azimuth z-axis rotation, Pitch x-axis rotation and Roll y-axis rotation
    public float[] orientation = new float[3];

    // Gyroscope Values
    private float[] gyroscopeValues;

    public String mode;
    public static String filename = "Vehicle Sensing";

    // Images
    public int[] images = new int[]{
            R.drawable.empty_view,
            R.drawable.warning
    };

    DataSet dataset = new DataSet();
    private Context context;
    ///////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        orientationRoll = (TextView) findViewById(R.id.orientationRoll);
        leanView = (TextView) findViewById(R.id.lean_text_view);
        hudView = (TextView) findViewById(R.id.hud_text_view);
        imageView = (ImageView) findViewById(R.id.imageView);

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
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);

        ACC_OF_BRAKE = -2.0f;
        EMERGENCY = -5.0f;
        z = 1.0f;

        // Initializing moving averages
        movingAvgGyroYawRate = new MovingAverage(15);
        movingAvgDirection = new MovingAverage(5);
        movingAvgAcceleration = new MovingAverage(3);

        hudView.setText("Riding Straight");

        // GPS location permission check
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        // Gets the current location from GPS
        if (location == null){
            location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        }

        // Request location from GPS and find speed
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                1000, 0.2f, new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                updateSpeed(location);
                speedFormer = speedLatter;
                speedLatter = speed;
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {
                try {
                    updateSpeed(locationManager.getLastKnownLocation(provider));
                }catch (SecurityException e){
                    Log.d("Security Error","Security Error");
                }
            }
            @Override
            public void onProviderDisabled(String provider) {
                updateSpeed(null);
            }
        });

        // For avoiding null pointer exception from GPS location
        if (location == null) {
            location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        }else{
            updateSpeed(location);
        }

        // Initializing Button
        final Button finishButton = (Button) findViewById(R.id.finishButton);

        // Assign the current time to startTime
        startTime = System.currentTimeMillis();

        // Timers
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                dataProcess();
            }
        },0,50);
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.sendEmptyMessage(0x110);
            }
        },0,1000);
        // FINISH BUTTON //
        // Adding functionality to the finish button
        finishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Saving current system time into end time
                endTime = System.currentTimeMillis();
                // Calculating ride time
                rideTime = endTime - startTime;
                // Converting time from milliseconds format to 00:00:00 and saving into string
                stringRideTime = TimeConverter.formatLongToString(rideTime);
                // Creating an Intent to start result activity
                Intent resultActivityIntent = new Intent(StartActivity.this, ResultActivity.class);
                // Packaging bundle with data
                bundle.putString("RIDE_TIME", stringRideTime);
                bundle.putFloat("LEFT_TURNS", numberOfLeftTurns);
                bundle.putFloat("RIGHT_TURNS", numberOfRightTurns);
                bundle.putFloat("U_TURNS", numberOfUTurns);
                bundle.putFloat("DANGEROUS_LEFT_LEAN", numberOfDangerousLeftLeans);
                bundle.putFloat("DANGEROUS_RIGHT_LEAN", numberOfDangerousRightLeans);
                // Sending bundle to result activity
                resultActivityIntent.putExtras(bundle);
                // Starting result activity
                startActivity(resultActivityIntent);
                finish();
            }
        });
    }

    // Variable to hold the acceleration value from GPS
    private float accelerationFromGps;

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 0x110) {
                accelerationFromGps = speedLatter - speedFormer;
            }else if (msg.what == 0x234){
                switch ((String) msg.obj){
                    case "Leaning Left":
                        leanView.setText("Leaning Left");
                        showFlag = 0;
                        break;
                    case "Leaning Right":
                        leanView.setText("Leaning Right");
                        showFlag = 0;
                        break;
                    case "Dangerous Left Lean":
                        hudView.setText("Dangerous Lean");
                        imageView.setImageResource(images[1]);
                        showFlag = 0;
                        break;
                    case "Dangerous Right Lean":
                        hudView.setText("Dangerous Lean");
                        imageView.setImageResource(images[1]);
                        showFlag = 0;
                        break;
                    case "No Lean":
                        leanView.setText("Not Leaning");
                        showFlag = 0;
                        break;
                    case "Left Turn":
                        hudView.setText("Left Turn");
                        ShowFlag = 0;
                        break;
                    case "Right Turn":
                        hudView.setText("Right Turn");
                        ShowFlag = 0;
                        break;
                    case "U-Turn":
                        hudView.setText("U-Turn");
                        ShowFlag = 0;
                        break;
                    case "Brake":
                        hudView.setText("Brake");
                        break;
                    case "Emergency Brake":
                        hudView.setText("Emergency Brake");
                        break;
                    case "Remove Image":
                        imageView.setImageResource(images[0]);
                        break;
                    case "Riding Straight":
                        hudView.setText("Riding Straight");
                        break;
                }
            }
        }
    };

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        // A float array that can store values from various sensors
        float[] sensorValues = sensorEvent.values;
        double save = 0;
        int sensorType = sensorEvent.sensor.getType();

        switch (sensorType){
            case Sensor.TYPE_ACCELEROMETER:
                findOrientation.setAccelerometerValues(sensorValues);
                accelerometerValues = sensorValues;
                break;
            case Sensor.TYPE_MAGNETIC_FIELD:
                findOrientation.setMagnetometerValues(sensorValues);
                magneticFieldValues = sensorValues;
                sensorValues[0] = (float)Math.toDegrees(sensorValues[0]);
                break;
            case Sensor.TYPE_LINEAR_ACCELERATION:
                // Z-axis acceleration
                // values[2] represent the z-axis
                accelerometerAcceleration = sensorValues[2];

                movingAvgAcceleration.setValue((float) accelerometerAcceleration);
                averacc= movingAvgAcceleration.getValue();
                float csc=(float) Math.sin(calculateOrientation());
                if(csc==0){
                    csc=(float) 0.1;
                }
                // Initialization of geoFrameAcceleration
                // Horizontal acceleration
                geoFrameAcceleration = averacc/csc;
                if(Math.abs(geoFrameAcceleration)>=10){
                    geoFrameAcceleration =save;
                }else{
                    save= geoFrameAcceleration;
                }

                DecimalFormat df1 = new DecimalFormat("#.00");

                accelerometerAcceleration2DF = Double.valueOf(df1.format(accelerometerAcceleration));
                averacc2DF = Double.valueOf(df1.format(averacc));
                hpaveracc2DF = Double.valueOf(df1.format(hpaveracc));
                // Initialization of averalinear1
                averalinear1 = Double.valueOf(df1.format(geoFrameAcceleration));

                break;
            case Sensor.TYPE_GYROSCOPE:
                gyroscopeValues = sensorEvent.values;
                gyroYawRate = (float) Math.sqrt(gyroscopeValues[1]*gyroscopeValues[1]+gyroscopeValues[2]*gyroscopeValues[2]);
                // To make the values positive
                if((gyroscopeValues[1]+gyroscopeValues[2])<0){
                    gyroYawRate = 0 - gyroYawRate;
                }
                break;
            case Sensor.TYPE_ROTATION_VECTOR:

                String message = new String();
                DecimalFormat df = new DecimalFormat("#,##0.000");

                float X = sensorValues[0];
                float Y = sensorValues[1];
                float Z = sensorValues[2];

                message += df.format(X) + "  ";
                message += df.format(Y) + "  ";
                message += df.format(Z) +  "\n";
                break;
        }
        findOrientation.Calculate();
        float[] rollOrientation;
        rollOrientation = findOrientation.getOrientation();
        gyroRoll = Math.round(Math.toDegrees(rollOrientation[2]));
        if (gyroRoll < 0){
            modGyroRoll = 0 - gyroRoll;
        }else{
            modGyroRoll = gyroRoll;
        }
        orientationRoll.setText(""+modGyroRoll+"°");
        View activityStart = findViewById(R.id.activity_start);
//        if(modGyroRoll > 35){
//            activityStart.setBackgroundColor(Color.parseColor("#F44336"));
//        }else{
//            activityStart.setBackgroundColor(Color.parseColor("#FF3F51B5"));
//        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    private void dataProcess() {
        Message resultMessage = new Message();
        movingAvgGyroYawRate.setValue(gyroYawRate);
        gyroYawRate = movingAvgGyroYawRate.getValue();
        vs[start] = gyroYawRate;

        averageVelocity += geoFrameAcceleration * 0.05;
        DecimalFormat df = new DecimalFormat("#.00");
        averageVelocity2DF = Float.valueOf(df.format(averageVelocity));

        velocitys[start] = averageVelocity;
        KalmanFilterSpeed = algorithmKalmanFilter();
        speeds[start] = speed;
        start = (start + 1) % 200;   // start++ until 200, after 200 start again from 0


        tDistance = Math.abs(getDistance(speeds, vs, (start + 199) % 200, start));//Change to start
        calculateAngle = angle_calculate(vs, (start + 199) % 200, start);//Change to start
        calculateOrientation = calculateOrientation();


        distance = Math.abs(getDistance(speeds, vs, begin, start));
        ang = angle_calculate(vs, begin, start);


        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd   HH:mm:ss");
        String simpleDateFormatString = simpleDateFormat.format(new Date());

        writeSDCard(simpleDateFormatString + "\t" + accelerometerAcceleration2DF + "\t" + averacc2DF + "\t"
                + averalinear1 + "\t" + accelerationFromGps + "\t" + averageVelocity2DF
                + "\t" + speed + "\t" + KalmanFilterSpeed + "\t" + gyroYawRate2DF + "\t"
                + movingAvgGyroYawRate.getValue() + "\t" + ang + "\t" + calculateOrientation + "\t"
                + distance + "\t" + calculateAngle + "\t" + tDistance + "\t" + "\n");
        //Determine the brake
        if (averalinear1 <= ACC_OF_BRAKE) {
            FLAG_OF_BRAKE++;
            if (averalinear1 < MAX_OF_BRAKE) {
                MAX_OF_BRAKE = (float) averalinear1;
            }
        } else if (averalinear1 > ACC_OF_BRAKE) {
            if (FLAG_OF_BRAKE > 2) {
                resultMessage.what = 0x234;
                resultMessage.obj = "Brake";
                handler.sendMessage(resultMessage);
                numberOfBrakes++;
                if (MAX_OF_BRAKE < EMERGENCY) {
                    resultMessage.what = 0x234;
                    resultMessage.obj = "Emergency Brake";
                    handler.sendMessage(resultMessage);
                    numberOfEmergencyBrakes++;
                }
            }
            MAX_OF_BRAKE = 0;
            FLAG_OF_BRAKE = 0;
        }


        if (gyroRoll == 0 ){
            resultMessage.what = 0x234;
            resultMessage.obj = "No Lean";
            handler.sendMessage(resultMessage);
        }else if (gyroRoll < 0){
            resultMessage.what = 0x234;
            resultMessage.obj = "Leaning Left";
            handler.sendMessage(resultMessage);
            if (Math.abs(gyroRoll) > 35){
                resultMessage = new Message();
                resultMessage.what = 0x234;
                resultMessage.obj = "Dangerous Left Lean";
                handler.sendMessage(resultMessage);
                numberOfDangerousLeftLeans++;
            }else if (Math.abs(gyroRoll)<35){
                resultMessage = new Message();
                resultMessage.what = 0x234;
                resultMessage.obj = "Remove Image";
                handler.sendMessage(resultMessage);
            }
        }else if (gyroRoll > 0){
            resultMessage.what = 0x234;
            resultMessage.obj = "Leaning Right";
            handler.sendMessage(resultMessage);
            if (Math.abs(gyroRoll) > 35){
                resultMessage = new Message();
                resultMessage.what = 0x234;
                resultMessage.obj = "Dangerous Right Lean";
                handler.sendMessage(resultMessage);
                numberOfDangerousRightLeans++;
            }else if (Math.abs(gyroRoll)<35){
                resultMessage = new Message();
                resultMessage.what = 0x234;
                resultMessage.obj = "Remove Image";
                handler.sendMessage(resultMessage);
            }
        }


        //If in NO BUMP state and the
        //Math.abs returns the positive value
        //Gyro readings greater than deltaL, into a bump state
        if (state == No_Bump && Math.abs(gyroYawRate) > deltaL) {
            state = One_Bump;

            begin = start;
            max = gyroYawRate;

        } else if (state == One_Bump && Math.abs(gyroYawRate) > deltaL) {      //At this time, a bump state is obtained
            if (Math.abs(gyroYawRate) > z) {
                BAD_TURN_1 = 1;
            }

            T_BUMP = T_BUMP + (float) 0.05;                 //The dwell time of the first bump
            if (Math.abs(gyroYawRate) > Math.abs(max)) {
                max = gyroYawRate;
            }                               //Calculate the maximum yaw rate during the measurement
        } else if (state == One_Bump && Math.abs(gyroYawRate) <= deltaL) {
            end = start;


            if (Math.abs(max) > deltaH && T_BUMP > t) {

                ALL_OF_BUMP++;
                BAD_TURN += BAD_TURN_1;
                max1 = max;
                state = Waiting_for_Bump;
                max = 0;

                if (Math.abs(angle_calculate(vs, begin, end)) > 135) {
                    resultMessage = new Message();
                    resultMessage.what = 0x234;
                    resultMessage.obj = getString(R.string.u_turn_2);
                    handler.sendMessage(resultMessage);
                    numberOfUTurns++;
                    state=No_Bump;
                    writeSDCard(simpleDateFormatString + "\t" + getString(R.string.u_turn_2) + "\t" + gyroYawRate2DF + "\t"
                            + movingAvgGyroYawRate.getValue() + "\t" + angle_calculate(vs, begin, end)
                            + "\t" + 0.05*(end-begin)+ "\t" + getDistance(speeds, vs, begin, end)
                            + "\n");
                }

            } else {
                //If the three conditions are not met, it is determined that the bump
                // just detected is invalid, the algorithm returns no bump state
                T_BUMP = 0;
                state = No_Bump;
                max = 0;
                BAD_TURN_1 = 0;
                resultMessage = new Message();
                resultMessage.what = 0x234;
                resultMessage.obj = "Riding Straight";
                handler.sendMessage(resultMessage);
            }

        } else if (state == Waiting_for_Bump) {
            //If this is the case to enter the wait for the bump state
            if (Math.abs(gyroYawRate) <= deltaL && start_of_2nd_bump == 0) {
                T_dwell = T_dwell + (float) 0.05;
                //Calculate the time between the second deltaL value of the previous bump and the first deltaL value of the next bump
            }

            //If the time interval between two deltaL more than 2 seconds will not play finished
            if (T_dwell < T_NEXT_DELAY && Math.abs(gyroYawRate) > deltaL) {   //如果两个凸点之间的时间间隔小于陀螺仪读数的最大停留时间，且陀螺仪读数大于s，进入第二个凸点状态

                T_BUMP2 = T_BUMP2 + (float) 0.05;            //第二个凸点的停留时间
                start_of_2nd_bump = 1;                   //开启第二个凸点状态
                if (Math.abs(gyroYawRate) > z) {
                    BAD_TURN_2 = 1;
                }
                if (Math.abs(gyroYawRate) > Math.abs(max)) {          //计算第二个凸点状态中的最大偏航率
                    max = gyroYawRate;
                }

            } else if (Math.abs(gyroYawRate) <= deltaL && start_of_2nd_bump == 1) {    //算法已经进入第二个凸点状态，且已经结束 未必是凸点
                end2 = start;

                //有效的凸点
                if (Math.abs(max) > deltaH && T_BUMP2 > t) {
                    //5月4日 删除了距离的下限 只要第二个凸点结束就会提示变道

                    ALL_OF_BUMP++;
                    BAD_TURN += BAD_TURN_2;

                    //两个反向凸点区分变道、在弯曲的道路上
                    if (max * max1 > 0) {
                        if (angle_calculate(vs, begin, end2) <= -60 && angle_calculate(vs, begin, end2) >= -135) {
                            //Toast.makeText(MainActivity.this, "Turn Right finished", Toast.LENGTH_SHORT).show();
                            resultMessage = new Message();
                            resultMessage.what = 0x234;
                            resultMessage.obj = "Right Turn";
                            handler.sendMessage(resultMessage);
                            numberOfRightTurns++;
                            writeSDCard(simpleDateFormatString + "\t" + "Right Turn"
                                    + "\t" + gyroYawRate2DF + "\t" + movingAvgGyroYawRate.getValue() + "\t"
                                    + angle_calculate(vs, begin, end) + "\t" + 0.05*(end-begin)
                                    + "\n");
                        } else if (angle_calculate(vs, begin, end2) <= 135 && angle_calculate(vs, begin, end2) >= 60) {
                            resultMessage = new Message();
                            resultMessage.what = 0x234;
                            resultMessage.obj = "Left Turn";
                            handler.sendMessage(resultMessage);
                            numberOfLeftTurns++;
                            writeSDCard(simpleDateFormatString + "\t" + "Left Turn" + "\t"
                                    + gyroYawRate2DF + "\t" + movingAvgGyroYawRate.getValue() + "\t" +
                                    + angle_calculate(vs, begin, end) + "\t" + 0.05*(end-begin)
                                    + "\n");
                        } else if (Math.abs(angle_calculate(vs, begin, end)) > 135) {
                            resultMessage = new Message();
                            resultMessage.what = 0x234;
                            resultMessage.obj = getString(R.string.u_turn_2);
                            handler.sendMessage(resultMessage);
                            numberOfUTurns++;
                            writeSDCard(simpleDateFormatString + "\t" + getString(R.string.u_turn_2) + "\t" + gyroYawRate2DF
                                    + "\t" + movingAvgGyroYawRate.getValue() + "\t"
                                    + angle_calculate(vs, begin, end) + "\t" + 0.05*(end-begin)
                                    + "\t" + getDistance(speeds, vs, begin, end) + "\n");
                        }
                    }
//        						}
                }
                //Invalid bump
                else {
                    if (angle_calculate(vs, begin, end) <= -60 && angle_calculate(vs, begin, end2) >= -135) {
                        resultMessage = new Message();
                        resultMessage.what = 0x234;
                        resultMessage.obj = "Right Turn";
                        handler.sendMessage(resultMessage);
                        numberOfRightTurns++;
                        writeSDCard(simpleDateFormatString + "\t" + "Right Turn" + "\t" + gyroYawRate2DF
                                + "\t" + movingAvgGyroYawRate.getValue() + "\t"
                                + angle_calculate(vs, begin, end) + "\t" + 0.05*(end-begin) + "\n");
                    } else if (angle_calculate(vs, begin, end) <= 135 && angle_calculate(vs, begin, end2) >= 60) {
                        resultMessage = new Message();
                        resultMessage.what = 0x234;
                        resultMessage.obj = "Left Turn";
                        handler.sendMessage(resultMessage);
                        numberOfLeftTurns++;
                        writeSDCard(simpleDateFormatString + "\t" + "Left Turn" + "\t" + gyroYawRate2DF + "\t"
                                + movingAvgGyroYawRate.getValue() + "\t" + angle_calculate(vs, begin, end)
                                + "\t" + 0.05*(end-begin) + "\n");
                    } else if (Math.abs(angle_calculate(vs, begin, end)) > 135) {
                        //Toast.makeText(MainActivity.this, "Turn Back", Toast.LENGTH_SHORT).show();
                        resultMessage = new Message();
                        resultMessage.what = 0x234;
                        resultMessage.obj = getString(R.string.u_turn_2);
                        handler.sendMessage(resultMessage);
                        numberOfUTurns++;
                        writeSDCard(simpleDateFormatString + "\t" + getString(R.string.u_turn_2) + "\t" + gyroYawRate2DF + "\t"
                                + movingAvgGyroYawRate.getValue() + "\t" + angle_calculate(vs, begin, end)
                                + "\t" + 0.05*(end-begin) + "\t" + getDistance(speeds, vs, begin, end)
                                + "\n");
                    } else {
                        resultMessage = new Message();
                        resultMessage.what = 0x234;
                        resultMessage.obj = "Riding Straight";
                        handler.sendMessage(resultMessage);
                    }
                }

                T_BUMP = 0;
                T_BUMP2 = 0;
                state = No_Bump;
                max = 0;
                max1 = 0;
                T_dwell = 0;
                start_of_2nd_bump = 0;
                //Restore to the original state
                BAD_TURN_1 = 0;
                BAD_TURN_2 = 0;

            } else if (T_dwell >= T_NEXT_DELAY) {
                end2 = start;

                //At this time, it is determined that there is only one bump
                if (angle_calculate(vs, begin, end) < -60 && angle_calculate(vs, begin, end) >= -135) {
                    //Toast.makeText(MainActivity.this, "Turn Right finished", Toast.LENGTH_SHORT).show();
                    resultMessage = new Message();
                    resultMessage.what = 0x234;
                    resultMessage.obj = "Right Turn";
                    handler.sendMessage(resultMessage);
                    numberOfRightTurns++;
                    writeSDCard(simpleDateFormatString + "\t" + "Right Turn" + "\t" + gyroYawRate2DF + "\t"
                            + movingAvgGyroYawRate.getValue() + "\t" + angle_calculate(vs, begin, end)
                            + "\t" + 0.05*(end-begin) + "\t" + "One_bump" + "\n");
                } else if (angle_calculate(vs, begin, end) <= 135 && angle_calculate(vs, begin, end) > 60) {
                    //Toast.makeText(MainActivity.this, "Turn Left finished", Toast.LENGTH_SHORT).show();
                    resultMessage = new Message();
                    resultMessage.what = 0x234;
                    resultMessage.obj = "Left Turn";
                    handler.sendMessage(resultMessage);
                    numberOfLeftTurns++;
                    writeSDCard(simpleDateFormatString + "\t" + "Left turn" + "\t" + gyroYawRate2DF + "\t"
                            + movingAvgGyroYawRate.getValue() + "\t" + angle_calculate(vs, begin, end)
                            + "\t" + 0.05*(end-begin) + "\t" + "One_bump" + "\n");
                } else if (Math.abs(angle_calculate(vs, begin, end)) > 135) {
                    //Toast.makeText(MainActivity.this, "Turn Back", Toast.LENGTH_SHORT).show();
                    resultMessage = new Message();
                    resultMessage.what = 0x234;
                    resultMessage.obj = getString(R.string.u_turn_2);
                    handler.sendMessage(resultMessage);
                    writeSDCard(simpleDateFormatString + "\t" + getString(R.string.u_turn_2) + "\t" + gyroYawRate2DF + "\t"
                            + movingAvgGyroYawRate.getValue() + "\t" + angle_calculate(vs, begin, end)
                            + "\t" + 0.05*(end-begin) + "\t" + getDistance(speeds, vs, begin, end)
                            + "\t" + "One_bump" + "\n");
                    //At this point the car U-turn, should be accompanied by turn signal lights
                } else {
                    resultMessage = new Message();
                    resultMessage.what = 0x234;
                    resultMessage.obj = "Riding Straight";
                    handler.sendMessage(resultMessage);
                }

                T_BUMP = 0;
                T_BUMP2 = 0;
                state = No_Bump;
                T_dwell = 0;
                max = 0;
                max1 = 0;
                start_of_2nd_bump = 0;
                BAD_TURN_1 = 0;
                BAD_TURN_2 = 0;

            }
        }
    }

    public float getDistance(float ve[], float angularVelocityGyro[], int begin, int end) {
        float horizontalDisplacement = 0;
//        float d1 = 0;
        float angle = 0;

        if (end >= begin) {
            for (int i = begin; i < end; i++) {
                // angle = angle before * time (100ms/0.1s)The angle is equal to the original angle
                // plus the gyro angular velocity multiplied by 0.1 seconds
                angle = angle + angularVelocityGyro[i] * (float) 0.05;
                //horizontal displacement The horizontal distance is equal to the velocity multiplied by the time in 0.1 seconds
                // times the sin value
                horizontalDisplacement = horizontalDisplacement + ve[i] * (float) 0.05 * (float) Math.sin(angle);
            }
        } else if (end < begin) {
            for (int i = begin; i < 200; i++) {
                //angle=angle before * time (100ms/0.1s)The angle is equal to the original angle
                // plus the gyro angular velocity multiplied by 0.1 seconds
                angle = angle + angularVelocityGyro[i] * (float) 0.05;
                //horizontal displacement The horizontal distance is equal to the velocity multiplied by the time in 0.1 seconds
                // times the sin value
                horizontalDisplacement = horizontalDisplacement + ve[i] * (float) 0.05 * (float) Math.sin(angle);
            }
            for (int i = 0; i <= end; i++) {
                //angle=angle before * time (100ms/0.1s) The angle is equal to the original angle
                // plus the gyro angular velocity multiplied by 0.1 seconds
                angle = angle + angularVelocityGyro[i] * (float) 0.05;
                //horizontal displacement The horizontal distance is equal to the velocity multiplied by the time in 0.1 seconds
                // times the sin value
                horizontalDisplacement = horizontalDisplacement + ve[i] * (float) 0.05 * (float) Math.sin(angle);
            }
        }

//        DecimalFormat df = new DecimalFormat("#.00");
//        d1 = Float.valueOf(df.format(horizontalDisplacement));

        return horizontalDisplacement;
    }

    //The return value is the angle
    public float angle_calculate(float[] vs, int b, int e) {
        float angle = 0;
        float angle2DF;
        if (e >= b) {
            for (int n = b; n <= e; n++) {
                angle = (float) (angle + vs[n] * 0.05 * 57.29578);
            }
        } else if (e < b) {
            for (int n = b; n < 200; n++) {
                angle = (float) (angle + vs[n] * 0.05 * 57.29578);
            }
            for (int n = 0; n <= e; n++) {
                angle = (float) (angle + vs[n] * 0.05 * 57.29578);
            }
        }

        DecimalFormat df2 = new DecimalFormat("#.00");
        angle2DF = Float.valueOf(df2.format(angle));

        return angle2DF;
    }

    // Method for calculating the orientation of the device
    public double calculateOrientation() {
        float[] R;
        R = new float[9];
        double averageOrientation;
        double orientation;
        SensorManager.getRotationMatrix(R, null, accelerometerValues, magneticFieldValues);
        SensorManager.getOrientation(R, this.orientation);
        movingAvgDirection.setValue(this.orientation[1]);
        averageOrientation = movingAvgDirection.getValue();
        DecimalFormat df = new DecimalFormat("#.00");
        orientation = Double.valueOf(df.format(averageOrientation));
        return orientation;
    }

    public void updateSpeed(Location location) {
        if (location != null) {
            // Gets the speed from the GPS
            speed = location.getSpeed();
        }
    }

    // Kalman filter algorithm for getting accurate sensor data
    // This function doesn't take any input parameters and returns the estimated velocity
    public float algorithmKalmanFilter() {
        // What is this?
        float x_10;
        // Error in estimate
        float p_10;
        // Kalman Gain
        float K;
        // What is this?
        float x_11;
        // What is this?
        float p_11;
        // This will return the estimated velocity after applying Kalman filter
        float estimatedVelocity;
        x_10 = (float) (dataset.getX_00() + 0.05 * averalinear1);
        p_10 = dataset.getP_00() + dataset.getQ();
        K = p_10 / (p_10 + dataset.getR());
        x_11 = x_10 + K * (speed - x_10);
        dataset.setX_00(x_11);
        p_11 = p_10 - K * p_10;
        dataset.setP_00(p_11);
        DecimalFormat df = new DecimalFormat("#.00");
        estimatedVelocity = Float.valueOf(df.format(x_11));
        dataset.kAll = K;
        return estimatedVelocity;
    }

    //need to be finished
    private void writeSDCard(String string) {
        try {
            Log.d("SDcard", "exists");
            // if the SDcard exists Judge whether there is SD card
            if (Environment.getExternalStorageState().equals(
                    Environment.MEDIA_MOUNTED)) {
                // get the directory of the SDcard 获取SD卡的目录
                File sdDire = Environment.getExternalStorageDirectory();
                FileOutputStream outFileStream = new FileOutputStream(
                        sdDire.getCanonicalPath() + "/" + filename + ".txt", true);
                outFileStream.write(string.getBytes());
                outFileStream.close();
                Log.d("SDcard", "input");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, gyroscope, SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        sensorManager.unregisterListener(this);
    }
}
