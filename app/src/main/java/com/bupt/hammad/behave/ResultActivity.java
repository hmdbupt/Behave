package com.bupt.hammad.behave;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class ResultActivity extends AppCompatActivity {

    // Bundle object creation
    Bundle bundle;

    // Bundle variables
    private String driveTime;
    private float leftTurns;
    private float rightTurns;
    private float uTurns;
    private float dangerousLeftLeans;
    private float dangerousRightLeans;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        // Text view declaration and initialization
        TextView driveTimeTextView = (TextView) findViewById(R.id.driveTimeTextView);
        TextView leftTurnTextView = (TextView) findViewById(R.id.leftTurnTextView);
        TextView rightTurnTextView = (TextView) findViewById(R.id.rightTurnTextView);
        TextView uTurnTextView = (TextView) findViewById(R.id.uTurnTextView);
//        TextView dangerousLeftLean = (TextView) findViewById(R.id.dangerousLeftLean);
//        TextView dangerousRightLean = (TextView) findViewById(R.id.dangerousRightLean);

        Intent iResultActivity = new Intent();
        iResultActivity.getExtras();

        // Get data that was collected by the start activity and passed to bundle
        // Bundle from start activity
        bundle = new Bundle();
        bundle = getIntent().getExtras();
        driveTime = bundle.getString("RIDE_TIME");
        leftTurns = bundle.getFloat("LEFT_TURNS");
        rightTurns = bundle.getFloat("RIGHT_TURNS");
        uTurns = bundle.getFloat("U_TURNS");
        dangerousLeftLeans = bundle.getFloat("DANGEROUS_LEFT_LEAN");
        dangerousRightLeans = bundle.getFloat("DANGEROUS_RIGHT_LEAN");

        driveTimeTextView.setText("Total Riding Time: "+driveTime);
        leftTurnTextView.setText("Left Turns: "+leftTurns);
        rightTurnTextView.setText("Right Turns: "+rightTurns);
        uTurnTextView.setText("U-Turns: "+uTurns);
//        dangerousLeftLean.setText("Dangerous Left Leans: "+dangerousLeftLeans);
//        dangerousRightLean.setText("Dangerous Right Leans: "+dangerousRightLeans);

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
}
