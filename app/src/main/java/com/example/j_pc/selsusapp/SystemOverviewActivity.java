package com.example.j_pc.selsusapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

public class SystemOverviewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_system_overview);



        Toolbar mToolbar = (Toolbar) findViewById(R.id.my_titlebar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("");

        TextView title = (TextView) mToolbar.findViewById(R.id.toolbar_title);
        title.setText("SelSus App");

        LinearLayout linear = (LinearLayout) findViewById(R.id.linear);
        Intent intent = getIntent();


        View child = getLayoutInflater().inflate(R.layout.selcomp_info_card, null);
        CardView card = (CardView)child.findViewById(R.id.card_view);
        card.setUseCompatPadding(true);

        TextView sensorID = (TextView)child.findViewById(R.id.selcompID);
        TextView state = (TextView)child.findViewById(R.id.state);
        TextView ip = (TextView)child.findViewById(R.id.ip);
        TextView mac = (TextView)child.findViewById(R.id.mac);
        TextView port = (TextView)child.findViewById(R.id.port);


        try {
            JSONObject selcomp = new JSONObject(intent.getStringExtra("selcomp"));
            sensorID.setText(selcomp.getString("selcompID"));
            state.setText(selcomp.getString("state"));
            ip.setText(selcomp.getString("ip"));
            mac.setText(selcomp.getString("mac"));
            port.setText(selcomp.getString("port"));

        } catch (JSONException e) {
            e.printStackTrace();
        }

        linear.addView(child);





    }
}
