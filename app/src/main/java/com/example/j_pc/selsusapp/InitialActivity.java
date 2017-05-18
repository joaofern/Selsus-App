package com.example.j_pc.selsusapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import java.util.Timer;
import java.util.TimerTask;

public class InitialActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_initial);

        new Timer().schedule(new TimerTask(){
            public void run() {
                InitialActivity.this.runOnUiThread(new Runnable() {
                    public void run() {
                        startActivity(new Intent(InitialActivity.this, SelcompSelectActivity.class));
                    }
                });
            }
        }, 2000);



    }

    @Override
    protected void onResume() {
        super.onResume();
        new Timer().schedule(new TimerTask(){
            public void run() {
                InitialActivity.this.runOnUiThread(new Runnable() {
                    public void run() {
                        startActivity(new Intent(InitialActivity.this, SelcompSelectActivity.class));
                    }
                });
            }
        }, 2000);
    }
}
