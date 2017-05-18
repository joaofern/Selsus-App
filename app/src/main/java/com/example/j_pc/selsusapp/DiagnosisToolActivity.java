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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class DiagnosisToolActivity extends AppCompatActivity {

    Toolbar mToolbar;
    ArraySet<Integer> sensorTypes = new ArraySet<>();
    ArraySet<Integer> filtered = new ArraySet<>();
    private SensorManager mSensorManager;
    private Button button;
    ArrayList<Integer> selected = new ArrayList<>();
    HashMap<Integer,CheckBox> checkboxes = new HashMap<>();
    private Intent intent;
    int mode;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    static final int REQUEST_VIDEO_CAPTURE = 1;
    static final int REQUEST_IMAGE_CAPTURE = 2;

    private void dispatchTakeVideoIntent() {
        Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        if (takeVideoIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takeVideoIntent, REQUEST_VIDEO_CAPTURE);
        }
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diagnosis_tool);

        mToolbar = (Toolbar) findViewById(R.id.my_titlebar);
        TextView title = (TextView) mToolbar.findViewById(R.id.toolbar_title);
        title.setText("Diagnosis Tool");

        ImageView picture_button = (ImageView) mToolbar.findViewById(R.id.toolbar_picture);
        picture_button.setVisibility(View.VISIBLE);
        picture_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }
        });

        ImageView video_button = (ImageView) mToolbar.findViewById(R.id.toolbar_video);
        video_button.setVisibility(View.VISIBLE);
        video_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakeVideoIntent();
            }
        });

        intent = getIntent();

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        List<Sensor> deviceSensors = mSensorManager.getSensorList(Sensor.TYPE_ALL);
        LinearLayout linear = (LinearLayout) findViewById(R.id.linear);

        for (Sensor sensor : deviceSensors) {

            if(sensorName(sensor.getType())!=null) {
                sensorTypes.add(sensor.getType());
            }

        }


        //Check sensors in recipe

        String selcomps_string = intent.getStringExtra("selcomps");
        mode = intent.getIntExtra("mode",1);

        try {
            JSONObject selcomps = new JSONObject(selcomps_string);

            JSONObject recipes = selcomps.getJSONObject("recipes");

            Iterator<?> keys = recipes.keys();
            while( keys.hasNext() ) {
                String key = (String)keys.next();
                if ( recipes.get(key) instanceof JSONObject ) {
                    JSONObject recipe_variables = recipes.getJSONObject(key).getJSONObject("variables");
                    JSONArray test = recipe_variables.names();
                    String first_var = (String)test.get(0);
                    String accelerometer = "Accelerometer";

                    //Check if sensor is in sensor recipes
                    if(first_var.toLowerCase().contains(accelerometer.toLowerCase())){
                        filtered.add(1);
                    }
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        if(mode==2) sensorTypes = filtered;

        for (int type : sensorTypes) {

            View child = getLayoutInflater().inflate(R.layout.sensor_check, null);
            TextView txt = (TextView) child.findViewById(R.id.sensorName);
            txt.setText(sensorName(type));
            final CheckBox check = (CheckBox) child.findViewById(R.id.sensorCheckbox);
            //checkboxes.put(type,check);
            check.setId(type);
            linear.addView(child);
        }


        final Button button = (Button) findViewById(R.id.next);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent next = new Intent(getApplicationContext(), DiagnosisActivity.class);
                selected.clear();
                for(int type : sensorTypes) {
                    if(((CheckBox) findViewById(type)).isChecked())
                        selected.add(type);
                }
                next.putExtra("selected", selected);
                next.putExtra("mode",mode);
                startActivity(next);
            }
        });


        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");

            //Convert to byte array
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] byteArray = stream.toByteArray();

            Intent in1 = new Intent(this, CameraResultsActivity.class);
            in1.putExtra("image",byteArray);

            startActivity(in1);
        }
        else if(requestCode == REQUEST_VIDEO_CAPTURE && resultCode == RESULT_OK){
            Uri videoUri = data.getData();

            Intent in2 = new Intent(this, VideoResultsActivity.class);
            in2.putExtra("imageUri", videoUri);
            startActivity(in2);

//            mVideoView.setVideoURI(videoUri);
        }
    }

    static public String sensorName(int type) {
        switch (type) {
            case Sensor.TYPE_ACCELEROMETER:
                return "Accelerometer";
            case Sensor.TYPE_AMBIENT_TEMPERATURE:
                return "Temperature";
            case Sensor.TYPE_GRAVITY:
                return "Gravity";
            case Sensor.TYPE_GYROSCOPE:
                return "Gyroscope";
            case Sensor.TYPE_LIGHT:
                return "Light";
            case Sensor.TYPE_LINEAR_ACCELERATION:
                return "Linear Acceleration";
            case Sensor.TYPE_MAGNETIC_FIELD:
                return "Magnetic Field";
            case Sensor.TYPE_PRESSURE:
                return "Pressure";
            case Sensor.TYPE_PROXIMITY:
                return "Proximity";
            case Sensor.TYPE_RELATIVE_HUMIDITY:
                return "Relative Humidity";
            case Sensor.TYPE_ROTATION_VECTOR:
                return "Rotation vector";
            default:
                return null;
        }
    }


    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("DiagnosisTool Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }
}
