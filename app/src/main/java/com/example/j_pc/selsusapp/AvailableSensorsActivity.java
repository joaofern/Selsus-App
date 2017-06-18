package com.example.j_pc.selsusapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.util.ArraySet;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class AvailableSensorsActivity extends AppCompatActivity {

    Toolbar mToolbar;
    private Button button;
    ArrayList<Integer> sensorIDs = new ArrayList<>();
    ArrayList<Integer> selected = new ArrayList<>();
//    ArrayList<String> sel_names = new ArrayList<>();
    String sel_name;
    int sel_id;

    HashMap<Integer,String> sensor_names = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diagnosis_tool);

        mToolbar = (Toolbar) findViewById(R.id.my_titlebar);
        TextView title = (TextView) mToolbar.findViewById(R.id.toolbar_title);
        title.setText("System Overview");

        LinearLayout linear = (LinearLayout) findViewById(R.id.linear);
        final Intent intent = getIntent();

        try {
            JSONObject selcomp = new JSONObject(intent.getStringExtra("selcomp"));
            JSONObject sensors = selcomp.getJSONObject("sensors");
            Iterator<?> keys = sensors.keys();
            while( keys.hasNext() ) {
                String key = (String)keys.next();
                View child = getLayoutInflater().inflate(R.layout.sensor_check, null);

                TextView txt = (TextView) child.findViewById(R.id.sensorName);
                txt.setText(key.toString());
                final CheckBox check = (CheckBox) child.findViewById(R.id.sensorCheckbox);
                sensorIDs.add(Integer.parseInt(sensors.get(key).toString()));
                check.setId(Integer.parseInt(sensors.get(key).toString()));
                sensor_names.put(Integer.parseInt(sensors.get(key).toString()),key);

                linear.addView(child);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        final Button button = (Button) findViewById(R.id.next);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent next = new Intent(getApplicationContext(), CloudRequestGraphActivity.class);
                selected.clear();
                for (int id : sensorIDs) {
                    if (((CheckBox) findViewById(id)).isChecked()){
                        selected.add(id);
                        sel_name = sensor_names.get(id);
                        sel_id = id;


                    }

                }
                next.putExtra("selected", selected);
                next.putExtra("selcomp",intent.getStringExtra("selcomp"));
                next.putExtra("mode",intent.getStringExtra("mode"));
                System.out.println(sel_name);
                next.putExtra("sel_name",sel_name);
                System.out.println("sel_id"+sel_id);
                next.putExtra("sel_id",sel_id);
                startActivity(next);
            }
        });

    }

}