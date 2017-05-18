package com.example.j_pc.selsusapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;
import java.util.Objects;

public class CloudGraphResultsActivity extends AppCompatActivity {
    private Intent intent;
    private ArrayList<Integer> selected = new ArrayList<>();
    private ArrayList<String> sensor_names = new ArrayList<>();
    private String from,to, sensor_name;
    private JSONObject json_data = new JSONObject();
    View child;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cloud_graph_results);
        child = this.getLayoutInflater().inflate(R.layout.graph_card, null);
        final LinearLayout linear = (LinearLayout) findViewById(R.id.linearlayout);

        Toolbar mToolbar;
        mToolbar = (Toolbar) findViewById(R.id.my_titlebar);
        TextView title = (TextView) mToolbar.findViewById(R.id.toolbar_title);
        title.setText("System Overview");

        intent = getIntent();
        selected = intent.getIntegerArrayListExtra("selected");
        sensor_names = intent.getStringArrayListExtra("sensor_names");
        from = intent.getStringExtra("from");
        to = intent.getStringExtra("to");
        sensor_name = intent.getStringExtra("sensor_name");

        //Percorrer sensorID - 1 request por grafico

        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(getBaseContext());
        String ip = getString(R.string.flask_adress);
        final String url = "http://"+ip+"/systec_panel/service/getDataCloud.php";
        System.out.println(url);
        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, url, (String)null,
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response) {
                        json_data = response;

                        CardView card = (CardView)child.findViewById(R.id.card_view);
                        card.setUseCompatPadding(true);
                        final GraphView graph = (GraphView) child.findViewById(R.id.graph);
                        graph.setTitle(" ");
                        final TextView tit = (TextView)child.findViewById(R.id.title);
                        tit.setTextSize(20);
                        tit.setText(sensor_name);
                        final CheckBox check = (CheckBox)child.findViewById(R.id.sensorCheckbox);
                        check.setVisibility(View.GONE);

                        final TextView lable1 = (TextView)child.findViewById(R.id.lable1);
                        final TextView lable2 = (TextView)child.findViewById(R.id.lable2);

                        Date date;
                        Iterator<?> keys = response.keys();
                        LineGraphSeries<DataPoint> series = new LineGraphSeries<>(new DataPoint[] {
                        });
                        int trick = 1;
                        String initial_date = "";
                        String final_date = "";
                        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-DD HH:mm:ss.SSS");

                        while( keys.hasNext() ) {
                            String key = (String)keys.next();

                            try {
                                date = format.parse(key.toString());
                                if(trick==1){
                                    initial_date = key.toString();
                                }
                                final_date = key.toString();
                                try {
                                    String[] arr = response.get(key).toString().split("");

                                    //1 line
                                    if(arr.length==6){
                                        String str = arr[2] + arr[3] + arr[4];
                                        Double dbl = Double.parseDouble(str);
                                        DataPoint dp = new DataPoint(date,dbl);

                                        series.appendData(dp,true,100000);
                                    }
                                    //2 lines
                                    else if(arr.length==9){

                                    }
                                    //3 lines
                                    else if(arr.length==12){

                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        }

                        int startIndex = initial_date.indexOf("2");
                        int endIndex = initial_date.indexOf(" ");
                        String replacement = "";

                        String toBeReplaced = initial_date.substring(startIndex, endIndex);
                        String toBeReplaced1 = final_date.substring(startIndex, endIndex);

                        lable1.setText(initial_date.replace(toBeReplaced, replacement));
                        lable2.setText(final_date.replace(toBeReplaced1, replacement));


                        graph.getGridLabelRenderer().setGridStyle(GridLabelRenderer.GridStyle.HORIZONTAL);
                        graph.getGridLabelRenderer().setHorizontalLabelsVisible(false);
                        graph.addSeries(series);
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        System.out.println(error.toString());
                    }
                }
        );

        // add it to the RequestQueue
        linear.addView(child);
        queue.add(getRequest);


    }
}
