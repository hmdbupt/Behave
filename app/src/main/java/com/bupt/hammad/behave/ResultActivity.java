package com.bupt.hammad.behave;

import android.content.Intent;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ResultActivity extends AppCompatActivity {

    // Bundle object creation
    Bundle bundle;

    // Bundle variables
    private String driveTime;
    private int leftTurns;
    private int rightTurns;
    private int uTurns;
    private int dangerousLeftLeans;
    private int dangerousRightLeans;
    private int brakes;
    private int emergencyBrakes;

    // Filename
    private String filename = "Riding stats.txt";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        // Text view declaration and initialization
        TextView driveTimeTextView = (TextView) findViewById(R.id.driveTimeTextView);
        TextView leftTurnTextView = (TextView) findViewById(R.id.leftTurnTextView);
        TextView rightTurnTextView = (TextView) findViewById(R.id.rightTurnTextView);
        TextView uTurnTextView = (TextView) findViewById(R.id.uTurnTextView);
        TextView dangerousLeftLeanTextView = (TextView) findViewById(R.id.dangerousLeftLeanTextView);
        TextView dangerousRightLeanTextView = (TextView) findViewById(R.id.dangerousRightLeanTextView);
        TextView brakesTextView = (TextView) findViewById(R.id.brakes);
        TextView emergencyBrakesTextView = (TextView) findViewById(R.id.emergencyBrakes);

        Intent iResultActivity = new Intent();
        iResultActivity.getExtras();

        // Get data that was collected by the twoHundredCounter activity and passed to bundle
        // Bundle from twoHundredCounter activity
        bundle = new Bundle();
        bundle = getIntent().getExtras();
        driveTime = bundle.getString("RIDE_TIME");
        leftTurns = (int) bundle.getFloat("LEFT_TURNS");
        rightTurns = (int) bundle.getFloat("RIGHT_TURNS");
        uTurns = (int) bundle.getFloat("U_TURNS");
        dangerousLeftLeans = (int) bundle.getFloat("DANGEROUS_LEFT_LEAN");
        dangerousRightLeans = (int) bundle.getFloat("DANGEROUS_RIGHT_LEAN");
        brakes = (int) bundle.getFloat("BRAKES");
        emergencyBrakes = (int) bundle.getFloat("EMERGENCY_BRAKES");

        driveTimeTextView.setText("Total Riding Time: "+driveTime);
        leftTurnTextView.setText("Left Turns: "+leftTurns);
        rightTurnTextView.setText("Right Turns: "+rightTurns);
        uTurnTextView.setText("U-Turns: "+uTurns);
        dangerousLeftLeanTextView.setText("Dangerous Left Leans: "+dangerousLeftLeans);
        dangerousRightLeanTextView.setText("Dangerous Right Leans: "+dangerousRightLeans);
        brakesTextView.setText("Brakes: "+brakes);
        emergencyBrakesTextView.setText("Emergency Brakes: "+emergencyBrakes);


        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("| yyyy-MM-dd | HH:mm:ss |");
        String simpleDateFormatString = simpleDateFormat.format(new Date());

        writeSDCard("===== "+simpleDateFormatString+" ====="+"\n"+
                "Total riding time: "+driveTime+"\n"+
                "Left turns: "+leftTurns+"\n"+
                "Right turns: "+rightTurns+"\n"+
                "U-Turns: "+uTurns+"\n");
//        dangerousLeftLeanTextView.setText("Dangerous Left Leans: "+dangerousLeftLeans);
//        dangerousRightLeanTextView.setText("Dangerous Right Leans: "+dangerousRightLeans);

        // Save data on SD-Card
        Button exitButton = (Button) findViewById(R.id.exitButton);
        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent iMainActivity = new Intent(ResultActivity.this, MainActivity.class);
                startActivity(iMainActivity);
                finish();
            }
        });
    }

    private void writeSDCard(String string){
        try{
            if (Environment.getExternalStorageState().equals(
                    Environment.MEDIA_MOUNTED)) {
                File directory = Environment.getExternalStorageDirectory();
                FileOutputStream outFileStream = new FileOutputStream(
                        directory.getCanonicalPath() + "/" + filename , true);
                outFileStream.write(string.getBytes());
                outFileStream.close();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
