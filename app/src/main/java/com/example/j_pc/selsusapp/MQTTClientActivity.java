package com.example.j_pc.selsusapp;


import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.Viewport;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.Series;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.SQLOutput;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

public class MQTTClientActivity extends AppCompatActivity {
    Toolbar mToolbar;
    final String serverUri = "tcp://192.168.0.101:1883";
    public MqttAndroidClient mqttAndroidClient = null;
    LineGraphSeries<DataPoint> series,series1,series2;

    int  mode;
    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mqtt_client);

        mToolbar = (Toolbar) findViewById(R.id.my_titlebar);
        TextView title = (TextView) mToolbar.findViewById(R.id.toolbar_title);
        title.setText("Selcomp Overview");

        LinearLayout linear = (LinearLayout) findViewById(R.id.linearlayout);
        final Intent intent = getIntent();
        mode = intent.getIntExtra("mode", 0);
        String card_title = intent.getStringExtra("sel_name");

        View child = this.getLayoutInflater().inflate(R.layout.graph_card, null);
        CardView card = (CardView) child.findViewById(R.id.card_view);
        card.setUseCompatPadding(true);

        final GraphView graph = (GraphView) child.findViewById(R.id.graph);

        series = new LineGraphSeries<>(new DataPoint[]{
        });
        series1 = new LineGraphSeries<>(new DataPoint[]{
        });
        series2 = new LineGraphSeries<>(new DataPoint[]{
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
        viewport.setMinY(0);
        viewport.setMaxY(50);
        viewport.setMinX(0);
        viewport.setMaxX(10);

        graph.getGridLabelRenderer().setGridStyle(GridLabelRenderer.GridStyle.HORIZONTAL);
        graph.getGridLabelRenderer().setHorizontalLabelsVisible(false);

        final TextView lable1 = (TextView) child.findViewById(R.id.lable1);
        final TextView lable2 = (TextView) child.findViewById(R.id.lable2);

        lable1.setText("0s");
        lable2.setText("10s");

        final TextView title1 = (TextView) child.findViewById(R.id.title);
        title1.setText(card_title);
        final CheckBox check = (CheckBox) child.findViewById(R.id.sensorCheckbox);
        check.setVisibility(View.GONE);

        linear.addView(child);

        //mqtt

        mqttAndroidClient = new MqttAndroidClient(this.getApplicationContext(), serverUri, "123-1312-13123-131323");
        mqttAndroidClient.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {
                System.out.println("Connection was lost!");

            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {

                List<Series> series = graph.getSeries();
                System.out.println("Message Arrived!: " + topic + ": " + new String(message.getPayload()));
                JSONObject jsOBj = new JSONObject(new String(message.getPayload()));

                Double x, y;
                String aux = jsOBj.getString(jsOBj.keys().next());
                y = Double.parseDouble(aux.substring(1, aux.length()-1));
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS");
                Date parsedDate = dateFormat.parse(jsOBj.keys().next());
                Timestamp ts = new java.sql.Timestamp(parsedDate.getTime());


                String a = String.valueOf(ts.getTime());

                x=Double.parseDouble(a)/1000d;


                Timestamp ts1 = new Timestamp(System.currentTimeMillis());
                String b = String.valueOf(ts1.getTime());


                int minDelay =5; //milliseconds
                int toKeep = 1000;
                if(minDelay!=0) //is zero in some sensors like light
                    toKeep = 20000 / minDelay; //10 seconds
                DataPoint dpX = new DataPoint(x,y);
                ((LineGraphSeries<DataPoint>)series.get(0)).appendData(dpX, true, toKeep);

            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {
                System.out.println("Delivery Complete!");
            }
        });

        try {
            mqttAndroidClient.connect(null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    System.out.println("Connection Success!");
                    try {
                        System.out.println("Subscrided topic /selcomp/selompID");
                        //mqttAndroidClient.subscribe("ecava/igx/temp", 0);5185
                        mqttAndroidClient.subscribe("/selcomp/115591",0);
                    } catch (MqttException ex) {
                        System.out.println("FAIL");
                    }

                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    System.out.println("Connection Failure!" + exception.toString());
                    try {
                        JSONObject jsOBj = new JSONObject("{\"2017-06-23 14:21:09.826\": \"[9]\"}");
                        System.out.println(jsOBj.keys().next());
                        String aux = jsOBj.getString(jsOBj.keys().next());
                        System.out.println(aux.substring(1, aux.length()-1));


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (MqttException ex) {
            System.out.println("WTFFF");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            mqttAndroidClient.disconnect();
        } catch (MqttException e) {
            e.printStackTrace();
            System.out.println("Couldn't disconnect!");
        }
    }
}
