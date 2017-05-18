package com.example.j_pc.selsusapp;

import android.app.DownloadManager;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
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
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
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
import java.util.List;

public class SelcompSelectActivity extends AppCompatActivity {
    JSONObject json_data = new JSONObject();
    JSONObject selcomps;
    List<String> listItems = new ArrayList<>();
    ArrayAdapter<String> mAdapter;
    FloatingActionButton qrcode;
    FloatingActionButton nfc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selcomp_select);
        Toolbar mToolbar = (Toolbar) findViewById(R.id.my_titlebar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("");

        TextView title = (TextView) mToolbar.findViewById(R.id.toolbar_title);
        title.setText("SelSus App");

        final TextView mTextView;
        mTextView = (TextView) findViewById(R.id.text);
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);
        String ip = getString(R.string.flask_adress);
        final String url = "http://"+ip+"/systec_panel/service/getSelcompsCloud.php";
        System.out.println(url);
        //"http://192.168.1.75:5000/systec_panel/service/getSelcompsCloud.php";
        // prepare the Request
        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, url, (String)null,
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            json_data = response;
                            selcomps = response.getJSONObject("selcomps");
                            Iterator<?> keys = selcomps.keys();
                            while( keys.hasNext() ) {
                                String key = (String)keys.next();
                                if ( selcomps.get(key) instanceof JSONObject ) {
                                    SelcompSelectActivity.this.listItems.add(key);
                                }
                            }
                            mAdapter.notifyDataSetChanged();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
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

        mAdapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,listItems);
        ListView listView = (ListView) findViewById(R.id.listview);
        listView.setAdapter(mAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String key = ((TextView) view).getText().toString();

                Intent next = new Intent(getApplicationContext(), ModeSelectActivity.class);
                try {
                    next.putExtra("key",key);
                    next.putExtra("selcomp",selcomps.getJSONObject(key).toString());
                    startActivity(next);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

//        int socketTimeout = 10000;//30 seconds - change to what you want
//        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
//        getRequest.setRetryPolicy(policy);
//        queue.add(getRequest);

        // add it to the RequestQueue
        queue.add(getRequest);

        qrcode = (FloatingActionButton) findViewById(R.id.button1);
        nfc = (FloatingActionButton) findViewById(R.id.button2);


        qrcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent next = new Intent(getApplicationContext(), QRReaderActivity.class);
                next.putExtra("selcomps",selcomps.toString());
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
                mAdapter.getFilter().filter(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                mAdapter.getFilter().filter(newText);
                return true;
            }
        });

        return true;
    }

}
