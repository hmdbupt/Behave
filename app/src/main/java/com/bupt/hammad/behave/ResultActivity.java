package com.bupt.hammad.behave;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class ResultActivity extends AppCompatActivity {

    //Longs
    private String driveTime;

    Bundle bundle;
    TextView driveTimeTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        bundle = new Bundle();
        bundle = getIntent().getExtras();
        TextView driveTimeTextView = (TextView) findViewById(R.id.driveTimeTextView);

        Intent iResultActivity = new Intent();
        iResultActivity.getExtras();
        driveTime = bundle.getString("DRIVE_TIME");
        driveTimeTextView.setText(driveTime);

        Button exitButton = (Button) findViewById(R.id.exitButton);
        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent iMainActivity = new Intent(ResultActivity.this, MainActivity.class);
                startActivity(iMainActivity);
            }
        });
    }
}
