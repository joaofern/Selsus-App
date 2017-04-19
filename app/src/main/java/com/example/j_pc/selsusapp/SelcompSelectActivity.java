package com.example.j_pc.selsusapp;

import android.app.DownloadManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;

public class SelcompSelectActivity extends AppCompatActivity {
    JSONObject json_data = new JSONObject();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selcomp_select);
        Toolbar mToolbar = (Toolbar) findViewById(R.id.my_titlebar);
        TextView title = (TextView) mToolbar.findViewById(R.id.toolbar_title);
        title.setText("SelComp List");


        final TextView mTextView;
        mTextView = (TextView) findViewById(R.id.text);
        final LinearLayout linear = (LinearLayout) findViewById(R.id.linear);
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);
        final String url = "http://10.0.2.2:5000/systec_panel/service/getSelcompsCloud.php";
        // prepare the Request
        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, url, (String)null,
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            json_data = response;
                            JSONObject selcomps = response.getJSONObject("selcomps");
                            Iterator<?> keys = selcomps.keys();

                            while( keys.hasNext() ) {
                                String key = (String)keys.next();
                                if ( selcomps.get(key) instanceof JSONObject ) {
                                    View child = getLayoutInflater().inflate(R.layout.sensor_check, null);
                                    TextView txt = (TextView) child.findViewById(R.id.sensorName);
                                    txt.setText(key);
                                    linear.addView(child);
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        mTextView.setText(error.toString());
                    }
                }
        );

//        int socketTimeout = 10000;//30 seconds - change to what you want
//        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
//        getRequest.setRetryPolicy(policy);
//        queue.add(getRequest);

        // add it to the RequestQueue
        queue.add(getRequest);

        final FloatingActionButton qrcode = (FloatingActionButton) findViewById(R.id.button1);
        final FloatingActionButton nfc = (FloatingActionButton) findViewById(R.id.button2);


        qrcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent next = new Intent(getApplicationContext(), QRReaderActivity.class);
                startActivity(next);

            }
        });
    }

}
