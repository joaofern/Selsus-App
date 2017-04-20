package com.example.j_pc.selsusapp;

import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

public class SelcompInfoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selcom_info);

        Toolbar mToolbar = (Toolbar) findViewById(R.id.my_titlebar);
        TextView title = (TextView) mToolbar.findViewById(R.id.toolbar_title);
        title.setText("SelComp Info");



        String response = getIntent().getStringExtra("selcomps");
//        try {
//            //JSONObject json = new JSONObject(response);
//
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }


    }
}
