package com.example.j_pc.selsusapp;

import android.app.DownloadManager;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
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
import java.util.HashMap;
import java.util.Iterator;

public class SelcompSelectActivity extends AppCompatActivity {
    JSONObject json_data = new JSONObject();
    HashMap<Integer,JSONObject> selcomps = new HashMap<>();
    ArrayList<CheckBox> checkboxes = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selcomp_select);
        Toolbar mToolbar = (Toolbar) findViewById(R.id.my_titlebar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("");

        TextView title = (TextView) mToolbar.findViewById(R.id.toolbar_title);
        title.setText("SelComp List");

//        ImageView search_icon = (ImageView) mToolbar.findViewById(R.id.toolbar_video);
//        search_icon.setImageResource(R.drawable.search);
//        search_icon.setVisibility(View.VISIBLE);
//        search_icon.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//            }
//        });

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
                            int minHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 40, getResources().getDisplayMetrics());

                            while( keys.hasNext() ) {
                                String key = (String)keys.next();
                                if ( selcomps.get(key) instanceof JSONObject ) {
                                    final JSONObject sel = (JSONObject) selcomps.get(key);
                                    View child = getLayoutInflater().inflate(R.layout.sensor_check, null);
                                    child.setMinimumHeight(minHeight);
                                    TextView txt = (TextView) child.findViewById(R.id.sensorName);
                                    CheckBox chck = (CheckBox) child.findViewById(R.id.sensorCheckbox);
                                    txt.setId(sel.getInt("selcompID"));
                                    txt.setText(key);
                                    chck.setVisibility(View.GONE);
                                    SelcompSelectActivity.this.selcomps.put(sel.getInt("selcompID"),sel);
                                    child.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Intent next = new Intent(getApplicationContext(), SelcompInfoActivity.class);
                                            next.putExtra("selcomp",sel.toString());
                                            startActivity(next);
                                        }
                                    });
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search, menu);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        return true;
    }

}
