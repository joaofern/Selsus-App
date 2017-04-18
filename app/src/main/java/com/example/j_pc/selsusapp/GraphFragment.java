package com.example.j_pc.selsusapp;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.Viewport;
import com.jjoe64.graphview.helper.StaticLabelsFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.Series;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import android.os.Handler;


public class GraphFragment extends Fragment {
    private int i = 0;
    private static final Random RANDOM = new Random();
    private final Handler mHandler = new Handler();
    private boolean changed = false;
    private double lux = 0;
    private DiagnosisActivity activity;
    private List<SensorEventListener> listeners;
    private Boolean onCapture = false;
    Button stopCapture;
    ArrayList<Integer> selected = new ArrayList<>();
    HashMap<Integer, List <List<DataPoint>>> capture = new HashMap<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        activity = (DiagnosisActivity) getActivity();
        final ArrayList<Integer> myData = activity.getSel();
        View view =  inflater.inflate(R.layout.fragment_graph, null, false);

        LinearLayout linear = (LinearLayout)view.findViewById(R.id.linearlayout);
        SensorManager manager = ((DiagnosisActivity) getActivity()).mSensorManager;
        listeners = new ArrayList<>(myData.size());

        stopCapture = (Button)view.findViewById(R.id.stop);
        stopCapture.setVisibility(View.GONE);

        capture.clear(); //CAAAAAAAARRRRREEEEEEEE

        for(final Integer type : myData){
            final Sensor sensor = manager.getDefaultSensor(type);

            View child = getActivity().getLayoutInflater().inflate(R.layout.graph_card, null);
            CardView card = (CardView)child.findViewById(R.id.card_view);
            card.setUseCompatPadding(true);

            final GraphView graph = (GraphView) child.findViewById(R.id.graph);
            activity.mGraphs.add(graph);
            LineGraphSeries<DataPoint>series = new LineGraphSeries<>(new DataPoint[] {
            });
            LineGraphSeries<DataPoint> series1 = new LineGraphSeries<>(new DataPoint[] {
            });
            LineGraphSeries<DataPoint> series2 = new LineGraphSeries<>(new DataPoint[] {
            });

            series.setColor(Color.BLUE);
            series1.setColor(Color.GREEN);
            series2.setColor(Color.RED);

            graph.addSeries(series);
            graph.addSeries(series1);
            graph.addSeries(series2);
            Viewport viewport = graph.getViewport();
            viewport.setYAxisBoundsManual(true);
            viewport.setXAxisBoundsManual(true);
            viewport.setMinY(0-sensor.getMaximumRange());
            viewport.setMaxY(sensor.getMaximumRange());
            viewport.setMinX(0);
            viewport.setMaxX(10000);

            graph.getGridLabelRenderer().setGridStyle(GridLabelRenderer.GridStyle.HORIZONTAL);
            graph.getGridLabelRenderer().setHorizontalLabelsVisible(false);

            final TextView lable1 = (TextView)child.findViewById(R.id.lable1);
            final TextView lable2 = (TextView)child.findViewById(R.id.lable2);

            lable1.setText("0s");
            lable2.setText("10s");

            final TextView title = (TextView)child.findViewById(R.id.title);
            final CheckBox check = (CheckBox) child.findViewById(R.id.sensorCheckbox);

            check.setId(type);

            title.setText(DiagnosisToolActivity.sensorName(type));
            linear.addView(child);

            final int index = i;
            final SensorEventListener listener = new SensorEventListener() {

                

                @Override
                public void onSensorChanged(SensorEvent event) {
                    try {

                        List<Series> series = graph.getSeries();
                        DataPoint dpX = new DataPoint(event.timestamp/1000000d, event.values[0]); //milliseconds
                        DataPoint dpY=null, dpZ=null;
                        if(event.values.length>1) {
                            dpY = new DataPoint(event.timestamp / 1000000d, event.values[1]);
                            dpZ = new DataPoint(event.timestamp / 1000000d, event.values[2]);
                        }

                        int minDelay = event.sensor.getMinDelay() / 1000; //milliseconds
                        int toKeep = 1000;
                        if(minDelay!=0) //is zero in some sensors like light
                            toKeep = 10000 / minDelay; //10 seconds

                        ((LineGraphSeries<DataPoint>)series.get(0)).appendData(dpX, true, toKeep);

                        if(onCapture && selected.contains(sensor.getType())) {
                            List<DataPoint> captureXDataPoints = capture.get(sensor.getType()).get(0);
                            captureXDataPoints.add(dpX);
                            if(event.sensor.getType()==Sensor.TYPE_LIGHT){
                                lux = event.values[0];

                            }

                        }
                        if(event.values.length>1&&event.sensor.getType()!=Sensor.TYPE_PROXIMITY&&event.sensor.getType()!=Sensor.TYPE_LIGHT) {
                            ((LineGraphSeries<DataPoint>) series.get(1)).appendData(dpY, true, toKeep);
                            ((LineGraphSeries<DataPoint>) series.get(2)).appendData(dpZ, true, toKeep);
                            if(onCapture && selected.contains(sensor.getType())) {
                                List<DataPoint> captureYDataPoints = capture.get(sensor.getType()).get(1);
                                captureYDataPoints.add(dpY);
                                List<DataPoint> captureZDataPoints = capture.get(sensor.getType()).get(2);
                                captureZDataPoints.add(dpZ);
                            }
                        }


                    } catch (NullPointerException | IndexOutOfBoundsException e) {
                        //do nothing
                    }



                }

                @Override
                public void onAccuracyChanged(Sensor sensor, int accuracy) {

                }

            };
            listeners.add(listener);
        }

        final Button button = (Button) view.findViewById(R.id.next);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                selected.clear();
                onCapture = true;
                stopCapture.setVisibility(View.VISIBLE);
                for(int type : myData) {
                    CheckBox check = ((CheckBox) getView().findViewById(type));
                    if(check.isChecked()) {
                        selected.add(type);

                        List<List<DataPoint>> list = new ArrayList<>(3);
                        list.add(new LinkedList<DataPoint>());
                        list.add(new LinkedList<DataPoint>());
                        list.add(new LinkedList<DataPoint>());
                        capture.put(type, list);
                    }
                }

            }
        });

        stopCapture.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                onCapture = false;
                Intent next = new Intent(getContext(), GraphResultsActivity.class);
                next.putExtra("capture", capture);
                next.putExtra("selected", selected);

                startActivity(next);

            }
        });


        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        ArrayList<Integer> myData = ((DiagnosisActivity) getActivity()).getSel();
        SensorManager manager = ((DiagnosisActivity) getActivity()).mSensorManager;
        for(int i = 0;i< myData.size(); i++) {
            int type = myData.get(i);
            Sensor sensor = manager.getDefaultSensor(type);
            manager.registerListener(listeners.get(i), sensor, SensorManager.SENSOR_DELAY_NORMAL);
        }


    }

    @Override
    public void onPause() {
        super.onPause();
        ArrayList<Integer> myData = ((DiagnosisActivity) getActivity()).getSel();
        SensorManager manager = ((DiagnosisActivity) getActivity()).mSensorManager;
        for(int i = 0;i< myData.size(); i++) {
            manager.unregisterListener(listeners.get(i));
        }
    }
}
