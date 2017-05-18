package com.example.j_pc.selsusapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Display;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ModeSelectActivity extends AppCompatActivity {
    private Intent intent;
    private String selcomps;
    private String key;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mode_select);

        Toolbar mToolbar = (Toolbar) findViewById(R.id.my_titlebar);
        TextView title = (TextView) mToolbar.findViewById(R.id.toolbar_title);
        title.setText("SelSus App");

        LinearLayout linear = (LinearLayout) findViewById(R.id.linear);

        intent = getIntent();
        selcomps = intent.getStringExtra("selcomp");
        key = intent.getStringExtra("key");

        List<CheckBox> checks = new ArrayList<CheckBox>();

        View calibration = getLayoutInflater().inflate(R.layout.sensor_check, null);
        View diagnosis = getLayoutInflater().inflate(R.layout.sensor_check, null);
        View selcomp_overview = getLayoutInflater().inflate(R.layout.sensor_check, null);
        View cloud_overview = getLayoutInflater().inflate(R.layout.sensor_check, null);
        TextView txt1 = (TextView) calibration.findViewById(R.id.sensorName);
        TextView txt2 = (TextView) diagnosis.findViewById(R.id.sensorName);
        TextView txt3 = (TextView) selcomp_overview.findViewById(R.id.sensorName);
        TextView txt4 = (TextView) cloud_overview.findViewById(R.id.sensorName);
        final CheckBox chck1 = (CheckBox) calibration.findViewById(R.id.sensorCheckbox);
        final CheckBox chck2 = (CheckBox) diagnosis.findViewById(R.id.sensorCheckbox);
        final CheckBox chck3 = (CheckBox) selcomp_overview.findViewById(R.id.sensorCheckbox);
        final CheckBox chck4 = (CheckBox) cloud_overview.findViewById(R.id.sensorCheckbox);
        checks.add(chck1);
        checks.add(chck2);
        checks.add(chck3);
        checks.add(chck4);


        chck1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(chck1.isChecked()){
                    chck2.setChecked(false);
                    chck3.setChecked(false);
                    chck4.setChecked(false);
                }
            }
        });

        chck2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(chck2.isChecked()){
                    chck1.setChecked(false);
                    chck3.setChecked(false);
                    chck4.setChecked(false);
                }
            }
        });

        chck3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(chck3.isChecked()){
                    chck2.setChecked(false);
                    chck1.setChecked(false);
                    chck4.setChecked(false);
                }
            }
        });

        chck4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(chck4.isChecked()){
                    chck2.setChecked(false);
                    chck1.setChecked(false);
                    chck3.setChecked(false);
                }
            }
        });


        txt1.setText("Diagnosis (SelSus Cloud)");
        txt2.setText("Calibration (SelComp)");
        txt3.setText("SelComp Overview");
        txt4.setText("Cloud Overview");
        chck1.setId(View.generateViewId());
        chck2.setId(View.generateViewId());
        chck3.setId(View.generateViewId());
        chck4.setId(View.generateViewId());
        linear.addView(calibration);
        linear.addView(diagnosis);
        linear.addView(selcomp_overview);
        linear.addView(cloud_overview);

        final Button button = (Button) findViewById(R.id.next);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent next;
                if(chck1.isChecked()) {
                    next = new Intent(getApplicationContext(), DiagnosisToolActivity.class);
                    next.putExtra("mode", 1);
                    next.putExtra("selcomps",selcomps);
                    startActivity(next);
                }
                else if(chck2.isChecked()){
                    next = new Intent(getApplicationContext(), DiagnosisToolActivity.class);
                    next.putExtra("mode", 2);
                    next.putExtra("selcomps",selcomps);
                    startActivity(next);

                }
                else if(chck3.isChecked()){
//                    next = new Intent(getApplicationContext(), AvailableSensorsActivity.class);
//                    next.putExtra("mode", 3);
//                    next.putExtra("selcomp",selcomps);
//                    startActivity(next);
                    AlertDialog alertDialog = new AlertDialog.Builder(ModeSelectActivity.this).create();
                    alertDialog.setTitle("Warning");
                    alertDialog.setMessage("Not available yet!!");
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    alertDialog.show();

                }
                else if(chck4.isChecked()){
                    next = new Intent(getApplicationContext(), AvailableSensorsActivity.class);
                    next.putExtra("mode", 4);
                    next.putExtra("selcomp",selcomps);
                    startActivity(next);
                }



            }
        });


    }
}
