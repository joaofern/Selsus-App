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
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.crystal.crystalrangeseekbar.interfaces.OnRangeSeekbarChangeListener;
import com.crystal.crystalrangeseekbar.widgets.CrystalRangeSeekbar;
import com.google.android.gms.vision.text.Text;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.Viewport;
import com.jjoe64.graphview.helper.StaticLabelsFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.io.Serializable;
import java.math.BigDecimal;
import java.security.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class GraphResultsActivity extends AppCompatActivity {

    ArrayList<Integer> sel = new ArrayList<>();
    HashMap<Integer, List <List<DataPoint>>> capture = new HashMap<>();
    HashMap<Integer, List <List<DataPoint>>> toStore = new HashMap<>();
    HashMap<Integer, String> comments = new HashMap<>();
    HashMap<Integer,CheckBox> checkBoxes = new HashMap<>();
    HashMap<Integer,TextView> commentTextViews = new HashMap<>();
    HashMap<Integer,EditText> commentInput = new HashMap<>();

    SensorManager mSensorManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph_results);

        LinearLayout linear = (LinearLayout) findViewById(R.id.linearlayout);

        Toolbar mToolbar;
        mToolbar = (Toolbar) findViewById(R.id.my_titlebar);
        TextView title = (TextView) mToolbar.findViewById(R.id.toolbar_title);
        title.setText("Capture Results");

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        Intent intent = this.getIntent();
        if (intent != null) {
            sel = intent.getExtras().getIntegerArrayList("selected");
            capture = (HashMap<Integer, List <List<DataPoint>>>) intent.getSerializableExtra("capture");

            for(final Integer type : sel){
                View child = this.getLayoutInflater().inflate(R.layout.graph_card2, null);
                CardView card = (CardView)child.findViewById(R.id.card_view);
                card.setUseCompatPadding(true);
                final GraphView graph = (GraphView) child.findViewById(R.id.graph);
                final TextView tit = (TextView)child.findViewById(R.id.title);
                final CheckBox check = (CheckBox)child.findViewById(R.id.sensorCheckbox);
                checkBoxes.put(type,check);

                final TextView com = (TextView) child.findViewById(R.id.comment);
                commentTextViews.put(type,com);

                final EditText et = (EditText) child.findViewById(R.id.mytextText);
                commentInput.put(type,et);

                final TextView sensorName = (TextView)child.findViewById(R.id.title);

                final TextView lable1 = (TextView)child.findViewById(R.id.lable1);
                final TextView lable2 = (TextView)child.findViewById(R.id.lable2);

                sensorName.setText(DiagnosisToolActivity.sensorName(type));


                LineGraphSeries<DataPoint> series = new LineGraphSeries<>(new DataPoint[] {
                });
                LineGraphSeries<DataPoint> series1 = new LineGraphSeries<>(new DataPoint[] {
                });
                LineGraphSeries<DataPoint> series2 = new LineGraphSeries<>(new DataPoint[] {
                });

                //Add DataPoints

                Integer toKeep = capture.get(type).get(0).size();
                for(DataPoint dp : capture.get(type).get(0)){
                    series.appendData(dp,true,toKeep);
                }
                for(DataPoint dp : capture.get(type).get(1)){
                    series1.appendData(dp,true,toKeep);
                }
                for(DataPoint dp : capture.get(type).get(2)){
                    series2.appendData(dp,true,toKeep);
                }
                int tam = capture.get(type).get(0).size();

                Double ts1 = (capture.get(type).get(0).get(0).getX());
                Double ts2 = (capture.get(type).get(0).get(tam-1).getX());

                Date itemdate1 = convertTimestamp(ts1);
                Date itemdate2 = convertTimestamp(ts2);

                String itemDateStr1 = new SimpleDateFormat("HH:mm:ss.SS").format(itemdate1);
                String itemDateStr2 = new SimpleDateFormat("HH:mm:ss.SS").format(itemdate2);

                lable1.setText(itemDateStr1);
                lable2.setText(itemDateStr2);

                series.setColor(Color.BLUE);
                series1.setColor(Color.GREEN);
                series2.setColor(Color.RED);

                graph.addSeries(series);
                graph.addSeries(series1);
                graph.addSeries(series2);

                graph.getGridLabelRenderer().setGridStyle(GridLabelRenderer.GridStyle.HORIZONTAL);
                graph.getGridLabelRenderer().setHorizontalLabelsVisible(false);

                final Sensor sensor = mSensorManager.getDefaultSensor(type);

                Viewport viewport = graph.getViewport();
                viewport.setYAxisBoundsManual(true);
                viewport.setXAxisBoundsManual(true);
                viewport.setMinY(0- sensor.getMaximumRange());
                viewport.setMaxY(sensor.getMaximumRange());

                // get seekbar from view
                final CrystalRangeSeekbar rangeSeekbar = (CrystalRangeSeekbar) child.findViewById(R.id.rangeSeekbar1);
                rangeSeekbar.setScaleX(0.75f);
                rangeSeekbar.setScaleY(0.75f);

                // get min and max text view
                final TextView tvMin = (TextView) child.findViewById(R.id.lable1);
                final TextView tvMax = (TextView) child.findViewById(R.id.lable2);

                // set listener
                rangeSeekbar.setOnRangeSeekbarChangeListener(new OnRangeSeekbarChangeListener() {
                    @Override
                    public void valueChanged(Number minValue, Number maxValue) {
                        float percent = ((capture.get(type).get(0).size()-1)/100f);

                        Double min = capture.get(type).get(0).get((int)(minValue.intValue()*percent)).getX();
                        Double max = capture.get(type).get(0).get((int)(maxValue.intValue()*percent)).getX();

                        String itemDateStr1 = new SimpleDateFormat("HH:mm:ss.SS").format(convertTimestamp(min));
                        String itemDateStr2 = new SimpleDateFormat("HH:mm:ss.SS").format(convertTimestamp(max));

                        tvMin.setText(itemDateStr1);
                        tvMax.setText(itemDateStr2);
                    }
                });
                linear.addView(child);
            }
            commentListener();

        }

        else System.out.println("FAIL!!!!");
    }


    public void commentListener() {

            for(final Integer type : sel){
                commentTextViews.get(type).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(commentInput.get(type).getVisibility()==View.GONE){
                            commentTextViews.get(type).setText("Save comment");
                            commentInput.get(type).setVisibility(View.VISIBLE);
                        }
                        else{
                            commentInput.get(type).setVisibility(View.GONE);
                            commentTextViews.get(type).setText("Saved!");
                            comments.put(type,commentInput.get(type).getText().toString());

                            //Close Android Keyboard
                            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                        }
                    }
                });


            }
    }


    public Date convertTimestamp(Double a){
        long timeInMillis = (new Date()).getTime()
                + ((long)(a*1000000d) - SystemClock.elapsedRealtimeNanos()) / 1000000L;
        Date itemdate = new Date(timeInMillis);
        return itemdate;
    }


}
