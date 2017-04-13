package com.example.j_pc.selsusapp;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.Series;

import java.util.ArrayList;
import java.util.List;

public class InfoFragment extends Fragment {
    private List<SensorEventListener> listeners;
    @Nullable
    @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            final DiagnosisActivity activity = (DiagnosisActivity) getActivity();
            ArrayList<Integer> myData = activity.getSel();
            View view =  inflater.inflate(R.layout.fragment_details, null, false);

            LinearLayout linear = (LinearLayout)view.findViewById(R.id.linearlayout);

            SensorManager manager = ((DiagnosisActivity) getActivity()).mSensorManager;
            listeners = new ArrayList<>(myData.size());
            for(int i = 0;i< myData.size(); i++){
                int type = myData.get(i);
                Sensor sensor = manager.getDefaultSensor(type);

                View child = getActivity().getLayoutInflater().inflate(R.layout.info_card, null);
                CardView card = (CardView)child.findViewById(R.id.card_view);
                card.setUseCompatPadding(true);

                final TextView x = (TextView)child.findViewById(R.id.X);
                final TextView xLabel = (TextView)child.findViewById(R.id.XLabel);
                final TextView y = (TextView)child.findViewById(R.id.Y);
                final TextView yLabel = (TextView)child.findViewById(R.id.YLabel);
                final TextView z = (TextView)child.findViewById(R.id.Z);
                final TextView zLabel = (TextView)child.findViewById(R.id.ZLabel);
                final TextView title = (TextView)child.findViewById(R.id.title);
                final TextView Xunit = (TextView)child.findViewById((R.id.Xunit));
                final TextView Yunit = (TextView)child.findViewById((R.id.Yunit));
                final TextView Zunit = (TextView)child.findViewById((R.id.Zunit));

                final SensorEventListener listener = new SensorEventListener() {
                    @Override
                    public void onSensorChanged(SensorEvent event) {
                        x.setText(String.format("%.3f", event.values[0]));
                        if(event.values.length>1) {
                            y.setText(String.format("%.3f", event.values[1]));
                            z.setText(String.format("%.3f", event.values[2]));
                        }
                    }

                    @Override
                    public void onAccuracyChanged(Sensor sensor, int accuracy) {

                    }
                };

                switch(type){
                    case Sensor.TYPE_ACCELEROMETER:
                        xLabel.setText("x:");
                        yLabel.setText("y:");
                        zLabel.setText("z:");
                        Xunit.setText("m/s\u00B2");
                        Yunit.setText("m/s\u00B2");
                        Zunit.setText("m/s\u00B2");
                        break;
                    case Sensor.TYPE_LIGHT:
                        xLabel.setText("Luminosity:");
                        Xunit.setText("lx");
                        y.setVisibility(View.GONE);
                        yLabel.setVisibility(View.GONE);
                        Yunit.setVisibility(View.GONE);
                        z.setVisibility(View.GONE);
                        zLabel.setVisibility(View.GONE);
                        Zunit.setVisibility(View.GONE);
                        break;
                    case Sensor.TYPE_MAGNETIC_FIELD:
                        xLabel.setText("x:");
                        yLabel.setText("y:");
                        zLabel.setText("z:");
                        Xunit.setText("\u00B5"+"T");
                        Yunit.setText("\u00B5"+"T");
                        Zunit.setText("\u00B5"+"T");
                        break;
                    case Sensor.TYPE_GYROSCOPE:
                        xLabel.setText("x:");
                        yLabel.setText("y:");
                        zLabel.setText("z:");
                        Xunit.setText("rad/s");
                        Yunit.setText("rad/s");
                        Zunit.setText("rad/s");
                        break;
                    case Sensor.TYPE_PROXIMITY:
                        xLabel.setText("Proximity:");
                        Xunit.setText("cm");
                        y.setVisibility(View.GONE);
                        yLabel.setVisibility(View.GONE);
                        Yunit.setVisibility(View.GONE);
                        z.setVisibility(View.GONE);
                        zLabel.setVisibility(View.GONE);
                        Zunit.setVisibility(View.GONE);
                        break;
                    case Sensor.TYPE_GRAVITY:
                        xLabel.setText("x:");
                        yLabel.setText("y:");
                        zLabel.setText("z:");
                        Xunit.setText("m/s\u00B2");
                        Yunit.setText("m/s\u00B2");
                        Zunit.setText("m/s\u00B2");
                        break;
                    case Sensor.TYPE_LINEAR_ACCELERATION:
                        xLabel.setText("x:");
                        yLabel.setText("y:");
                        zLabel.setText("z:");
                        Xunit.setText("m/s\u00B2");
                        Yunit.setText("m/s\u00B2");
                        Zunit.setText("m/s\u00B2");
                        break;
                    case Sensor.TYPE_ROTATION_VECTOR:
                        xLabel.setText("x:");
                        yLabel.setText("y:");
                        zLabel.setText("z:");
                        break;
                    case Sensor.TYPE_STEP_COUNTER:
                        xLabel.setText("Passos:");
                        y.setVisibility(View.GONE);
                        yLabel.setVisibility(View.GONE);
                        Yunit.setVisibility(View.GONE);
                        z.setVisibility(View.GONE);
                        zLabel.setVisibility(View.GONE);
                        Zunit.setVisibility(View.GONE);
                        break;
                    default:
                        xLabel.setText("x:");
                        yLabel.setText("y:");
                        zLabel.setText("z:");
                        break;
                }
                listeners.add(listener);

                title.setText(DiagnosisToolActivity.sensorName(type));

                linear.addView(child);
            }
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
