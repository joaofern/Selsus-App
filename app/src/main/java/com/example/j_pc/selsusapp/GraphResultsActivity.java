package com.example.j_pc.selsusapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.SystemClock;
import android.support.annotation.NonNull;
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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.crystal.crystalrangeseekbar.interfaces.OnRangeSeekbarChangeListener;
import com.crystal.crystalrangeseekbar.widgets.CrystalRangeSeekbar;
import com.google.android.gms.vision.text.Text;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.Viewport;
import com.jjoe64.graphview.helper.StaticLabelsFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

public class GraphResultsActivity extends AppCompatActivity {

    ArrayList<Integer> sel = new ArrayList<>();
    HashMap<Integer, List <List<DataPoint>>> capture = new HashMap<>();
    HashMap<Integer, List <List<DataPoint>>> toStore = new HashMap<>();
    HashMap<Integer, String> comments = new HashMap<>();
    HashMap<Integer,CheckBox> checkBoxes = new HashMap<>();
    HashMap<Integer,TextView> commentTextViews = new HashMap<>();
    HashMap<Integer,EditText> commentInput = new HashMap<>();
    String itemDateStr1;
    String itemDateStr2;
    Double min,max;
    int mode;

    SensorManager mSensorManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph_results);

        LinearLayout linear = (LinearLayout) findViewById(R.id.linearlayout);

        Toolbar mToolbar;
        mToolbar = (Toolbar) findViewById(R.id.my_titlebar);
        TextView title = (TextView) mToolbar.findViewById(R.id.toolbar_title);
        title.setText("Capture Results");

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        final Button next = (Button) findViewById(R.id.next);

        Intent intent = this.getIntent();
        if (intent != null) {
            sel = intent.getExtras().getIntegerArrayList("selected");
            capture = GraphFragment.capture;
            mode = intent.getIntExtra("mode",0);
            for(final Integer type : sel){
                View child = this.getLayoutInflater().inflate(R.layout.graph_card2, null);
                CardView card = (CardView)child.findViewById(R.id.card_view);
                card.setUseCompatPadding(true);
                final GraphView graph = (GraphView) child.findViewById(R.id.graph);
                final TextView tit = (TextView)child.findViewById(R.id.title);
                final CheckBox check = (CheckBox)child.findViewById(R.id.sensorCheckbox);
                checkBoxes.put(type,check);

                final TextView com = (TextView) child.findViewById(R.id.comment);
                commentTextViews.put(type,com);

                final EditText et = (EditText) child.findViewById(R.id.mytextText);
                commentInput.put(type,et);

                final TextView sensorName = (TextView)child.findViewById(R.id.title);

                final TextView lable1 = (TextView)child.findViewById(R.id.lable1);
                final TextView lable2 = (TextView)child.findViewById(R.id.lable2);



                sensorName.setText(DiagnosisToolActivity.sensorName(type));


                LineGraphSeries<DataPoint> series = new LineGraphSeries<>(new DataPoint[] {
                });
                LineGraphSeries<DataPoint> series1 = new LineGraphSeries<>(new DataPoint[] {
                });
                LineGraphSeries<DataPoint> series2 = new LineGraphSeries<>(new DataPoint[] {
                });

                //Add DataPoints
                System.out.println("to keep2: " + capture.get(type).get(0).size());
                Integer toKeep = capture.get(type).get(0).size();
                int a = 0;
                for(DataPoint dp : capture.get(type).get(0)){
                    a++;
                    series.appendData(dp,true,toKeep);
                }
                for(DataPoint dp : capture.get(type).get(1)){
                    series1.appendData(dp,true,toKeep);
                }
                for(DataPoint dp : capture.get(type).get(2)){
                    series2.appendData(dp,true,toKeep);
                }
                System.out.println("A: " + a);

                int tam = capture.get(type).get(0).size();

                Double ts1 = (capture.get(type).get(0).get(0).getX());
                Double ts2 = (capture.get(type).get(0).get(tam-1).getX());

                Date itemdate1 = convertTimestamp(ts1);
                Date itemdate2 = convertTimestamp(ts2);

                itemDateStr1 = new SimpleDateFormat("HH:mm:ss.SS").format(itemdate1);
                itemDateStr2 = new SimpleDateFormat("HH:mm:ss.SS").format(itemdate2);

                lable1.setText(itemDateStr1);
                lable2.setText(itemDateStr2);

                series.setColor(Color.BLUE);
                series1.setColor(Color.GREEN);
                series2.setColor(Color.RED);

                graph.addSeries(series);
                graph.addSeries(series1);
                graph.addSeries(series2);

                graph.getGridLabelRenderer().setGridStyle(GridLabelRenderer.GridStyle.HORIZONTAL);
                graph.getGridLabelRenderer().setHorizontalLabelsVisible(false);

                final Sensor sensor = mSensorManager.getDefaultSensor(type);

                Viewport viewport = graph.getViewport();
                viewport.setYAxisBoundsManual(true);
                viewport.setXAxisBoundsManual(true);
                viewport.setMinY(0- sensor.getMaximumRange());
                viewport.setMaxY(sensor.getMaximumRange());

                // get seekbar from view
                final CrystalRangeSeekbar rangeSeekbar = (CrystalRangeSeekbar) child.findViewById(R.id.rangeSeekbar1);
                rangeSeekbar.setScaleX(0.75f);
                rangeSeekbar.setScaleY(0.75f);

                // get min and max text view
                final TextView tvMin = (TextView) child.findViewById(R.id.lable1);
                final TextView tvMax = (TextView) child.findViewById(R.id.lable2);


                // set listener
                rangeSeekbar.setOnRangeSeekbarChangeListener(new OnRangeSeekbarChangeListener() {
                    @Override
                    public void valueChanged(Number minValue, Number maxValue) {
                        float percent = ((capture.get(type).get(0).size()-1)/100f);
                        min = capture.get(type).get(0).get((int)(minValue.intValue()*percent)).getX();
                        max = capture.get(type).get(0).get((int)(maxValue.intValue()*percent)).getX();
                        itemDateStr1 = new SimpleDateFormat("HH:mm:ss.SS").format(convertTimestamp(min));
                        itemDateStr2 = new SimpleDateFormat("HH:mm:ss.SS").format(convertTimestamp(max));
                        tvMin.setText(itemDateStr1);
                        tvMax.setText(itemDateStr2);
                    }
                });
                linear.addView(child);

                List<List<DataPoint>> lst = capture.get(type);
                List<List<DataPoint>> lst_store = new ArrayList<>();
                //por cada axis
                for(int i=0;i<lst.size();i++){
                    List<DataPoint> dps = new ArrayList<>();
                    try {
                        dps = setTimeInterval(lst.get(i),min,max);
                        lst_store.add(dps);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
                toStore.put(type,lst_store);
            }
            try {
                if(mode==2) sendDataSelcomp(toStore.get(1));
                else if(mode==1) sendDataCloud(toStore.get(1));
            } catch (ParserConfigurationException e) {
                e.printStackTrace();
            } catch (TransformerConfigurationException e) {
                e.printStackTrace();
            } catch (TransformerException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            commentListener();
        }
        else System.out.println("FAIL!!!!");
    }

    public void sendDataCloud(List<List<DataPoint>> capture) throws ParserConfigurationException, TransformerException {
        ArrayList<String> str = new ArrayList<>();
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

        // root elements
        Document doc = docBuilder.newDocument();
        Element rootElement = doc.createElement("measurementdata");


        Attr attr3 = doc.createAttribute("xmlns");
        attr3.setValue("http://selsus.eu/sensorData");
        rootElement.setAttributeNode(attr3);
        doc.appendChild(rootElement);

        Element service = doc.createElement("service");
        rootElement.appendChild(service);

        Element ele_servicetype = doc.createElement("servicetype");
        ele_servicetype.appendChild(doc.createTextNode("ObservationService"));
        service.appendChild(ele_servicetype);

        //device_id
        Element ele_deviceid = doc.createElement("deviceid");
        ele_deviceid.appendChild(doc.createTextNode("13452"));
        service.appendChild(ele_deviceid);

        Element cycle = doc.createElement("cycle");
        service.appendChild(cycle);

        Attr number = doc.createAttribute("number");
        number.setValue("1");
        cycle.setAttributeNode(number);

        for (int i = 0; i < capture.get(0).size(); i++) {
            String var = "{"+ capture.get(0).get(i).getY() +","+capture.get(1).get(i).getY()+","+capture.get(2).get(i).getY()+"}";
            String time = ""+capture.get(0).get(i).getX();

            Element dataset = doc.createElement("dataset");
            cycle.appendChild(dataset);

            Element data1 = doc.createElement("data");
            dataset.appendChild(data1);

            Attr id1 = doc.createAttribute("id");
            id1.setValue("measurement");
            data1.setAttributeNode(id1);

            data1.appendChild(doc.createTextNode(var));

            Element data2 = doc.createElement("data");
            dataset.appendChild(data2);

            Attr id2 = doc.createAttribute("id");
            id2.setValue("timestamp");
            data2.setAttributeNode(id2);

            data2.appendChild(doc.createTextNode(time));
        }

        // write the content into xml string
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        DOMSource source = new DOMSource(doc);
        final StringWriter writer = new StringWriter();
        StreamResult result = new StreamResult(writer);
        transformer.transform(source, result);

        String selcomp_ip = "192.168.0.101:57681";
        String service1 = "http://172.30.20.143:5000/systec_panel/service/payload";

        final String body = writer.toString();
        String ip = getString(R.string.flask_adress);
        final String url = "http://" + selcomp_ip + "/WebServiceTest.asmx?WSDL/parseRecipe";


        System.out.println(writer.toString());



        // Create the request queue
        RequestQueue queue = Volley.newRequestQueue(this);

        // Create the request object
        StringRequest stringRequest = new StringRequest(Request.Method.POST, service1,
                new Response.Listener() {

                    @Override
                    public void onResponse(Object response) {
                        System.out.println("RESPONSE: " + response.toString());
                    }

                },
                new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO handle the error
                    }

                }
        ) {

            protected Map<String, String> getParams() throws com.android.volley.AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("payload", writer.toString());
                return params;
            };

            @Override
            public String getBodyContentType() {
                return "text/xml; charset=" +
                        getParamsEncoding();
            }

            @Override
            public byte[] getBody() throws AuthFailureError {
                String postData = body;
                try {
                    return postData == null ? null :
                            postData.getBytes(getParamsEncoding());
                } catch (UnsupportedEncodingException uee) {
                    // TODO consider if some other action should be taken
                    return null;
                }
            }

        };

        // Schedule the request on the queue
        queue.add(stringRequest);
    }

    public void sendDataSelcomp(List<List<DataPoint>> capture) throws ParserConfigurationException, TransformerException, IOException {
        ArrayList<String> str = new ArrayList<>();
        for (int i = 0; i < capture.size(); i++) {
            String var = "{";
            String time = "{";
            for (int j = 0; j < capture.get(i).size(); j++) {
                var = var + capture.get(i).get(j).getY();
                time = time + capture.get(i).get(j).getX();
                if (j != capture.get(i).size() - 1) {
                    var += ", ";
                    time += ", ";
                }
            }
            var = var + "}";
            time = time + "}";
            str.add(var);
            str.add(time);
        }

        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

        // root elements
        Document doc = docBuilder.newDocument();
        Element rootElement = doc.createElement("recipeadjustment");

        // set attribute to staff element
        Attr attr = doc.createAttribute("name");
        attr.setValue("ProcessAdjustment");
        rootElement.setAttributeNode(attr);
        doc.appendChild(rootElement);

        // servicetype elements
        Element ele_servicetype = doc.createElement("servicetype");
        ele_servicetype.appendChild(doc.createTextNode("OBSERVATION"));
        rootElement.appendChild(ele_servicetype);

        //device_id
        Element ele_deviceid = doc.createElement("deviceid");
        ele_deviceid.appendChild(doc.createTextNode("13452"));
        rootElement.appendChild(ele_deviceid);

        //variables
        Element variables = doc.createElement("variables");
        rootElement.appendChild(variables);

        //variable1
        Element variable1 = doc.createElement("variable");
        variable1.appendChild(doc.createTextNode(str.get(0)));
        Attr attr1 = doc.createAttribute("name");
        attr1.setValue("Accelerometer1");
        variable1.setAttributeNode(attr1);
        variables.appendChild(variable1);

        //variable2
        Element variable2 = doc.createElement("variable");
        variable2.appendChild(doc.createTextNode(str.get(2)));
        Attr attr2 = doc.createAttribute("name");
        attr2.setValue("Accelerometer2");
        variable2.setAttributeNode(attr2);
        variables.appendChild(variable2);

        //variable2
        Element variable3 = doc.createElement("variable");
        variable3.appendChild(doc.createTextNode(str.get(4)));
        Attr attr3 = doc.createAttribute("name");
        attr3.setValue("Accelerometer3");
        variable3.setAttributeNode(attr3);
        variables.appendChild(variable3);

        //timestamp
        Element times = doc.createElement("variable");
        times.appendChild(doc.createTextNode(str.get(1)));
        Attr attr4 = doc.createAttribute("name");
        attr4.setValue("timestamp");
        times.setAttributeNode(attr4);
        variables.appendChild(times);

        // write the content into xml string
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        DOMSource source = new DOMSource(doc);
        final StringWriter writer = new StringWriter();
        StreamResult result = new StreamResult(writer);
        transformer.transform(source, result);

        String selcomp_ip = "192.168.0.101:57681";
        String service = "http://172.30.20.143:5000/systec_panel/service/recipeAdjustment";

        final String body = writer.toString();
        String ip = getString(R.string.flask_adress);
        final String url = "http://" + selcomp_ip + "/WebServiceTest.asmx?WSDL/parseRecipe";


        System.out.println(writer.toString());



        // Create the request queue
        RequestQueue queue = Volley.newRequestQueue(this);

        // Create the request object
        StringRequest stringRequest = new StringRequest(Request.Method.POST, service,
                new Response.Listener() {

                    @Override
                    public void onResponse(Object response) {
                        System.out.println("RESPONSE: " + response.toString());
                    }

                },
                new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO handle the error
                    }

                }
        ) {

            protected Map<String, String> getParams() throws com.android.volley.AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("recipe", writer.toString());
                return params;
            };

            @Override
            public String getBodyContentType() {
                return "text/xml; charset=" +
                        getParamsEncoding();
            }

            @Override
            public byte[] getBody() throws AuthFailureError {
                String postData = body;
                try {
                    return postData == null ? null :
                            postData.getBytes(getParamsEncoding());
                } catch (UnsupportedEncodingException uee) {
                    // TODO consider if some other action should be taken
                    return null;
                }
            }

        };

        // Schedule the request on the queue
        queue.add(stringRequest);
    }


    public void commentListener() {
            for(final Integer type : sel){
                commentTextViews.get(type).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(commentInput.get(type).getVisibility()==View.GONE){
                            commentTextViews.get(type).setText("Save comment");
                            commentInput.get(type).setVisibility(View.VISIBLE);
                        }
                        else{
                            commentInput.get(type).setVisibility(View.GONE);
                            commentTextViews.get(type).setText("Saved!");
                            comments.put(type,commentInput.get(type).getText().toString());

                            //Close Android Keyboard
                            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                        }
                    }
                });
            }
    }


    public Date convertTimestamp(Double a){
        long timeInMillis = (new Date()).getTime()
                + ((long)(a*1000000d) - SystemClock.elapsedRealtimeNanos()) / 1000000L;
        Date itemdate = new Date(timeInMillis);
        return itemdate;
    }

    public List<DataPoint> setTimeInterval(List<DataPoint> capture,Double startdate, Double enddate) throws ParseException {
        List<DataPoint> store  = new ArrayList<>();

        for(DataPoint dp : capture){
            Date date = convertTimestamp(dp.getX());

            Date start = convertTimestamp(min);
            Date end = convertTimestamp(max);

            if(date.after(start) & date.before(end)){
                DataPoint new_dp = new DataPoint(dp.getX(),dp.getY());
                store.add(new_dp);
            }
        }

        return store;
    }


}
