package com.example.j_pc.selsusapp;

import android.content.Context;
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

import java.util.ArrayList;
import java.util.List;

public class DetailsFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        DiagnosisActivity activity = (DiagnosisActivity) getActivity();
        ArrayList<Integer> myData = activity.getSel();
        View view =  inflater.inflate(R.layout.fragment_details, null, false);

        LinearLayout linear = (LinearLayout)view.findViewById(R.id.linearlayout);

        SensorManager manager = ((DiagnosisActivity) getActivity()).mSensorManager;
        for(Integer type : myData){
            Sensor sensor = manager.getDefaultSensor(type);

            String p=Float.toString(sensor.getPower());
            String r=Float.toString(sensor.getResolution());
            String m=Float.toString(sensor.getMaximumRange());

            View child = getActivity().getLayoutInflater().inflate(R.layout.details_card, null);
            CardView card = (CardView)child.findViewById(R.id.card_view);
            card.setUseCompatPadding(true);

            TextView title = (TextView)child.findViewById(R.id.title);
            TextView name = (TextView)child.findViewById(R.id.name);
            TextView fabricant = (TextView)child.findViewById(R.id.fabricant);
            TextView version = (TextView)child.findViewById(R.id.version);
            TextView power = (TextView)child.findViewById(R.id.power);
            TextView resolution = (TextView)child.findViewById(R.id.resolution);
            TextView maxrange = (TextView)child.findViewById(R.id.maxrange);
            TextView Resunit = (TextView)child.findViewById(R.id.Resolutionunit);
            TextView Maxunit = (TextView)child.findViewById(R.id.Maxunit);




            title.setText(DiagnosisToolActivity.sensorName(type));
            name.setText(sensor.getName());
            fabricant.setText(sensor.getVendor());

            version.setText(Integer.toString(sensor.getVersion()));
            power.setText(p);
            resolution.setText(r);
            maxrange.setText(m);

            linear.addView(child);

            switch(type) {
                case Sensor.TYPE_ACCELEROMETER:
                    Resunit.setText("m/s\u00B2");
                    Maxunit.setText("m/s\u00B2");
                    break;
                case Sensor.TYPE_LIGHT:
                    Resunit.setText("lx");
                    Maxunit.setText("lx");
                    break;
                case Sensor.TYPE_MAGNETIC_FIELD:
                    Resunit.setText("\u00B5" + "T");
                    Maxunit.setText("\u00B5" + "T");
                    break;
                case Sensor.TYPE_GYROSCOPE:
                    Resunit.setText("m/s");
                    Maxunit.setText("m/s");
                    break;
                case Sensor.TYPE_PROXIMITY:
                    Resunit.setText("cm");
                    Maxunit.setText("cm");
                    break;
                case Sensor.TYPE_GRAVITY:
                    Resunit.setText("m/s\u00B2");
                    Maxunit.setText("m/s\u00B2");
                    break;
                case Sensor.TYPE_LINEAR_ACCELERATION:
                    Resunit.setText("m/s\u00B2");
                    Maxunit.setText("m/s\u00B2");
                    break;
                default:
                    break;
            }

        }



        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



    }



}
