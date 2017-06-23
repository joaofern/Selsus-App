package com.example.j_pc.selsusapp;
    import android.content.Context;
    import android.content.Intent;
    import android.graphics.Color;
    import android.hardware.Sensor;
    import android.hardware.SensorManager;
    import android.net.Uri;
    import android.support.design.widget.TabLayout;
    import android.support.v4.app.Fragment;
    import android.support.v4.app.FragmentManager;
    import android.support.v4.app.FragmentPagerAdapter;
    import android.support.v4.view.ViewPager;
    import android.support.v4.widget.DrawerLayout;
    import android.support.v7.app.ActionBarDrawerToggle;
    import android.support.v7.app.AppCompatActivity;
    import android.os.Bundle;
    import android.support.v7.widget.Toolbar;
    import android.view.LayoutInflater;
    import android.view.View;
    import android.view.ViewGroup;
    import android.widget.TextView;

    import com.google.android.gms.appindexing.Action;
    import com.google.android.gms.appindexing.AppIndex;
    import com.google.android.gms.appindexing.Thing;
    import com.google.android.gms.common.api.GoogleApiClient;
    import com.jjoe64.graphview.GraphView;
    import com.jjoe64.graphview.series.DataPoint;
    import com.jjoe64.graphview.series.LineGraphSeries;

    import java.util.ArrayList;
    import java.util.HashMap;
    import java.util.List;
    import java.util.Random;

public class DiagnosisActivity extends AppCompatActivity {

    Toolbar mToolbar;
    TabLayout tabLayout;
    ViewPager viewPager;
    ArrayList<Integer> sel = new ArrayList<>();
    SensorManager mSensorManager;
    List<GraphView> mGraphs;
    HashMap<Integer, List<List<DataPoint>>> capture;

    int mode;

    LineGraphSeries series = new LineGraphSeries();
    int lastX = 0;
    final Random RANDOM = new Random();
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information);

        mToolbar = (Toolbar) findViewById(R.id.my_titlebar);
        TextView title = (TextView) mToolbar.findViewById(R.id.toolbar_title);
        title.setText("Diagnosis Tool");

        viewPager = (ViewPager) findViewById(R.id.viewPager);
        viewPager.setAdapter(new CustomAdapter(getSupportFragmentManager(), getApplicationContext()));

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        Intent intent = this.getIntent();
        mode = intent.getIntExtra("mode",1);
        if (intent != null) sel = intent.getExtras().getIntegerArrayList("selected");
        else System.out.println("FAIL!!!!");

        mGraphs = new ArrayList<>(sel.size());
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Diagnosis Page")
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }


    @Override
    protected void onRestart() {
        super.onRestart();
        finish();
        startActivity(getIntent());
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

    private class CustomAdapter extends FragmentPagerAdapter {
        private String fragments[] = {"Info", "Details", "Graph"};

        public CustomAdapter(FragmentManager supportFragmentManager, Context applicationContext) {
            super(supportFragmentManager);
        }



        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new InfoFragment();
                case 1:
                    return new DetailsFragment();
                case 2:
                    return new GraphFragment();
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return fragments.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return fragments[position];
        }


    }

    public ArrayList<Integer> getSel(){
        return sel;
    }
    public int getMode() { return mode; }

}
