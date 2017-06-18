package com.example.j_pc.selsusapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.crystal.crystalrangeseekbar.interfaces.OnRangeSeekbarChangeListener;
import com.crystal.crystalrangeseekbar.widgets.CrystalRangeSeekbar;
import com.github.jjobes.slidedatetimepicker.SlideDateTimeListener;
import com.github.jjobes.slidedatetimepicker.SlideDateTimePicker;
import com.google.android.gms.vision.text.Text;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.Viewport;
import com.jjoe64.graphview.helper.StaticLabelsFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.math.BigDecimal;
import java.security.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class CloudRequestGraphActivity extends AppCompatActivity {

    ArrayList<Integer> sel = new ArrayList<>();
    ArrayList<String> sensor_names = new ArrayList<>();
    HashMap<Integer, List <List<DataPoint>>> capture = new HashMap<>();
    HashMap<Integer, String> comments = new HashMap<>();
    String from1,to1, sensor_name;
    int sel_id;
    List<CheckBox> checks = new ArrayList<CheckBox>();


    SensorManager mSensorManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cloud_request_graph);

        Toolbar mToolbar = (Toolbar) findViewById(R.id.my_titlebar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("");

        TextView title = (TextView) mToolbar.findViewById(R.id.toolbar_title);
        title.setText("SelSus App");

        LinearLayout linear = (LinearLayout) findViewById(R.id.linear);
        LinearLayout linear2 = (LinearLayout) findViewById(R.id.linear2);
        Intent intent = getIntent();
        sensor_name = intent.getStringExtra("sel_name");
        sel_id = intent.getIntExtra("sel_id",0);

        View last5minutes = getLayoutInflater().inflate(R.layout.sensor_check, null);
        View lasthour = getLayoutInflater().inflate(R.layout.sensor_check, null);
        View lastday = getLayoutInflater().inflate(R.layout.sensor_check, null);
        View other = getLayoutInflater().inflate(R.layout.sensor_check, null);

        TextView txt1 = (TextView) last5minutes.findViewById(R.id.sensorName);
        TextView txt2 = (TextView) lasthour.findViewById(R.id.sensorName);
        TextView txt3 = (TextView) lastday.findViewById(R.id.sensorName);
        TextView txt4 = (TextView) other.findViewById(R.id.sensorName);
        final CheckBox chck1 = (CheckBox) last5minutes.findViewById(R.id.sensorCheckbox);
        final CheckBox chck2 = (CheckBox) lasthour.findViewById(R.id.sensorCheckbox);
        final CheckBox chck3 = (CheckBox) lastday.findViewById(R.id.sensorCheckbox);
        final CheckBox chck4 = (CheckBox) other.findViewById(R.id.sensorCheckbox);
        checks.add(chck1);
        checks.add(chck2);
        checks.add(chck3);
        checks.add(chck4);

        final TextView from = (TextView)findViewById(R.id.from);
        final TextView to = (TextView)findViewById(R.id.to);


        chck1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(chck1.isChecked()){
                    chck2.setChecked(false);
                    chck3.setChecked(false);
                    chck4.setChecked(false);
                    from.setVisibility(View.INVISIBLE);
                    to.setVisibility(View.INVISIBLE);
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
                    from.setVisibility(View.INVISIBLE);
                    to.setVisibility(View.INVISIBLE);
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
                    from.setVisibility(View.INVISIBLE);
                    to.setVisibility(View.INVISIBLE);
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
                    from.setVisibility(View.VISIBLE);
                    to.setVisibility(View.VISIBLE);
                }
            }
        });

        txt1.setText("Last 5 minutes");
        txt2.setText("Last hour");
        txt3.setText("Last day");
        txt4.setText("Other");

        linear2.addView(last5minutes);
        linear2.addView(lasthour);
        linear2.addView(lastday);
        linear2.addView(other);


        sel = intent.getIntegerArrayListExtra("selected");
        sensor_names = intent.getStringArrayListExtra("sensor_names");

        String mode = intent.getStringExtra("mode");

        View child = getLayoutInflater().inflate(R.layout.selcomp_info_card, null);
        CardView card = (CardView)child.findViewById(R.id.card_view);
        card.setUseCompatPadding(true);

        TextView sensorID = (TextView)child.findViewById(R.id.selcompID);
        TextView state = (TextView)child.findViewById(R.id.state);
        TextView ip = (TextView)child.findViewById(R.id.ip);
        TextView mac = (TextView)child.findViewById(R.id.mac);
        TextView port = (TextView)child.findViewById(R.id.port);
        TextView sensor = (TextView)child.findViewById(R.id.sensor);


        try {
            JSONObject selcomp = new JSONObject(intent.getStringExtra("selcomp"));
            sensorID.setText(selcomp.getString("selcompID"));
            state.setText(selcomp.getString("state"));
            ip.setText(selcomp.getString("ip"));
            mac.setText(selcomp.getString("mac"));
            port.setText(selcomp.getString("port"));
            sensor.setText(intent.getStringExtra("sel_name"));


        } catch (JSONException e) {
            e.printStackTrace();
        }

        linear.addView(child);


        final Button next = (Button)findViewById(R.id.next);
        final LinearLayout from_ex = (LinearLayout)findViewById(R.id.from1);
        final LinearLayout to_ex = (LinearLayout)findViewById(R.id.to1);
        final TextView tv_from_date = (TextView)findViewById(R.id.from_date);
        final TextView tv_from_hour = (TextView)findViewById(R.id.from_hour);
        final TextView tv_to_date = (TextView)findViewById(R.id.to_date);
        final TextView tv_to_hour = (TextView)findViewById(R.id.to_hour);


        from.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final SlideDateTimeListener listener = new SlideDateTimeListener() {
                    @Override
                    public void onDateTimeSet(Date date)
                    {
                        // Do something with the date. This Date object contains
                        // the date and time that the user has selected.
                        from1 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
                                .format(date);
                        String hour = new SimpleDateFormat("HH:mm:ss")
                                .format(date);
                        String date1 = new SimpleDateFormat("dd-MM-yyyy")
                                .format(date);
                        tv_from_date.setText(date1);
                        tv_from_hour.setText(hour);
                        from.setVisibility(View.GONE);
                        from_ex.setVisibility(View.VISIBLE);


                    }
                };
                new SlideDateTimePicker.Builder(getSupportFragmentManager())
                        .setListener(listener)
                        .setInitialDate(new Date())
                        .build()
                        .show();
            }
        });

        to.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final SlideDateTimeListener listener = new SlideDateTimeListener() {
                    @Override
                    public void onDateTimeSet(Date date)
                    {
                        // Do something with the date. This Date object contains
                        // the date and time that the user has selected.
                        to1 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
                                .format(date);
                        String hour = new SimpleDateFormat("HH:mm:ss")
                                .format(date);
                        String date1 = new SimpleDateFormat("dd-MM-yyyy")
                                .format(date);
                        tv_to_date.setText(date1);
                        tv_to_hour.setText(hour);
                        to.setVisibility(View.GONE);
                        to_ex.setVisibility(View.VISIBLE);
                    }
                };
                new SlideDateTimePicker.Builder(getSupportFragmentManager())
                        .setListener(listener)
                        .setInitialDate(new Date())
                        .build()
                        .show();
            }
        });

        from_ex.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final SlideDateTimeListener listener = new SlideDateTimeListener() {
                    @Override
                    public void onDateTimeSet(Date date)
                    {
                        // Do something with the date. This Date object contains
                        // the date and time that the user has selected.
                        from1 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
                                .format(date);
                        String hour = new SimpleDateFormat("HH:mm:ss:")
                                .format(date);
                        String date1 = new SimpleDateFormat("dd-MM-yyyy")
                                .format(date);
                        tv_from_date.setText(date1);
                        tv_from_hour.setText(hour);


                    }
                };
                new SlideDateTimePicker.Builder(getSupportFragmentManager())
                        .setListener(listener)
                        .setInitialDate(new Date())
                        .build()
                        .show();
            }
        });

        to_ex.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final SlideDateTimeListener listener = new SlideDateTimeListener() {
                    @Override
                    public void onDateTimeSet(Date date)
                    {
                        // Do something with the date. This Date object contains
                        // the date and time that the user has selected.
                        to1 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
                                .format(date);
                        String hour = new SimpleDateFormat("HH:mm:ss")
                                .format(date);
                        String date1 = new SimpleDateFormat("dd-MM-yyyy")
                                .format(date);
                        tv_to_date.setText(date1);
                        tv_to_hour.setText(hour);
                    }
                };
                new SlideDateTimePicker.Builder(getSupportFragmentManager())
                        .setListener(listener)
                        .setInitialDate(new Date())
                        .build()
                        .show();
            }
        });



        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent next_btn = new Intent(getApplicationContext(), CloudGraphResultsActivity.class);
                next_btn.putExtra("selected",sel);
                next_btn.putExtra("sensor_name",sensor_name);
                next_btn.putExtra("from",from1);
                next_btn.putExtra("sel_id", sel_id);
                next_btn.putExtra("to",to1);
                startActivity(next_btn);
            }
        });
    }
}
