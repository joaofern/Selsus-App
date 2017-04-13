package com.example.j_pc.selsusapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.widget.ImageView;
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
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class CameraResultsActivity extends AppCompatActivity {
    String message, date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_results);

        LinearLayout linear = (LinearLayout) findViewById(R.id.linearlayout);

        Toolbar mToolbar;
        mToolbar = (Toolbar) findViewById(R.id.my_titlebar);
        TextView title = (TextView) mToolbar.findViewById(R.id.toolbar_title);
        TextView tit = (TextView) findViewById(R.id.title);

        ImageView img = (ImageView) findViewById(R.id.picture);
        title.setText("Diagnosis Tool");
        tit.setText("Camera results");

        Intent intent = this.getIntent();
        if (intent != null) {
            byte[] byteArray = getIntent().getByteArrayExtra("image");
            Bitmap bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
            img.setImageBitmap(bmp);

        }

        else System.out.println("FAIL!!!!");

        final TextView comment = (TextView) findViewById(R.id.comment);
        final EditText edit = (EditText) findViewById(R.id.mytextText);

        // Add Photo Date
        final String DATE_FORMAT_NOW = "HH:mm:ss dd-MM-yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_NOW);
        TextView time = (TextView) findViewById(R.id.time);
        Calendar c = Calendar.getInstance();
        time.setText(sdf.format(c.getTime()));
        date = sdf.format(c.getTime());

        // Add comment listener
        comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(edit.getVisibility()==View.GONE){
                   comment.setText("Save comment");
                    edit.setVisibility(View.VISIBLE);
                }
                else{
                    edit.setVisibility(View.GONE);
                    comment.setText("Saved!");
                    message = edit.getText().toString();
                    // Close Android Keyboard
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        });

        Button btn = (Button) findViewById(R.id.next);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //enviar POST com o bitmap e a data
                //message String
                //sate String
            }
        });

    }
}
