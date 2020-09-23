package com.POS.apis.FingerprintController;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import jepower.com.t508ac_demo.R;

public class Dashboard extends Activity {

    Button btnToGoBallot;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        btnToGoBallot = findViewById(R.id.btnToGoBallot);

        btnToGoBallot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToBallot();
            }
        });
    }

    public void goToBallot() {
        Intent intent = new Intent(this, PresidentialBallot.class);
        startActivity(intent);
    }
}